package be.pxl.services.service;

import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface IReviewService {
    List<ReviewResponse> findReviewsByPostId(Long id, String userRole);

    void approve(ReviewRequest reviewRequest, Long userId, String userRole) throws JsonProcessingException;

    void reject(ReviewRequest reviewRequest, Long userId, String userRole) throws JsonProcessingException;
    void comment (ReviewRequest reviewRequest, Long userId, String userRole) throws JsonProcessingException;
}
