package be.pxl.services.service;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;

import java.util.List;

public interface IPostService {
    List<PostResponse> findAllPosts();

    PostResponse findPostById(Long id);

    PostResponse createPost(PostRequest postRequest);

    List<Category> findAllCategories();
}
