package be.pxl.services.service;

import be.pxl.services.client.ReviewClient;
import be.pxl.services.domain.Category;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.State;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    @Autowired
    private ObjectMapper objectMapper;

    private final PostRepository postRepository;
    private final ReviewClient reviewClient;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @RabbitListener(queues = "reviewQueue")
    public void listen(String in) {
        logger.info("Message read from reviewQueue : {}", in);

        try {
            Map review = objectMapper.readValue(in, Map.class);
            Integer postIdInt = (Integer) review.get("postId");
            Integer reviewerIdInt = (Integer) review.get("reviewerId");

            String reviewType = (String) review.get("reviewType");
            if (reviewType.equals("REJECTION") || reviewType.equals("APPROVAL")) {
                logger.info("Updating state to after {} of post with id {}", reviewType, postIdInt);

                State[] validStates = {State.SUBMITTED};
                Post post = checksToUpdatePost(postIdInt.longValue(), reviewerIdInt.longValue(), false, validStates);

                State newState = reviewType.equals("APPROVAL") ? State.APPROVED : State.REJECTED;
                logger.info("New state is {}", newState);
                post.setState(newState);
                postRepository.save(post);
            }

        } catch (Exception e) {
            logger.error("Error occurred: {}", e.getMessage());
        }
    }

    @Override
    public List<PostResponse> findAllPosts() {
        logger.info("Getting all posts");
        return postRepository.findAll()
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public PostResponse findPostById(Long id) {
        logger.info("Getting post by id {}", id);
        return postRepository.findById(id)
                .map(this::mapToPostResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));
    }

    @Override
    public List<Category> findAllCategories() {
        logger.info("Getting all categories");
        return List.of(Category.values());
    }

    @Override
    public List<PostResponse> findPublishedPosts() {
        logger.info("Getting published posts");
        return postRepository.findByState(State.PUBLISHED)
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public PostResponse findPostByIdWithReviews(Long id, String userRole) {
        logger.info("Getting post by id {} with reviews", id);

        checksUserRole(userRole);

        PostResponse postResponse = postRepository.findById(id)
                .map(this::mapToPostResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        List<ReviewResponse> reviews = reviewClient.getReviewsByPostId(id, userRole)
                .stream()
                .map(this::mapToReviewResponse)
                .toList();

        postResponse.setReviews(reviews);
        return postResponse;
    }

    @Override
    public List<PostResponse> findSubmittedPosts(Long userId, String userRole) {
        logger.info("Getting submitted posts");

        checksUserRole(userRole);

        return postRepository.findByState(State.SUBMITTED)
                .stream()
                .filter(post -> !post.getUserId().equals(userId))
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public void publish(Long id, Long userId, String userRole) {
        logger.info("Publishing post with id {}", id);

        checksUserRole(userRole);
        State[] validStates = {State.APPROVED};
        Post post = checksToUpdatePost(id, userId, true, validStates);

        post.setState(State.PUBLISHED);
        postRepository.save(post);
    }

    @Override
    public List<PostResponse> findPostsByUserId(Long userId, String userRole) {
        logger.info("Getting post with userId {}", userId);

        checksUserRole(userRole);

        return postRepository.findByUserId(userId)
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public PostResponse createPost(Long userId, String userRole, PostRequest postRequest) {
        logger.info("Creating post");

        checksUserRole(userRole);

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .userId(userId)
                .category(postRequest.getCategory())
                .createdAt(LocalDateTime.now())
                .state(State.DRAFTED)
                .build();

        logger.info("Saving post");
        return mapToPostResponse(postRepository.save(post));
    }

    @Override
    public void submit(Long id, Long userId, String userRole) {
        logger.info("Submitting post with id {}", id);

        checksUserRole(userRole);
        State[] validStates = {State.DRAFTED, State.REJECTED};
        Post post = checksToUpdatePost(id, userId, true, validStates);

        post.setState(State.SUBMITTED);
        postRepository.save(post);
    }

    @Override
    public PostResponse updatePost(Long id, Long userId, String userRole, PostRequest postRequest) {
        logger.info("Updating post with id {}", id);

        checksUserRole(userRole);
        State[] validStates = new State[]{State.DRAFTED, State.REJECTED};
        Post post = checksToUpdatePost(id, userId, true, validStates);
        post.setContent(postRequest.getContent());
        return mapToPostResponse(postRepository.save(post));
    }

    private void checksUserRole(String role) {
        if (!role.equals("editor")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an editor.");
        }
    }

    private Post checksToUpdatePost(Long id, Long userId, boolean ownerAllowed, State[] validStates) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        if (post.getUserId().equals(userId) != ownerAllowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + userId + " cannot access this post.");
        }

        boolean postStateValid = false;
        for (State state : validStates) {
            if (post.getState() == state) {
                postStateValid = true;
                break;
            }
        }

        if (!postStateValid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " does not have the right state.");

        return post;
    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUserId())
                .category(post.getCategory())
                .createdAt(post.getCreatedAt())
                .state(post.getState())
                .build();
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .postId(review.getPostId())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .type(review.getType())
                .build();
    }
}
