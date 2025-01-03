package be.pxl.services.service;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.repository.CommentRespository;
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
    private final CommentRespository commentRespository;
    private final PostClient postClient;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Override
    public List<CommentResponse> findCommentsByPostId(Long postId, String userRole) {
        logger.info("Getting comments by postId {}", postId);

        checksUserRole(userRole);

        return commentRespository.findCommentsByPostId(postId)
                .stream()
                .map(this::mapToCommentResponse)
                .toList();
    }

    @Override
    public CommentResponse createComment(CommentRequest commentRequest, Long userId, String userRole) {
        logger.info("Creating comment");

        checksUserRole(userRole);

        try {
            Post post = postClient.getPostById(commentRequest.getPostId());

            if(!post.getState().equals("PUBLISHED")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot comment on a post that is not published yet.");
            }
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + commentRequest.getPostId() + " not found.");
        }

        Comment comment = Comment.builder()
                .userId(userId)
                .postId(commentRequest.getPostId())
                .content(commentRequest.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        logger.info("Saving comment");
        return mapToCommentResponse(commentRespository.save(comment));

    }

    @Override
    public CommentResponse updateComment(Long id, Long userId, String userRole, CommentRequest commentRequest) {
        logger.info("Updating comment with id {}", id);

        checksUserRole(userRole);

        Comment comment = checksAccessToComment(id, userId);
        comment.setContent(commentRequest.getContent());
        return mapToCommentResponse(commentRespository.save(comment));
    }

    @Override
    public void deleteComment(Long id, Long userId, String userRole) {
        logger.info("Deleting comment with id {}", id);

        checksUserRole(userRole);

        Comment comment = checksAccessToComment(id, userId);
        commentRespository.delete(comment);
    }

    private void checksUserRole(String role) {
        if (!role.equals("editor") && !role.equals("user")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not logged in.");
        }
    }

    private Comment checksAccessToComment(Long id, Long userId) {
        Comment comment = commentRespository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have access to this comment");
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
