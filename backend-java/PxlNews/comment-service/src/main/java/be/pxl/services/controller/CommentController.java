package be.pxl.services.controller;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;

    @GetMapping(path = "post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.findCommentsByPostId(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@RequestBody CommentRequest commentRequest, @RequestHeader Long userId, @RequestHeader String userRole) {
        return commentService.createComment(commentRequest, userId, userRole);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse editComment(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole, @RequestBody String content) {
        return commentService.updateComment(id, userId, userRole, content);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole) {
        commentService.deleteComment(id, userId, userRole);
    }
}
