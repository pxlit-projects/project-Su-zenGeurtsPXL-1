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
            State state = i % 3 == 0 ? State.PUBLISHED : i % 3 == 1 ? State.SUBMITTED : State.DRAFTED;

            Post post = Post.builder()
                    .title("Post " + i)
                    .content("Content...")
                    .userId((long)(i))
                    .category(Category.values()[i % Category.values().length])
                    .createdAt(LocalDateTime.now())
                    .state(state)
                    .build();
            postRepository.save(post);
        }
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

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/submitted"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getPostByIdWithReviews_shouldReturnRequestedPostWithReviews() throws Exception {
        Post expectedPost = postRepository.findAll().get(1);

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
        Mockito.when(reviewClient.getReviewsByPostId(expectedPost.getId())).thenReturn(expectedReviews);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + expectedPost.getId() + "/with-reviews"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedPost)));
    }

    @Test
    public void getPostsByUserId_shouldReturnListOfRequestedPosts() throws Exception {
        long userId = 5;
        List<Post> expectedPosts = postRepository.findByUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void addPost_shouldCreateDraftedPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .userId((long) 123456)
                .userRole("editor")
                .category(Category.ALUMNI)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
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
                .userId((long) 123456)
                .userRole("invalid")
                .category(Category.ALUMNI)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitPost_shouldChangeStateToSubmittedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(2);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/submit/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(State.SUBMITTED, updatedPost.getState());
    }

    @Test
    public void submitPost_withInvalidId_shouldReturnNotFound() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .userId(1L)
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/submit/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void submitPost_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("invalid")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/submit/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitPost_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(2);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId() + 1)
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/submit/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitPost_withStateSubmitted_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(1);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/submit/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void submitPost_withStatePublished_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/submit/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void publishPost_shouldChangeStateToPublishedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(1);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/publish/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(State.PUBLISHED, updatedPost.getState());
    }
    @Test
    public void publishPost_withInvalidId_shouldReturnNotFound() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .userId(1L)
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/publish/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void publishPost_withInvalidUserRole_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(1);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("invalid")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/publish/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void publishPost_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(2);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId() + 1)
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/publish/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void publishPost_withStateDrafted_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(2);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/publish/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void publishPost_withStatePublished_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/publish/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void updatePost_shouldChangeStateToSubmittedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(2);

        String content = "Updated content...";
        PostRequest postRequest = PostRequest.builder()
                .content(content)
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(content, updatedPost.getContent());
    }

    @Test
    public void updatePost_withInvalidId_shouldReturnNotFound() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .content("Content...")
                .userId(123456L)
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updatePost_withInvalidUserTole_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .content("Content...")
                .userId(post.getUserId())
                .userRole("invalid")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePost_withInvalidUserId_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);

        PostRequest postRequest = PostRequest.builder()
                .content("Content...")
                .userId(post.getUserId() + 1)
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePost_withStateSubmitted_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(1);
        post.setState(State.SUBMITTED);
        postRepository.save(post);

        PostRequest postRequest = PostRequest.builder()
                .content("Content...")
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePost_withStatePublished_shouldReturnBadRequest() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.PUBLISHED);
        postRepository.save(post);

        PostRequest postRequest = PostRequest.builder()
                .content("Content...")
                .userId(post.getUserId())
                .userRole("editor")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }
}
