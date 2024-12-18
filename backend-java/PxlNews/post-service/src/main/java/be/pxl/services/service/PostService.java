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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final ReviewClient reviewClient;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

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
        logger.info("Getting post by id " + id);
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
    public PostResponse findPostByIdWithReviews(Long id) {
        logger.info("Getting post by id " + id + " with reviews");
        PostResponse postResponse = postRepository.findById(id)
                .map(this::mapToPostResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        List<ReviewResponse> reviews = reviewClient.getReviewsByPostId(id)
                .stream()
                .map(this::mapToReviewResponse)
                .toList();

        postResponse.setReviews(reviews);
        return postResponse;
    }

    @Override
    public List<PostResponse> findSubmittedPosts() {
        logger.info("Getting submitted posts");
        return postRepository.findByState(State.SUBMITTED)
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public void publish(Long id, Long userId, String userRole) {
        logger.info("Publishing post with id " + id);

        checksUserRole(userRole);
        Post post = checksToUpdatePost(id, userId, State.SUBMITTED);

        post.setState(State.PUBLISHED);
        postRepository.save(post);
    }

    @Override
    public List<PostResponse> findPostsByUserId(Long userId, String userRole) {
        logger.info("Getting post with userId " + userId);

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
        logger.info("Submitting post with id " + id);

        checksUserRole(userRole);
        Post post = checksToUpdatePost(id, userId, State.DRAFTED);

        post.setState(State.SUBMITTED);
        postRepository.save(post);
    }

    @Override
    public PostResponse updatePost(Long id, Long userId, String userRole, PostRequest postRequest) {
        logger.info("Updating post");

        checksUserRole(userRole);
        Post post = checksToUpdatePost(id, userId, State.DRAFTED);
        post.setContent(postRequest.getContent());
        return mapToPostResponse(postRepository.save(post));
    }

    private void checksUserRole(String role) {
        if (!role.equals("editor")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an editor.");
        }
    }

    private Post checksToUpdatePost(Long id, Long userId, State validState) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        if (!post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + userId + " cannot access this post.");
        }

        if (post.getState() == State.DRAFTED && validState != State.DRAFTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " is still a draft.");
        }

        if (post.getState() == State.SUBMITTED && validState != State.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " is already submitted.");
        }

        if (post.getState() == State.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " is already published.");
        }

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
