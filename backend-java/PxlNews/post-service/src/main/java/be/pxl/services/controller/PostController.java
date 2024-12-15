package be.pxl.services.controller;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getAllPosts() {
        return postService.findAllPosts();
    }

    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostById(@PathVariable Long id) {
        return postService.findPostById(id);
    }

    @GetMapping(path = "/category")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllPostCategories() {
        return postService.findAllCategories();
    }

    @GetMapping(path = "/published")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPublishedPosts() {
        return postService.findPublishedPosts();
    }

    @GetMapping(path = "/submitted")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getSubmittedPosts() {
        return postService.findSubmittedPosts();
    }


    @GetMapping(path = "/{id}/with-reviews")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostByIdWithReviews(@PathVariable Long id) {
        return postService.findPostByIdWithReviews(id);
    }

    @GetMapping(path = "/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPostsByUserId(@PathVariable Long userId) {
        return postService.findPostsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse addPost(@RequestBody PostRequest postRequest) {
        return postService.createPost(postRequest);
    }

    @PostMapping(path = "submit/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void submitPost(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        postService.submit(id, postRequest);
    }

    @PostMapping(path = "publish/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void publishPost(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        postService.publish(id, postRequest);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse editPost(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        return postService.updatePost(id, postRequest);
    }
}
