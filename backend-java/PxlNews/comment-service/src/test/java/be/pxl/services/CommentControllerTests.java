package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.repository.CommentRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CommentControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostClient postClient;

    @Autowired
    private CommentRepository commentRepository;

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
        commentRepository.deleteAll();

        for (int i = 0; i < 3; i++) {
            Comment comment = Comment.builder()
                    .userId(1L)
                    .postId(1L)
                    .content("Content...")
                    .createdAt(LocalDateTime.now())
                    .build();

            commentRepository.save(comment);
        }
    }

    @Test
    public void getCommentsByPostId_shouldFindCommentsByPostId() throws Exception {
        Comment comment = commentRepository.findAll().get(0);
        long postId = comment.getPostId();

        String userRole = "editor";

        List<Comment> expectedComments = commentRepository.findCommentsByPostId(postId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comment/post/" + postId)
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedComments)));
    }

    @Test
    public void addComment_shouldCreateComment() throws Exception {
        CommentRequest commentRequest = CommentRequest.builder()
                .postId(1L)
                .content("Content...")
                .build();

        long userId = 5L;
        String userRole = "editor";

        Post post = new Post();
        post.setState("PUBLISHED");

        Mockito.when(postClient.getPostById(1L)).thenReturn(post);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment")
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated());

        assertEquals(4, commentRepository.findAll().size());
    }

    @Test
    public void editComment_shouldUpdateContentOfRequestedComment() throws Exception {
        Comment comment = commentRepository.findAll().get(0);

        String content = "Updated content...";

        long userId = comment.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/comment/" + comment.getId())
                        .header("userId", userId)
                        .header("userRole", userRole)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isNoContent());

        Comment updatedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertEquals(content, updatedComment.getContent());
    }

    @Test
    public void removeComment_shouldDeleteRequestedComment() throws Exception {
        Comment comment = commentRepository.findAll().get(0);

        long userId = comment.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/" + comment.getId())
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isNoContent());

        assertEquals(2, commentRepository.findAll().size());
    }

}
