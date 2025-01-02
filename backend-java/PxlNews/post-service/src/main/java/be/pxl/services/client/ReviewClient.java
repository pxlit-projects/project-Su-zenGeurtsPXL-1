package be.pxl.services.client;

import be.pxl.services.domain.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "review-service")
public interface ReviewClient {
    @GetMapping(path = "/api/review/post/{postId}")
    List<Review> getReviewsByPostId(@PathVariable Long postId, @RequestHeader String userRole);
}
