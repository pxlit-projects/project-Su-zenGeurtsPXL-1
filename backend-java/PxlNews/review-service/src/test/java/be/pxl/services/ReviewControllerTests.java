package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReviewControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostClient postClient;

    @Autowired
    private ReviewRepository reviewRepository;

    @Container
    private static final MySQLContainer sqlContainer =
            new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @BeforeEach
    public void initEach() {
        reviewRepository.deleteAll();
    }

    @Test
    public void getReviewsByPostId_shouldReturnRequestedReviews() throws Exception {
        long postId = 1;

        for (int i = 0; i < 3; i++) {
            Review review = Review.builder()
                    .userId(1L)
                    .postId(postId)
                    .content("Content...")
                    .createdAt(LocalDateTime.now())
                    .type(Type.APPROVAL)
                    .build();

            reviewRepository.save(review);
        }

        String userRole = "editor";

        List<Review> expectedReviews = reviewRepository.findAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/post/" + postId)
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedReviews)));
    }

    @Test
    public void approve_shouldAddReviewOfTypeApprovalForRequestedPost() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();
        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId() + 1;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
                        .header("userId", userId)
                        .header("userRole", userRole)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk());

        Review addedReview = reviewRepository.findAll().get(0);
        assertEquals(Type.APPROVAL, addedReview.getType());
    }

    @Test
    public void approve_withInvalidPostId_shouldReturnNotFound() throws Exception {
        long postId = 9999;
        Mockito.when(postClient.getPostById(postId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        Long userId = 1L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void approve_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();

        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId() + 1;
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void approve_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();

        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void approve_withInvalidPostState_shouldReturnBadRequest() throws Exception {
        List<String> invalidStates = List.of("DRAFTED", "REJECTED", "APPROVED", "PUBLISHED");

        for (String state : invalidStates) {
            Post post = Post.builder()
                    .id(0L)
                    .title("Title ")
                    .content("Content...")
                    .userId(1L)
                    .category("ALUMNI")
                    .state(state)
                    .createdAt(LocalDateTime.now())
                    .build();

            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

            ReviewRequest reviewRequest = ReviewRequest.builder()
                    .postId(post.getId())
                    .content("Content...")
                    .build();

            Long userId = post.getUserId() + 1;
            String userRole = "editor";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(reviewRequest)))
                    .andExpect(status().isBadRequest());
        }
    }


    @Test
    public void reject_shouldAddReviewOfTypeRejectionForRequestedPost() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();

        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId() + 1;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk());

        Review addedReview = reviewRepository.findAll().get(0);
        assertEquals(Type.REJECTION, addedReview.getType());
    }

    @Test
    public void reject_withInvalidPostId_shouldReturnNotFound() throws Exception {
        long postId = 9999;

        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://post-service/api/post/9999",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );

        Mockito.when(postClient.getPostById(postId))
                .thenThrow(new FeignException.NotFound("Post with id " + postId + " not found.", request, null , null));

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        Long userId = 1L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void reject_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();

        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId() + 1;
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void reject_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();

        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void reject_withInvalidPostState_shouldReturnBadRequest() throws Exception {
        List<String> invalidStates = List.of("DRAFTED", "REJECTED", "APPROVED", "PUBLISHED");

        for (String state : invalidStates) {
            Post post = Post.builder()
                    .id(0L)
                    .title("Title ")
                    .content("Content...")
                    .userId(1L)
                    .category("ALUMNI")
                    .createdAt(LocalDateTime.now())
                    .state(state)
                    .build();

            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

            ReviewRequest reviewRequest = ReviewRequest.builder()
                    .postId(post.getId())
                    .content("Content...")
                    .build();

            Long userId = post.getUserId() + 1;
            String userRole = "editor";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(reviewRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void comment_shouldAddReviewOfTypeCommentForRequestedPost() throws Exception {
        List<String> validStates = List.of("SUBMITTED", "REJECTED", "APPROVED");

        for (String state : validStates) {
            Post post = Post.builder()
                    .id(0L)
                    .title("Title ")
                    .content("Content...")
                    .userId(1L)
                    .category("ALUMNI")
                    .createdAt(LocalDateTime.now())
                    .state(state)
                    .build();

            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

            ReviewRequest reviewRequest = ReviewRequest.builder()
                    .postId(post.getId())
                    .content("Content...")
                    .build();

            Long userId = post.getUserId() + 1;
            String userRole = "editor";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(reviewRequest)))
                    .andExpect(status().isOk());

            Review addedReview = reviewRepository.findAll().get(0);
            assertEquals(Type.COMMENT, addedReview.getType());
        }
    }

    @Test
    public void comment_withInvalidPostId_shouldReturnNotFound() throws Exception {
        long postId = 9999;

        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://post-service/api/post/9999",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );

        Mockito.when(postClient.getPostById(postId))
                .thenThrow(new FeignException.NotFound("Post with id " + postId + " not found.", request, null , null));

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        Long userId = 1L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void comment_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();

        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId() + 1;
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void comment_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = Post.builder()
                .id(0L)
                .title("Title ")
                .content("Content...")
                .userId(1L)
                .category("ALUMNI")
                .createdAt(LocalDateTime.now())
                .state("SUBMITTED")
                .build();

        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(post.getId())
                .content("Content...")
                .build();

        Long userId = post.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void comment_withInvalidPostState_shouldReturnBadRequest() throws Exception {
        List<String> invalidStates = List.of("DRAFTED", "PUBLISHED");

        for (String state : invalidStates) {
            Post post = Post.builder()
                    .id(0L)
                    .title("Title ")
                    .content("Content...")
                    .userId(1L)
                    .category("ALUMNI")
                    .createdAt(LocalDateTime.now())
                    .state(state)
                    .build();

            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);

            ReviewRequest reviewRequest = ReviewRequest.builder()
                    .postId(post.getId())
                    .content("Content...")
                    .build();

            Long userId = post.getUserId() + 1;
            String userRole = "editor";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(reviewRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}
