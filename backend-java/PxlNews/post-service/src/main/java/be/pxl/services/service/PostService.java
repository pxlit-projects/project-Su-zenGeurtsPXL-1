package be.pxl.services.service;

import be.pxl.services.client.CommentClient;
import be.pxl.services.client.ReviewClient;
import be.pxl.services.domain.*;
import be.pxl.services.domain.dto.*;
import be.pxl.services.repository.NotificationRepository;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final NotificationRepository notificationRepository;
    private final ReviewClient reviewClient;
    private final CommentClient commentClient;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @RabbitListener(queues = "reviewQueue")
    public void listen(String in) {
        logger.info("Message read from reviewQueue : {}", in);

        try {
            handleMessage(in);
        } catch (Exception e) {
            logger.error("Error occurred: {}", e.getMessage());
        }
    }

    private void handleMessage(String message) throws JsonProcessingException {
            Map review = objectMapper.readValue(message, Map.class);
            Long postId = ((Integer) review.get("postId")).longValue();
            Long reviewerId = ((Integer) review.get("reviewerId")).longValue();

            Long userId = postRepository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + postId + " not found.")).getUserId();

            LocalDateTime executedAt = LocalDateTime.parse((String) review.get("executedAt"));
            logger.info("Creating notification");
            Notification notification = Notification.builder()
                    .postId(postId)
                    .receiverId(userId)
                    .executorId(reviewerId)
                    .content((String) review.get("comment"))
                    .action((String) review.get("reviewType"))
                    .executedAt(executedAt)
                    .isRead(false)
                    .build();

            logger.info("Saving notification");
            notificationRepository.save(notification);

            String reviewType = (String) review.get("reviewType");
            if (reviewType.equals("REJECTION") || reviewType.equals("APPROVAL")) {
                logger.info("Updating state to after {} of post with id {}", reviewType, postId);

                State[] validStates = {State.SUBMITTED};
                Post post = checksToUpdatePost(postId, reviewerId, false, validStates);

                State newState = reviewType.equals("APPROVAL") ? State.APPROVED : State.REJECTED;
                logger.info("New state is {}", newState);
                post.setState(newState);
                postRepository.save(post);
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
    public PostResponse findPostByIdWithComments(Long id) {
        logger.info("Getting post by id {} with comments", id);

        PostResponse postResponse = postRepository.findById(id)
                .map(this::mapToPostResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        List<CommentResponse> comments = commentClient.getCommentsByPostId(id)
                .stream()
                .map(this::mapToCommentResponse)
                .toList();

        postResponse.setComments(comments);
        return postResponse;
    }


    @Override
    public List<PostResponse> findReviewablePosts(Long userId, String userRole) {
        logger.info("Getting reviewable posts");

        checksUserRole(userRole);

        List<Post> reviewablePosts = new java.util.ArrayList<>(postRepository.findByState(State.SUBMITTED));
        reviewablePosts.addAll(postRepository.findByState(State.REJECTED));
        reviewablePosts.addAll(postRepository.findByState(State.APPROVED));

        return reviewablePosts
                .stream()
                .filter(post -> !post.getUserId().equals(userId))
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public void updatePostStateToSubmitted(Long id, Long userId, String userRole) {
        logger.info("Submitting post with id {}", id);

        checksUserRole(userRole);
        State[] validStates = {State.DRAFTED, State.REJECTED};
        Post post = checksToUpdatePost(id, userId, true, validStates);

        post.setState(State.SUBMITTED);
        postRepository.save(post);
    }

    @Override
    public void updatePostStateToPublished(Long id, Long userId, String userRole) {
        logger.info("Publishing post with id {}", id);

        checksUserRole(userRole);
        State[] validStates = {State.APPROVED};
        Post post = checksToUpdatePost(id, userId, true, validStates);

        post.setState(State.PUBLISHED);
        postRepository.save(post);
    }

    @Override
    public List<PostResponse> findMyPosts(Long userId, String userRole) {
        logger.info("Getting posts with userId {}", userId);

        checksUserRole(userRole);

        return postRepository.findByUserId(userId)
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> findMyNotifications(Long userId, String userRole) {
        logger.info("Getting notifications with userId {}", userId);

        checksUserRole(userRole);

        return notificationRepository.findByReceiverId(userId)
                .stream()
                .map(this::mapToNotificationResponse)
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
    public void updateNotificationIsReadToTrue(Long notificationId, Long userId, String userRole) {
        logger.info("Putting notification with id {} on read", notificationId);
        checksUserRole(userRole);

        Notification notification = checksToUpdateNotification(notificationId, userId);
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public PostResponse updatePost(Long id, Long userId, String userRole, String content) {
        logger.info("Updating post with id {}", id);

        checksUserRole(userRole);
        State[] validStates = new State[]{State.DRAFTED, State.REJECTED};
        Post post = checksToUpdatePost(id, userId, true, validStates);
        post.setContent(content);
        return mapToPostResponse(postRepository.save(post));
    }

    private void checksUserRole(String role) {
        if (!role.equals("editor")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an editor.");
        }
    }

    private Post checksToUpdatePost(Long id, Long userId, boolean ownerIsAllowed, State[] validStates) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        if (post.getUserId().equals(userId) != ownerIsAllowed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + userId + " cannot access this post.");
        }

        boolean hasValidState = false;
        for (State state : validStates) {
            if (post.getState() == state) {
                hasValidState = true;
                break;
            }
        }

        if (!hasValidState) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " does not have the right state.");

        return post;
    }

    private Notification checksToUpdateNotification(Long id, Long userId){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found."));

        if (!notification.getReceiverId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + userId + " cannot access this notification.");
        }

        if (notification.getIsRead()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Notification with id " + id + " is already read.");
        }

        return notification;
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

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .postId(notification.getPostId())
                .receiverId(notification.getReceiverId())
                .executorId(notification.getExecutorId())
                .content(notification.getContent())
                .action(notification.getAction())
                .executedAt(notification.getExecutedAt())
                .isRead(notification.getIsRead())
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

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
