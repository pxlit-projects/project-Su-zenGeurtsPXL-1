package be.pxl.services.client;

import be.pxl.services.domain.Comment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "comment-service")
public interface CommentClient {
    @GetMapping(path = "/api/comment/post/{postId}")
    List<Comment> getCommentsByPostId(@PathVariable Long postId);
}
