package be.pxl.services.service;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import feign.FeignException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final ReviewRepository reviewRepository;
    private final PostClient postClient;
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Override
    public List<ReviewResponse> findReviewsByPostId(Long postId, String userRole) {
        logger.info("Getting reviews by postId {}", postId);

        checksUserRole(userRole);

        return reviewRepository.findReviewsByPostId(postId)
                .stream()
                .map(this::mapToReviewResponse)
                .toList();
    }

    @Override
    public void createReviewWithTypeApproval(ReviewRequest reviewRequest, Long userId, String userRole, String email) throws JsonProcessingException {
        logger.info("Approving post with id {}", reviewRequest.getPostId());

        String[] validStates = {"SUBMITTED"};

        createReview(reviewRequest, userId, userRole, email, Type.APPROVAL,  validStates);
    }

    @Override
    public void createReviewWithTypeRejection(ReviewRequest reviewRequest, Long userId, String userRole, String email) throws JsonProcessingException {
        logger.info("Rejecting post with id {}", reviewRequest.getPostId());

        String[] validStates = {"SUBMITTED"};

        createReview(reviewRequest, userId, userRole, email, Type.REJECTION,  validStates);
    }

    @Override
    public void createReviewWithTypeComment(ReviewRequest reviewRequest, Long userId, String userRole, String email) throws JsonProcessingException {
        logger.info("Commenting on a post with id {}", reviewRequest.getPostId());

        String[] validStates = {"SUBMITTED", "REJECTED", "APPROVED"};

        createReview(reviewRequest, userId, userRole, email, Type.COMMENT,  validStates);
    }

    @Override
    public void checksUserRole(String role) {
        if (!role.equals("editor")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an editor.");
        }
    }

    @Override
    public void createReview(ReviewRequest reviewRequest, Long userId, String userRole, String email, Type reviewType, String[] validStates) throws JsonProcessingException {
        checksUserRole(userRole);

        long postId = reviewRequest.getPostId();

        try {
            postClient.getPostById(postId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + postId + " not found.");
        }

        Post post = postClient.getPostById(postId);

        if (post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with id " + userId + " cannot review own post.");
        }

        boolean hasValidState = false;
        for (String state : validStates) {
            if (post.getState().equals(state)) {
                hasValidState = true;
                break;
            }
        }

        if (!hasValidState) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + postId + " does not have the right state.");

        Review review = Review.builder()
                .userId(userId)
                .postId(reviewRequest.getPostId())
                .content(reviewRequest.getContent())
                .createdAt(LocalDateTime.now())
                .type(reviewType)
                .build();

        Review savedReview = reviewRepository.save(review);

        Map<String, Object> message = new HashMap<>();
        message.put("postId", savedReview.getPostId());
        message.put("reviewId", savedReview.getId());
        message.put("reviewerId", savedReview.getUserId());
        message.put("reviewType", savedReview.getType());
        message.put("comment", savedReview.getContent());
        message.put("executedAt", savedReview.getCreatedAt());

        String messageString = objectMapper.writeValueAsString(message);

        rabbitTemplate.convertAndSend("reviewQueue", messageString);

        sendEmail(email, post, savedReview);
    }

    @Override
    public void sendEmail(String to, Post post, Review savedReview) {
        // TODO: Make mail better
        logger.info("Sending email to {}", to);
        String subject = "Your post has been reviewed!";
        // Hello editor,
        // Your post has been approved/reviewed/commented on. Review "[title]".
        // Click here to view the review.
        // Kind regards,
        // The PXL News team
        String verb = savedReview.getType().equals(Type.APPROVAL) ? "approved" : savedReview.getType().equals(Type.REJECTION) ? "rejected" : "commented on";
        String text = "Hello editor," +
        "\nYour post '" + post.getTitle() + "' has been " + verb + " ." +
        "\n\nLink to view the post: http://localhost:4200/post/" + post.getId() +
        "\n\nKind regards," +
        "\nThe PXL News team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("janedoepxl@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
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
