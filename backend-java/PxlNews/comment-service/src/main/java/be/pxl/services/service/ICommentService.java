package be.pxl.services.service;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;

import java.util.List;

public interface ICommentService {
    List<CommentResponse> findCommentsByPostId(Long postId);

    void createComment(CommentRequest commentRequest, Long userId, String userRole);

    void updateCommentContent(Long id, Long userId, String userRole, String content);

    void deleteComment(Long id, Long userId, String userRole);

    void checksUserRole(String role);

    void checkPost(Long postId);

    Comment checkComment(Long id, Long userId);
}
