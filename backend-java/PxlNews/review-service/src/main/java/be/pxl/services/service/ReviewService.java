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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import feign.FeignException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
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
    public void approve(ReviewRequest reviewRequest, Long userId, String userRole) throws JsonProcessingException {
        logger.info("Approving post with id {}", reviewRequest.getPostId());

        checksUserRole(userRole);

        String[] validStates = {"SUBMITTED"};
        checksToReviewPost(reviewRequest.getPostId(), userId, validStates);

        Review review = Review.builder()
                .userId(userId)
                .postId(reviewRequest.getPostId())
                .content(reviewRequest.getContent())
                .createdAt(LocalDateTime.now())
                .type(Type.APPROVAL)
                .build();

        Review savedReview = reviewRepository.save(review);
        rabbitTemplate.convertAndSend("reviewQueue", createMessage(savedReview));
    }

    @Override
    public void reject(ReviewRequest reviewRequest, Long userId, String userRole) throws JsonProcessingException {
        logger.info("Rejecting post with id {}", reviewRequest.getPostId());

        checksUserRole(userRole);

        String[] validStates = {"SUBMITTED"};
        checksToReviewPost(reviewRequest.getPostId(), userId, validStates);

        Review review = Review.builder()
                .userId(userId)
                .postId(reviewRequest.getPostId())
                .content(reviewRequest.getContent())
                .createdAt(LocalDateTime.now())
                .type(Type.REJECTION)
                .build();

        Review savedReview = reviewRepository.save(review);

        rabbitTemplate.convertAndSend("reviewQueue", createMessage(savedReview));
    }

    @Override
    public void comment(ReviewRequest reviewRequest, Long userId, String userRole) throws JsonProcessingException {
        logger.info("Commenting on a post with id {}", reviewRequest.getPostId());

        checksUserRole(userRole);

        String[] validStates = {"SUBMITTED", "REJECTED", "APPROVED"};
        checksToReviewPost(reviewRequest.getPostId(), userId, validStates);

        Review review = Review.builder()
                .userId(userId)
                .postId(reviewRequest.getPostId())
                .content(reviewRequest.getContent())
                .createdAt(LocalDateTime.now())
                .type(Type.COMMENT)
                .build();

        Review savedReview = reviewRepository.save(review);

        rabbitTemplate.convertAndSend("reviewQueue", createMessage(savedReview));
    }

    private String createMessage(Review review) throws JsonProcessingException {
        Map<String, Object> message = new HashMap<>();
        message.put("postId", review.getPostId());
        message.put("reviewId", review.getId());
        message.put("reviewerId", review.getUserId());
        message.put("reviewType", review.getType());
        message.put("comment", review.getContent());
        message.put("executedAt", review.getCreatedAt());

        return objectMapper.writeValueAsString(message);
    }

    private void checksUserRole(String role) {
        if (!role.equals("editor")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an editor.");
        }
    }

    private void checksToReviewPost(Long postId, Long userId, String[] validStates) {
        try {
            postClient.getPostById(postId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + postId + " not found.");
        }
        Post post = postClient.getPostById(postId);

        if (post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + userId + " cannot review own post.");
        }

        boolean postStateValid = false;
        for (String state : validStates) {
            if (post.getState().equals(state)) {
                postStateValid = true;
                break;
            }
        }

        if (!postStateValid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + postId + " does not have the right state.");
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
