package be.pxl.services.service;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.dto.NotificationResponse;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;

import java.util.List;

public interface IPostService {
    List<PostResponse> findAllPosts();

    PostResponse findPostById(Long id);

    PostResponse createPost(Long userId, String userRole, PostRequest postRequest);

    List<Category> findAllCategories();

    List<PostResponse> findPostsByUserId(Long userId, String userRole);
    List<NotificationResponse> findNotificationsByUserId(Long userId, String userRole);

    void submit(Long id, Long userId, String userRole);
    void putNotificationOnRead(Long notificationId, Long userId, String userRole);

    PostResponse updatePost(Long id, Long userId, String userRole, PostRequest postRequest);

    List<PostResponse> findPublishedPosts();

    PostResponse findPostByIdWithReviews(Long id, String userRole);

    List<PostResponse> findReviewablePosts(Long userId, String userRole);

    void publish(Long id, Long userId, String userRole);

    PostResponse findPostByIdWithComments(Long id);
}
