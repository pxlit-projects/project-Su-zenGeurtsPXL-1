package be.pxl.services.controller;

import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.service.IReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public List<ReviewResponse> getReviewsByPostId(@PathVariable Long postId, @RequestHeader String userRole) {
        return reviewService.findReviewsByPostId(postId, userRole);
    }

    @PostMapping(path = "approve")
    @ResponseStatus(HttpStatus.OK)
    public void approve(@RequestBody ReviewRequest reviewRequest, @RequestHeader Long userId, @RequestHeader String userRole) throws JsonProcessingException {
        reviewService.approve(reviewRequest, userId, userRole);
    }

    @PostMapping(path = "reject")
    @ResponseStatus(HttpStatus.OK)
    public void reject(@RequestBody ReviewRequest reviewRequest, @RequestHeader Long userId, @RequestHeader String userRole) throws JsonProcessingException {
        reviewService.reject(reviewRequest, userId, userRole);
    }
}
