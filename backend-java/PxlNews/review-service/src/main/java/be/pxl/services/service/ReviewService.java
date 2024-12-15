package be.pxl.services.service;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import feign.FeignException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final PostClient postClient;
    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Override
    public List<ReviewResponse> findReviewsByPostId(Long postId) {
        logger.info("Getting reviews by postId " + postId);
        return reviewRepository.findReviewsByPostId(postId)
                .stream()
                .map(this::mapToReviewResponse)
                .toList();
    }

    @Override
    public void approve(ReviewRequest reviewRequest) {
        logger.info("Approving post by id " + reviewRequest.getPostId());

        checksToReviewPost(reviewRequest.getPostId(), reviewRequest.getUserId(), reviewRequest.getUserRole());

        Review review = Review.builder()
                .userId(reviewRequest.getUserId())
                .postId(reviewRequest.getPostId())
                .content("")
                .createdAt(LocalDateTime.now())
                .type(Type.APPROVAL)
                .build();

        reviewRepository.save(review);
    }

    @Override
    public void reject(ReviewRequest reviewRequest) {
        logger.info("Rejecting post by id " + reviewRequest.getPostId());

        checksToReviewPost(reviewRequest.getPostId(), reviewRequest.getUserId(), reviewRequest.getUserRole());

        Review review = Review.builder()
                .userId(reviewRequest.getUserId())
                .postId(reviewRequest.getPostId())
                .content("")
                .createdAt(LocalDateTime.now())
                .type(Type.REJECTION)
                .build();

        reviewRepository.save(review);
    }

    private void checksToReviewPost(Long postId, Long userId, String role) {
        try {
            postClient.getPostById(postId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + postId + " not found.");
        }
        Post post = postClient.getPostById(postId);

        if (!role.equals("editor")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an editor.");
        }

        if (post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + userId + " cannot review own post.");
        }

        if (post.getState().equals("DRAFTED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + postId + " is still a draft.");
        }

        if (post.getState().equals("PUBLISHED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + postId + " is already published.");
        }

        if (reviewRepository.findReviewsByPostId(postId).stream().anyMatch(review -> review.getType().equals(Type.APPROVAL))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + postId + " is already approved.");
        }
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
