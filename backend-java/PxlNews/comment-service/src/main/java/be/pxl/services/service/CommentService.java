package be.pxl.services.service;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.repository.CommentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final PostClient postClient;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Override
    public List<CommentResponse> findCommentsByPostId(Long postId) {
        logger.info("Getting comments by postId {}", postId);

        return commentRepository.findCommentsByPostId(postId)
                .stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    @Override
    public void createComment(CommentRequest commentRequest, Long userId, String userRole) {
        logger.info("Creating comment");

        checksUserRole(userRole);

        checkPost(commentRequest.getPostId());

        Comment comment = Comment.builder()
                .userId(userId)
                .postId(commentRequest.getPostId())
                .content(commentRequest.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        logger.info("Saving comment");
        commentRepository.save(comment);

    }

    @Override
    public void updateCommentContent(Long id, Long userId, String userRole, String content) {
        logger.info("Updating comment with id {}", id);

        checksUserRole(userRole);

        Comment comment = checkComment(id, userId);
        comment.setContent(content);
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long id, Long userId, String userRole) {
        logger.info("Deleting comment with id {}", id);

        checksUserRole(userRole);

        Comment comment = checkComment(id, userId);
        commentRepository.delete(comment);
    }

    @Override
    public void checksUserRole(String role) {
        if (!role.equals("editor") && !role.equals("user")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not logged in.");
        }
    }

    @Override
    public void checkPost(Long id) {
        try {
            Post post = postClient.getPostById(id);

            if(!post.getState().equals("PUBLISHED")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " is not published yet.");
            }
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found.");
        }
    }

    @Override
    public Comment checkComment(Long id, Long userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment with id " + id + " not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User with id " + userId + " does not have access to this comment");
        }

        return comment;
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
