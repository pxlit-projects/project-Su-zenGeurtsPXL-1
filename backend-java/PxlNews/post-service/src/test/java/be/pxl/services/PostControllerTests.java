package be.pxl.services;

import be.pxl.services.client.CommentClient;
import be.pxl.services.client.ReviewClient;
import be.pxl.services.domain.*;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.repository.NotificationRepository;
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
public class PostControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private ReviewClient reviewClient;

    @MockBean
    private CommentClient commentClient;

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
        notificationRepository.deleteAll();

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

        for (int i = 0; i < 10; i++) {
            Notification notification = Notification.builder()
                    .postId((long)(i))
                    .receiverId((long)(i))
                    .executorId((long)(i+ 1))
                    .content("Content...")
                    .action("Action")
                    .executedAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }
    }

    @Test
    public void getAllPosts_shouldReturnListOfRequestedPosts() throws Exception {
        List<Post> expectedPosts = postRepository.findAll();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getMyPosts_shouldReturnListOfRequestedPosts() throws Exception {
        long userId = 5L;
        String userRole = "editor";

        List<Post> expectedPosts = postRepository.findByUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/mine")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getPublishedPosts_shouldReturnListOfPublishedPosts() throws Exception {
        List<Post> expectedPosts = postRepository.findByState(State.PUBLISHED);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/published"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedPosts)));
    }

    @Test
    public void getReviewablePosts_shouldReturnListOfSubmittedPosts() throws Exception {
        long userId = 10L;
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/reviewable")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isOk());
    }

    @Test
    public void getPostById_shouldReturnRequestedPost() throws Exception {
        Post expectedPost = postRepository.findAll().get(0);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + expectedPost.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedPost)));
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
    public void getPostByIdWithComments_shouldReturnRequestedPostWithComments() throws Exception {
        Post expectedPost = postRepository.findAll().get(0);

        List<Comment> expectedComments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Comment comment = Comment.builder()
                    .id((long) i)
                    .userId(expectedPost.getUserId() + 1)
                    .postId(expectedPost.getId())
                    .content("Comment")
                    .createdAt(LocalDateTime.now())
                    .build();
            expectedComments.add(comment);
        }

        expectedPost.setComments(expectedComments);

        Mockito.when(commentClient.getCommentsByPostId(expectedPost.getId())).thenReturn(expectedComments);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + expectedPost.getId() + "/with-comments"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedPost)));
    }

    @Test
    public void getAllPostCategories_shouldReturnListOfCategories() throws Exception {
        List<Category> expectedCategories = List.of(Category.values());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/category"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedCategories)));
    }

    @Test
    public void getMyNotifications_shouldReturnListOfRequestedPosts() throws Exception {
        long userId = 2L;
        String userRole = "editor";

        List<Notification> expectedNotifications = notificationRepository.findByReceiverId(userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/notification/mine")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedNotifications)));
    }

    @Test
    public void addPost_shouldCreateDraftedPost() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Post title")
                .content("Content...")
                .category(Category.ALUMNI)
                .build();

        long userId = 5L;
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
    public void editPost_shouldChangeContentOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(0);

        String content = "Updated content...";

        long userId = post.getUserId();
        String userRole = "editor";

        List<State> validStates = List.of(State.DRAFTED, State.REJECTED);

        for (State state : validStates) {
            post.setState(state);
            postRepository.save(post);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId())
                            .header("userId", userId)
                            .header("userRole", userRole)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(status().isNoContent());

            Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
            assertEquals(content, updatedPost.getContent());
        }
    }

    @Test
    public void submitPost_shouldChangeStateToSubmittedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(0);
        long userId = post.getUserId();
        String userRole = "editor";

        List<State> validStates = List.of(State.DRAFTED, State.REJECTED);

        for (State state : validStates) {
            post.setState(state);
            postRepository.save(post);


            mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId() + "/submit")
                            .header("userId", userId)
                            .header("userRole", userRole))
                    .andExpect(status().isNoContent());

            Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
            assertEquals(State.SUBMITTED, updatedPost.getState());
        }
    }

    @Test
    public void publishPost_shouldChangeStateToPublishedOfRequestedPost() throws Exception {
        Post post = postRepository.findAll().get(0);
        post.setState(State.APPROVED);
        postRepository.save(post);

        long userId = post.getUserId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/" + post.getId() + "/publish")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isNoContent());

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(State.PUBLISHED, updatedPost.getState());
    }

    @Test
    public void markNotificationAsRead_shouldChangeIsReadToTrueOfRequestedNotification() throws Exception {
        Notification notification = notificationRepository.findAll().get(0);
        notification.setIsRead(false);
        notificationRepository.save(notification);

        long userId = notification.getReceiverId();
        String userRole = "editor";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/notification/" + notification.getId() + "/read")
                        .header("userId", userId)
                        .header("userRole", userRole))
                .andExpect(status().isNoContent());

        Notification updatedNotification = notificationRepository.findById(notification.getId()).orElseThrow();
        assertTrue(updatedNotification.getIsRead());
    }
}
