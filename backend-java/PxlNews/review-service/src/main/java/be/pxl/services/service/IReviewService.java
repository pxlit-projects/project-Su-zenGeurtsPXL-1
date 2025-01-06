package be.pxl.services.service;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface IReviewService {
    List<ReviewResponse> findReviewsByPostId(Long id, String userRole);

    void createReviewWithTypeApproval(ReviewRequest reviewRequest, Long userId, String userRole, String email) throws JsonProcessingException;

    void createReviewWithTypeRejection(ReviewRequest reviewRequest, Long userId, String userRole, String email) throws JsonProcessingException;

    void createReviewWithTypeComment(ReviewRequest reviewRequest, Long userId, String userRole, String email) throws JsonProcessingException;

    void checksUserRole(String role);

    void createReview(ReviewRequest reviewRequest, Long userId, String userRole, String email, Type reviewType, String[] validStates) throws JsonProcessingException;

    void sendEmail(String email, Review savedReview);
}
