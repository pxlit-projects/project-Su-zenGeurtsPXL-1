package be.pxl.services.service;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Notification;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.State;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.NotificationResponse;

import java.util.List;

public interface IPostService {
    void listen(String message);
    List<PostResponse> findAllPosts();

    List<PostResponse> findMyPosts(Long userId, String userRole);

    List<PostResponse> findPublishedPosts();

    List<PostResponse> findReviewablePosts(Long userId, String userRole);

    PostResponse findPostById(Long id);

    PostResponse findPostByIdWithReviews(Long id, String userRole);

    PostResponse findPostByIdWithComments(Long id);

    List<Category> findAllCategories();

    List<NotificationResponse> findMyNotifications(Long userId, String userRole);

    PostResponse createPost(Long userId, String userRole, PostRequest postRequest);

    PostResponse updatePost(Long id, Long userId, String userRole, String content);

    void updatePostStateToSubmitted(Long id, Long userId, String userRole);

    void updatePostStateToPublished(Long id, Long userId, String userRole);

    void updateNotificationIsReadToTrue(Long notificationId, Long userId, String userRole);

    void checksUserRole(String role);

    Post checksToUpdatePost(Long id, Long userId, boolean ownerIsAllowed, State[] validStates);

    Notification checksToUpdateNotification(Long notificationId, Long userId);
}
