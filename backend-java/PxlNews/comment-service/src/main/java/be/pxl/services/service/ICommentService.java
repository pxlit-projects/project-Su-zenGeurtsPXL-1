package be.pxl.services.service;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;

import java.util.List;

public interface ICommentService {
    List<CommentResponse> findCommentsByPostId(Long postId);

    CommentResponse createComment(CommentRequest commentRequest, Long userId, String userRole);

    CommentResponse updateComment(Long id, Long userId, String userRole, String content);

    void deleteComment(Long id, Long userId, String userRole);
}
