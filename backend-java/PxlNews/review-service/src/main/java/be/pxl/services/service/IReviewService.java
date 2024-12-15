package be.pxl.services.service;

import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;

import java.util.List;

public interface IReviewService {
    List<ReviewResponse> findReviewsByPostId(Long id);

    void approve(ReviewRequest reviewRequest);

    void reject(ReviewRequest reviewRequest);
}
