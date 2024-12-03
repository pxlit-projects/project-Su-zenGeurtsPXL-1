package be.pxl.services;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.State;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
                    .userId((long) (i + 1))
                    .category(Category.values()[i % Category.values().length])
                    .createdAt(LocalDateTime.now())
                    .state(State.DRAFTED)
                    .build();
            postRepository.save(post);
        }
    }

    @Test
    public void applicationContext_shouldLoadSuccessfully() {
        assertDoesNotThrow(() -> PostServiceApplication.main(new String[] {}));
    }

    @Test
    public void getAllPosts_shouldReturnListOfPosts() throws Exception {
        List<Post> expectedPosts = postRepository.findAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getPostById_shouldReturnPost() throws Exception {
        List<Post> posts = postRepository.findAll();
        Post expectedPost = posts.get(0);
        int id = expectedPost.getId().intValue();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + id))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedPost)));
    }

    @Test
    public void getPostById_postIdDoesNotExist_ShouldReturnNotFound() throws Exception {
        int nonExistentId = 9999;

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllPostCategories_shouldReturnAllCategories() throws Exception {
        List<Category> expectedCategories = List.of(Category.values());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/category"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedCategories)));
    }

    @Test
    public void addPost_withoutState_shouldCreateDraftedPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .userId((long) 123456)
                .category(Category.ALUMNI)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

        assertEquals(11, postRepository.findAll().size());
    }

    @Test
    public void addPost_withState_DRAFTED_shouldCreateDraftedPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .userId((long) 123456)
                .category(Category.ALUMNI)
                .state("DRAFTED")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

        assertEquals(11, postRepository.findAll().size());
    }

    @Test
    public void addPost_withState_SUBMITTED_shouldCreateSubmittedPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .userId((long) 123456)
                .category(Category.ALUMNI)
                .state("SUBMITTED")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated());

        assertEquals(11, postRepository.findAll().size());
    }

    @Test
    public void addPost_withState_PUBLISHED_shouldReturnBadRequest() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .userId((long) 123456)
                .category(Category.ALUMNI)
                .state("PUBLISHED")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addPost_stateDoesNotExist_shouldReturnBadRequest() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .userId((long) 123456)
                .category(Category.ALUMNI)
                .state("NOSUCHSTATE")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isBadRequest());
    }
}
