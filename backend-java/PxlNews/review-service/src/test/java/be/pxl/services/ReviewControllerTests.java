package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
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

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private PostClient postClient;

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

        for (int i = 0; i < 10; i++) {
            Review review = Review.builder()
                    .userId((long)(i) + 1)
                    .postId((long)(i))
                    .content("Content...")
                    .createdAt(LocalDateTime.now())
                    .type(Type.APPROVAL)
                    .build();

            reviewRepository.save(review);
        }

        List<String> states = List.of("SUBMITTED", "REJECTED", "APPROVED");

        for (int i = 0; i < states.size(); i++) {
            Post post = Post.builder()
                    .id((long)(i + 1))
                    .title("Title ")
                    .content("Content...")
                    .userId((long)(i + 1))
                    .category("ALUMNI")
                    .createdAt(LocalDateTime.now())
                    .state(states.get(i))
                    .build();

            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
        }
    }

    @Test
    public void getReviewsByPostId_shouldFindReviewsWithGivenPostId() throws Exception {
        long postId = 1L;
        List<Review> expectedReviews = reviewRepository.findReviewsByPostId(postId);

        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/post/" + postId)
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedReviews)));
    }

    @Test
    public void approvePost_shouldCreateReviewWithTypeApprovalForPostWithGivenId() throws Exception {
        long postId = 1L;

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        long userId = postId + 1;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
                        .header("userId", userId)
                        .header("userRole", userRole)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isAccepted());

        Review addedReview = reviewRepository.findAll().get(reviewRepository.findAll().size() - 1);
        assertEquals(Type.APPROVAL, addedReview.getType());
    }

    @Test
    public void rejectPost_shouldCreateReviewWithTypeRejectionForPostWithGivenId() throws Exception {
        long postId = 1L;

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        long userId = postId + 1;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isAccepted());

        Review addedReview = reviewRepository.findAll().get(reviewRepository.findAll().size() - 1);
        assertEquals(Type.REJECTION, addedReview.getType());
    }

    @Test
    public void commentOnPost_shouldCreateReviewWithTypeCommentForPostWithGivenId() throws Exception {
        for (int i = 1; i < 4; i++) {
            ReviewRequest reviewRequest = ReviewRequest.builder()
                    .postId((long) i)
                    .content("Content...")
                    .build();

            long userId = (long) i + 1;
            String userRole = "editor";

            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(reviewRequest)))
                    .andExpect(status().isAccepted());

            assertEquals(Type.COMMENT, reviewRepository.findAll().get(reviewRepository.findAll().size() - 1).getType());
        }
    }

//    @Test
//    public void approve_Post_withInvalidPostId_shouldReturnNotFound() throws Exception {
//        long postId = 9999;
//        Mockito.when(postClient.getPostById(postId))
//                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(postId)
//                .content("Content...")
//                .build();
//
//        long userId = 1L;
//        String userRole = "editor";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void approve_Post_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
//        Post post = Post.builder()
//                .id(0L)
//                .title("Title ")
//                .content("Content...")
//                .userId(1L)
//                .category("ALUMNI")
//                .createdAt(LocalDateTime.now())
//                .state("SUBMITTED")
//                .build();
//
//        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(post.getId())
//                .content("Content...")
//                .build();
//
//        long userId = post.getUserId() + 1;
//        String userRole = "user";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void approve_Post_withInvalidUserId_shouldReturnBadRequest() throws Exception {
//        Post post = Post.builder()
//                .id(0L)
//                .title("Title ")
//                .content("Content...")
//                .userId(1L)
//                .category("ALUMNI")
//                .createdAt(LocalDateTime.now())
//                .state("SUBMITTED")
//                .build();
//
//        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(post.getId())
//                .content("Content...")
//                .build();
//
//        long userId = post.getUserId();
//        String userRole = "editor";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void approve_Post_withInvalidPostState_shouldReturnBadRequest() throws Exception {
//        List<String> invalidStates = List.of("DRAFTED", "REJECTED", "APPROVED", "PUBLISHED");
//
//        for (String state : invalidStates) {
//            Post post = Post.builder()
//                    .id(0L)
//                    .title("Title ")
//                    .content("Content...")
//                    .userId(1L)
//                    .category("ALUMNI")
//                    .state(state)
//                    .createdAt(LocalDateTime.now())
//                    .build();
//
//            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//            ReviewRequest reviewRequest = ReviewRequest.builder()
//                    .postId(post.getId())
//                    .content("Content...")
//                    .build();
//
//            long userId = post.getUserId() + 1;
//            String userRole = "editor";
//
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/approve")
//                            .header("userId", userId)
//                            .header("userRole", userRole)
//                            .contentType("application/json")
//                            .content(objectMapper.writeValueAsString(reviewRequest)))
//                    .andExpect(status().isBadRequest());
//        }
//    }
//
//    @Test
//    public void reject_Post_withInvalidPostId_shouldReturnNotFound() throws Exception {
//        long postId = 9999;
//
//        Request request = Request.create(
//                Request.HttpMethod.GET,
//                "http://post-service/api/post/9999",
//                Collections.emptyMap(),
//                null,
//                new RequestTemplate()
//        );
//
//        Mockito.when(postClient.getPostById(postId))
//                .thenThrow(new FeignException.NotFound("Post with id " + postId + " not found.", request, null , null));
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(postId)
//                .content("Content...")
//                .build();
//
//        long userId = 1L;
//        String userRole = "editor";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void reject_Post_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
//        Post post = Post.builder()
//                .id(0L)
//                .title("Title ")
//                .content("Content...")
//                .userId(1L)
//                .category("ALUMNI")
//                .createdAt(LocalDateTime.now())
//                .state("SUBMITTED")
//                .build();
//
//        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(post.getId())
//                .content("Content...")
//                .build();
//
//        long userId = post.getUserId() + 1;
//        String userRole = "user";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void reject_Post_withInvalidUserId_shouldReturnBadRequest() throws Exception {
//        Post post = Post.builder()
//                .id(0L)
//                .title("Title ")
//                .content("Content...")
//                .userId(1L)
//                .category("ALUMNI")
//                .createdAt(LocalDateTime.now())
//                .state("SUBMITTED")
//                .build();
//
//        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(post.getId())
//                .content("Content...")
//                .build();
//
//        long userId = post.getUserId();
//        String userRole = "editor";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void reject_Post_withInvalidPostState_shouldReturnBadRequest() throws Exception {
//        List<String> invalidStates = List.of("DRAFTED", "REJECTED", "APPROVED", "PUBLISHED");
//
//        for (String state : invalidStates) {
//            Post post = Post.builder()
//                    .id(0L)
//                    .title("Title ")
//                    .content("Content...")
//                    .userId(1L)
//                    .category("ALUMNI")
//                    .createdAt(LocalDateTime.now())
//                    .state(state)
//                    .build();
//
//            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//            ReviewRequest reviewRequest = ReviewRequest.builder()
//                    .postId(post.getId())
//                    .content("Content...")
//                    .build();
//
//            long userId = post.getUserId() + 1;
//            String userRole = "editor";
//
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/reject")
//                            .header("userId", userId)
//                            .header("userRole", userRole)
//                            .contentType("application/json")
//                            .content(objectMapper.writeValueAsString(reviewRequest)))
//                    .andExpect(status().isBadRequest());
//        }
//    }
//
//    @Test
//    public void comment_OnPost_withInvalidPostId_shouldReturnNotFound() throws Exception {
//        long postId = 9999;
//
//        Request request = Request.create(
//                Request.HttpMethod.GET,
//                "http://post-service/api/post/9999",
//                Collections.emptyMap(),
//                null,
//                new RequestTemplate()
//        );
//
//        Mockito.when(postClient.getPostById(postId))
//                .thenThrow(new FeignException.NotFound("Post with id " + postId + " not found.", request, null , null));
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(postId)
//                .content("Content...")
//                .build();
//
//        long userId = 1L;
//        String userRole = "editor";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void comment_OnPost_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
//        Post post = Post.builder()
//                .id(0L)
//                .title("Title ")
//                .content("Content...")
//                .userId(1L)
//                .category("ALUMNI")
//                .createdAt(LocalDateTime.now())
//                .state("SUBMITTED")
//                .build();
//
//        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(post.getId())
//                .content("Content...")
//                .build();
//
//        long userId = post.getUserId() + 1;
//        String userRole = "user";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void comment_OnPost_withInvalidUserId_shouldReturnBadRequest() throws Exception {
//        Post post = Post.builder()
//                .id(0L)
//                .title("Title ")
//                .content("Content...")
//                .userId(1L)
//                .category("ALUMNI")
//                .createdAt(LocalDateTime.now())
//                .state("SUBMITTED")
//                .build();
//
//        Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//        ReviewRequest reviewRequest = ReviewRequest.builder()
//                .postId(post.getId())
//                .content("Content...")
//                .build();
//
//        long userId = post.getUserId();
//        String userRole = "editor";
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
//                        .header("userId", userId)
//                        .header("userRole", userRole)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(reviewRequest)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void comment_OnPost_withInvalidPostState_shouldReturnBadRequest() throws Exception {
//        List<String> invalidStates = List.of("DRAFTED", "PUBLISHED");
//
//        for (String state : invalidStates) {
//            Post post = Post.builder()
//                    .id(0L)
//                    .title("Title ")
//                    .content("Content...")
//                    .userId(1L)
//                    .category("ALUMNI")
//                    .createdAt(LocalDateTime.now())
//                    .state(state)
//                    .build();
//
//            Mockito.when(postClient.getPostById(post.getId())).thenReturn(post);
//
//            ReviewRequest reviewRequest = ReviewRequest.builder()
//                    .postId(post.getId())
//                    .content("Content...")
//                    .build();
//
//            long userId = post.getUserId() + 1;
//            String userRole = "editor";
//
//            mockMvc.perform(MockMvcRequestBuilders.post("/api/review/comment")
//                            .header("userId", userId)
//                            .header("userRole", userRole)
//                            .contentType("application/json")
//                            .content(objectMapper.writeValueAsString(reviewRequest)))
//                    .andExpect(status().isBadRequest());
//        }
//    }
}
