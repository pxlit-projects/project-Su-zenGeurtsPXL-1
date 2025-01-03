package be.pxl.services.controller;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.dto.NotificationResponse;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final IPostService postService;
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

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

    @GetMapping(path = "/reviewable")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getReviewablePosts(@RequestHeader Long userId, @RequestHeader String userRole) {
        return postService.findReviewablePosts(userId, userRole);
    }

    @GetMapping(path = "/{id}/with-reviews")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostByIdWithReviews(@PathVariable Long id, @RequestHeader String userRole) {
        return postService.findPostByIdWithReviews(id, userRole);
    }

    @GetMapping(path = "/{id}/with-comments")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostByIdWithComments(@PathVariable Long id) {
        return postService.findPostByIdWithComments(id);
    }

    @GetMapping(path = "/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPostsByUserId(@RequestHeader Long userId, @RequestHeader String userRole) {
        return postService.findPostsByUserId(userId, userRole);
    }

    @GetMapping(path = "/notification")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> getNotificationsByUserId(@RequestHeader Long userId, @RequestHeader String userRole) {
        return postService.findNotificationsByUserId(userId, userRole);
    }

    @PostMapping(path = "/notification/{notificationId}/read")
    @ResponseStatus(HttpStatus.OK)
    public void readNotification(@PathVariable Long notificationId, @RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("On endpoint: readNotification");
        postService.putNotificationOnRead(notificationId, userId, userRole);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse addPost(@RequestHeader Long userId, @RequestHeader String userRole, @RequestBody PostRequest postRequest) {
        return postService.createPost(userId, userRole, postRequest);
    }

    @PostMapping(path = "{id}/submit")
    @ResponseStatus(HttpStatus.OK)
    public void submitPost(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole) {
        postService.submit(id, userId, userRole);
    }

    @PostMapping(path = "/{id}/publish")
    @ResponseStatus(HttpStatus.OK)
    public void publishPost(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole) {
        postService.publish(id, userId, userRole);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse editPost(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole, @RequestBody PostRequest postRequest) {
        return postService.updatePost(id, userId, userRole, postRequest);
    }
}
