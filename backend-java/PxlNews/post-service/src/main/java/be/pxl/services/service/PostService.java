package be.pxl.services.service;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.State;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    @Override
    public List<PostResponse> findAllPosts() {
        logger.info("Getting all posts");
        return postRepository.findAll()
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public PostResponse findPostById(Long id) {
        logger.info("Getting post by id " + id);
        return postRepository.findById(id)
                .map(this::mapToPostResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));
    }

    @Override
    public List<Category> findAllCategories() {
        logger.info("Getting all categories");
        return List.of(Category.values());
    }

    @Override
    public List<PostResponse> findPublishedPosts() {
        logger.info("Getting published posts");
        return postRepository.findByState(State.PUBLISHED)
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public List<PostResponse> findPostsByUserId(Long userId) {
        logger.info("Getting post with userId " + userId);
        return postRepository.findByUserId(userId)
                .stream()
                .map(this::mapToPostResponse)
                .toList();
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        logger.info("Creating post");

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .userId(postRequest.getUserId())
                .category(postRequest.getCategory())
                .createdAt(LocalDateTime.now())
                .state(State.DRAFTED)
                .build();

        logger.info("Saving post");
        return mapToPostResponse(postRepository.save(post));
    }

    @Override
    public void submit(Long id, Long userId) {
        logger.info("Submitting post with id " + id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        if (!post.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + userId + " cannot submit this post.");
        }

        if (post.getState() == State.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " is already submitted.");
        }

        if (post.getState() == State.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post with id " + id + " is published.");
        }

        post.setState(State.SUBMITTED);
        postRepository.save(post);
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        logger.info("Updating post");

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post with id " + id + " not found."));

        if (!post.getUserId().equals(postRequest.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id " + postRequest.getUserId() + " cannot submit this post.");
        }

        post.setContent(postRequest.getContent());
        return mapToPostResponse(postRepository.save(post));
    }

    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .userId(post.getUserId())
                .category(post.getCategory())
                .createdAt(post.getCreatedAt())
                .state(post.getState())
                .build();
    }
}
