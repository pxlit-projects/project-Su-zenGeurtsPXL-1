package be.pxl.services.controller;

import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.domain.dto.ReviewResponse;
import be.pxl.services.service.IReviewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @GetMapping(path = "post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getReviewsByPostId(@PathVariable Long postId, @RequestHeader String userRole) {
        logger.info("[GET] /api/review/post/{}: getReviewsByPostId()", postId);
        return reviewService.findReviewsByPostId(postId, userRole);
    }

    @PostMapping(path = "approve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void approvePost(@RequestBody ReviewRequest reviewRequest, @RequestHeader Long userId, @RequestHeader String userRole) throws JsonProcessingException {
        logger.info("[POST] /api/review/approve: approvePost()");
        reviewService.createReviewWithTypeApproval(reviewRequest, userId, userRole);
    }

    @PostMapping(path = "reject")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void rejectPost(@RequestBody ReviewRequest reviewRequest, @RequestHeader Long userId, @RequestHeader String userRole) throws JsonProcessingException {
        logger.info("[POST] /api/review/reject: rejectPost()");
        reviewService.createReviewWithTypeRejection(reviewRequest, userId, userRole);
    }

    @PostMapping(path = "comment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void commentOnPost(@RequestBody ReviewRequest reviewRequest, @RequestHeader Long userId, @RequestHeader String userRole) throws JsonProcessingException {
        logger.info("[POST] /api/review/comment: commentOnPost()");
        reviewService.createReviewWithTypeComment(reviewRequest, userId, userRole);
    }
}
