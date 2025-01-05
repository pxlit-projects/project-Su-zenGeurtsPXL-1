package be.pxl.services.controller;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.NotificationResponse;
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
        logger.info("[GET] /api/post: getAllPosts()");
        return postService.findAllPosts();
    }

    @GetMapping(path = "/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getMyPosts(@RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[GET] /api/post/mine: getMyPosts()");
        return postService.findMyPosts(userId, userRole);
    }

    @GetMapping(path = "/published")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getPublishedPosts() {
        logger.info("[GET] /api/post/published: getPublishedPosts()");
        return postService.findPublishedPosts();
    }

    @GetMapping(path = "/reviewable")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponse> getReviewablePosts(@RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[GET] /api/post/reviewable: getReviewablePosts()");
        return postService.findReviewablePosts(userId, userRole);
    }

    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostById(@PathVariable Long id) {
        logger.info("[GET] /api/post/{}: getPostById()", id);
        return postService.findPostById(id);
    }

    @GetMapping(path = "/{id}/with-reviews")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostByIdWithReviews(@PathVariable Long id, @RequestHeader String userRole) {
        logger.info("[GET] /api/post/{}/with-reviews: getAllPosts()", id);
        return postService.findPostByIdWithReviews(id, userRole);
    }

    @GetMapping(path = "/{id}/with-comments")
    @ResponseStatus(HttpStatus.OK)
    public PostResponse getPostByIdWithComments(@PathVariable Long id) {
        logger.info("[GET] /api/post/{}/with-comments: getAllPosts()", id);
        return postService.findPostByIdWithComments(id);
    }

    @GetMapping(path = "/category")
    @ResponseStatus(HttpStatus.OK)
    public List<Category> getAllCategories() {
        logger.info("[GET] /api/post/category: getAllCategories()");
        return postService.findAllCategories();
    }

    @GetMapping(path = "/notification/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResponse> getMyNotifications(@RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[GET] /api/post/notification: getNotificationsByUserId()");
        return postService.findMyNotifications(userId, userRole);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addPost(@RequestHeader Long userId, @RequestHeader String userRole, @RequestBody PostRequest postRequest) {
        logger.info("[POST] /api/post: addPost()");
        postService.createPost(userId, userRole, postRequest);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editPost(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole, @RequestBody String content) {
        logger.info("[PUT] /api/post/{}: editPost()", id);
        postService.updatePostContent(id, userId, userRole, content);
    }

    @PutMapping(path = "{id}/submit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitPost(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[PUT] /api/post/{}/submit: submitPost()", id);
        postService.updatePostStateToSubmitted(id, userId, userRole);
    }

    @PutMapping(path = "/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publishPost(@PathVariable Long id, @RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[PUT] /api/post/{}/publish: publishPost()", id);
        postService.updatePostStateToPublished(id, userId, userRole);
    }

    @PutMapping(path = "/notification/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markNotificationAsRead(@PathVariable Long notificationId, @RequestHeader Long userId, @RequestHeader String userRole) {
        logger.info("[PUT] /api/post/notification/{}/read: markNotificationAsRead()", notificationId);
        postService.updateNotificationIsReadToTrue(notificationId, userId, userRole);
    }
}
