package be.pxl.services.controller;

import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @GetMapping(path = "post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewsByPostId(@PathVariable Long postId) {
        return reviewService.findReviewsByPostId(postId);
    }

    @PostMapping(path = "approve")
    @ResponseStatus(HttpStatus.OK)
    public void approve(@RequestBody ReviewRequest reviewRequest) {
        reviewService.approve(reviewRequest);
    }

    @PostMapping(path = "reject")
    @ResponseStatus(HttpStatus.OK)
    public void reject(@RequestBody ReviewRequest reviewRequest) {
        reviewService.reject(reviewRequest);
    }
}
