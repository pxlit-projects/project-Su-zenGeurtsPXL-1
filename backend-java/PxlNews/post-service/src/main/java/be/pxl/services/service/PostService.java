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
    public PostResponse createPost(PostRequest postRequest) {
        logger.info("Creating post with state" + postRequest.getStateString());
        State state;
        if (postRequest.getStateString() == null) {
            logger.info("Using default state");
            state = State.DRAFTED;
        } else {
            try {
                state = State.valueOf(postRequest.getStateString());
                if (state == State.PUBLISHED) {
                    logger.error("PUBLISHED state not allowed");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post cannot be published before approval.");
                }
            } catch (IllegalArgumentException e) {
                logger.error("Non-existent state");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid state.");
            }
        }

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .userId(postRequest.getUserId())
                .category(postRequest.getCategory())
                .createdAt(LocalDateTime.now())
                .state(state)
                .build();

        logger.info("Saving post");
        return mapToPostResponse(postRepository.save(post));
    }

    @Override
    public List<Category> findAllCategories() {
        logger.info("Getting all categories");
        return List.of(Category.values());
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
