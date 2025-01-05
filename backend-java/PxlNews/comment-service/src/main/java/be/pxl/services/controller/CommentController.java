package be.pxl.services.controller;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final ICommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @GetMapping(path = "post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getCommentsByPostId(@PathVariable Long postId) {
        logger.info("[GET] /api/comment/post/{}: getReviewsByPostId()", postId);
        return commentService.findCommentsByPostId(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addComment(@RequestBody CommentRequest commentRequest, @RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[POST] /api/comment: addComment()");
        commentService.createComment(commentRequest, userId, userRole);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editComment(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole, @RequestBody String content) {
        logger.info("[PUT] /api/comment/{}: editComment()", id);
        commentService.updateCommentContent(id, userId, userRole, content);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeComment(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[DELETE] /api/comment/{}: removeComment()", id);
        commentService.deleteComment(id, userId, userRole);
    }
}
