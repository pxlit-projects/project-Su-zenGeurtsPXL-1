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
    public void submitPost(@PathVariable Long id, @RequestBody Long userId) {
        postService.submit(id, userId);
    }
}
