package be.pxl.services.service;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;

import java.util.List;

public interface IPostService {
    List<PostResponse> findAllPosts();

    PostResponse findPostById(Long id);

    PostResponse createPost(Long userId, String userRole, PostRequest postRequest);

    List<Category> findAllCategories();

    List<PostResponse> findPostsByUserId(Long userId, String userRole);

    void submit(Long id, Long userId, String userRole);

    PostResponse updatePost(Long id, Long userId, String userRole, PostRequest postRequest);

    List<PostResponse> findPublishedPosts();

    PostResponse findPostByIdWithReviews(Long id, String userRole);

    List<PostResponse> findSubmittedPosts(String userRole);

    void publish(Long id, Long userId, String userRole);
}
