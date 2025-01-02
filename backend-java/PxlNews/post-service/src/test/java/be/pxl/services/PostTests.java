package be.pxl.services;

import be.pxl.services.client.ReviewClient;
import be.pxl.services.domain.Category;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.State;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewClient reviewClient;

    @Autowired
    private PostRepository postRepository;

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
        postRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            Post post = Post.builder()
                    .title("Post " + i)
                    .content("Content...")
                    .userId((long)(i))
                    .category(Category.values()[i % Category.values().length])
                    .createdAt(LocalDateTime.now())
                    .state(State.DRAFTED)
                    .build();
            postRepository.save(post);
        }
    }

    @Test
    public void postBuilder_shouldBeImplemented() {
        Post post = Post.builder()
                .title("Post title")
                .content("Content...")
                .userId(123456L)
                .category(Category.ALUMNI)
                .createdAt(LocalDateTime.now())
                .state(State.DRAFTED)
                .build();

        assertNotNull(post);
        assertEquals("Post title", post.getTitle());
        assertEquals("Content...", post.getContent());
        assertEquals(123456L, post.getUserId());
        assertEquals(Category.ALUMNI, post.getCategory());
        assertNotNull(post.getCreatedAt());
        assertEquals(State.DRAFTED, post.getState());
    }

    @Test
    public void applicationContext_shouldLoadSuccessfully() {
        assertDoesNotThrow(() -> PostServiceApplication.main(new String[] {}));
    }



    @Test
    public void getAllPosts_shouldReturnListOfRequestedPosts() throws Exception {
        List<Post> expectedPosts = postRepository.findAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getPostById_shouldReturnRequestedPost() throws Exception {
        Post expectedPost = postRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + expectedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedPost)));
    }

    @Test
    public void getPostById_withInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllPostCategories_shouldReturnListOfCategories() throws Exception {
        List<Category> expectedCategories = List.of(Category.values());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/category"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedCategories)));
    }

    @Test
    public void getPublishedPosts_shouldReturnListOfPublishedPosts() throws Exception {
        List<Post> expectedPosts = postRepository.findByState(State.PUBLISHED);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/published"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getSubmittedPosts_shouldReturnListOfSubmittedPosts() throws Exception {
        List<Post> expectedPosts = postRepository.findByState(State.SUBMITTED);
        Long userId = 10L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/submitted")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getSubmittedPosts_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Long userId = 5L;
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/submitted")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getPostByIdWithReviews_shouldReturnRequestedPostWithReviews() throws Exception {
        Post expectedPost = postRepository.findAll().get(0);

        List<Review> expectedReviews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Review review = Review.builder()
                    .id((long) i)
                    .userId(expectedPost.getUserId() + 1)
                    .postId(expectedPost.getId())
                    .content("Comment")
                    .createdAt(LocalDateTime.now())
                    .type("REJECTION")
                    .build();
            expectedReviews.add(review);
        }

        expectedPost.setReviews(expectedReviews);

        String userRole = "editor";
        Mockito.when(reviewClient.getReviewsByPostId(expectedPost.getId(), userRole )).thenReturn(expectedReviews);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + expectedPost.getId() + "/with-reviews")
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedPost)));
    }

    @Test
    public void getPostByIdWithReviews_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post expectedPost = postRepository.findAll().get(0);
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + expectedPost.getId() + "/with-reviews")
                        .header("userRole", userRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getPostsByUserId_shouldReturnListOfRequestedPosts() throws Exception {
        Long userId = 5L;
        String userRole = "editor";

        List<Post> expectedPosts = postRepository.findByUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/mine")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void addPost_shouldCreateDraftedPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .category(Category.ALUMNI)
                .build();

        Long userId = 5L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

        assertEquals(11, postRepository.findAll().size());
    }

    @Test
    public void addPost_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .category(Category.ALUMNI)
                .build();

        Long userId = 5L;
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitPost_shouldChangeStateToSubmittedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.DRAFTED);
        postRepository.save(post);

        Long userId = post.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/submit")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(State.SUBMITTED, updatedPost.getState());
    }

    @Test
    public void submitPost_withInvalidId_shouldReturnNotFound() throws Exception {
        Long userId = 5L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + 9999 + "/submit")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isNotFound());
    }

    @Test
    public void submitPost_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.DRAFTED);
        postRepository.save(post);

        Long userId = post.getUserId();
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/submit")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitPost_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.DRAFTED);
        postRepository.save(post);

        Long userId = post.getUserId() + 1;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/submit")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitPost_withInvalidState_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);
        Long userId = post.getUserId();
        String userRole = "editor";

        List<State> invalidStates = List.of(State.SUBMITTED, State.REJECTED, State.APPROVED, State.PUBLISHED);

        for (State state : invalidStates) {
            post.setState(state);
            postRepository.save(post);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/submit")
                            .header("userId", userId)
                            .header("userRole", userRole))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void publishPost_shouldChangeStateToPublishedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.APPROVED);
        postRepository.save(post);

        Long userId = post.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/publish")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(State.PUBLISHED, updatedPost.getState());
    }
    @Test
    public void publishPost_withInvalidId_shouldReturnNotFound() throws Exception {
        Long userId = 5L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + 9999 + "/publish")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isNotFound());
    }

    @Test
    public void publishPost_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.APPROVED);
        postRepository.save(post);

        Long userId = post.getUserId();
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/publish")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void publishPost_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.APPROVED);
        postRepository.save(post);

        Long userId = post.getUserId() + 1;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/publish")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void publishPost_withInvalidState_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);
        Long userId = post.getUserId();
        String userRole = "editor";

        List<State> invalidStates = List.of(State.DRAFTED, State.SUBMITTED, State.REJECTED, State.PUBLISHED);

        for (State state : invalidStates) {
            post.setState(state);
            postRepository.save(post);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/post/" + post.getId() + "/publish")
                            .header("userId", userId)
                            .header("userRole", userRole))
                    .andExpect(status().isBadRequest());
        }
    }


    @Test
    public void updatePost_shouldChangeStateToSubmittedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(0);

        String content = "Updated content...";
        PostRequest postRequest = PostRequest.builder()
                .content(content)
                .build();

        Long userId = post.getUserId();
        String userRole = "editor";

        List<State> validStates = List.of(State.DRAFTED, State.REJECTED);

        for (State state : validStates) {
            post.setState(state);
            postRepository.save(post);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postRequest)))
                    .andExpect(status().isOk());

            Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
            assertEquals(content, updatedPost.getContent());
        }
    }

    @Test
    public void updatePost_withInvalidId_shouldReturnNotFound() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .content("Updated content...")
                .build();

        Long userId = 5L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + 9999)
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePost_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .content("Updated content...")
                .build();

        Long userId = post.getUserId();
        String userRole = "user";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePost_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .content("Updated content...")
                .build();

        Long userId = post.getUserId() + 1;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePost_withInvalidState_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .content("Updated content...")
                .build();

        Long userId = post.getUserId();
        String userRole = "editor";

        List<State> invalidStates = List.of(State.SUBMITTED, State.APPROVED, State.PUBLISHED);

        for (State state : invalidStates) {
            post.setState(state);
            postRepository.save(post);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}
