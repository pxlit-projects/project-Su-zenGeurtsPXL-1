package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;
import be.pxl.services.domain.dto.ReviewRequest;
import be.pxl.services.repository.ReviewRepository;
import be.pxl.services.service.IReviewService;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ReviewServiceTests {
    @Autowired
    private IReviewService reviewService;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private PostClient postClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    public void checksUserRole_WithInvalidUserRole_ShouldThrowAResponseStatusException() {
        String userRole = "user";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reviewService.checksUserRole(userRole));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("User is not an editor.", exception.getReason());
    }

    @Test
    public void createReview_WithInvalidPostId_ShouldThrowAResponseStatusException() {
        long postId = 1L;
        long userId = 1L;
        String userRole = "editor";
        String mail = "john.doe@gmail.com";
        Type type = Type.APPROVAL;
        String[] validStates = {"SUBMITTED"};
        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        Mockito.when(postClient.getPostById(postId)).thenThrow(new FeignException.NotFound(
                "Post not found",
                Request.create(Request.HttpMethod.GET, "/api/post/" + postId,
                        java.util.Collections.emptyMap(), null, StandardCharsets.UTF_8),
                null, null
        ));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reviewService.createReview(reviewRequest, userId, userRole, mail, type, validStates));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Post with id " + postId + " not found.", exception.getReason());
    }

    @Test
    public void createReview_WithInvalidUserId_ShouldThrowAResponseStatusException() {
        long postId = 1L;
        long userId = 1L;
        String userRole = "editor";
        String mail = "john.doe@gmail.com";
        Type type = Type.APPROVAL;
        String[] validStates = {"SUBMITTED"};

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        Post post = new Post();
        post.setUserId(1L);
        Mockito.when(postClient.getPostById(postId)).thenReturn(post);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reviewService.createReview(reviewRequest, userId, userRole, mail, type, validStates));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("User with id " + userId + " cannot review own post.", exception.getReason());
    }

    @Test
    public void createReview_WithPostHasInvalidState_ShouldThrowAResponseStatusException() {
        long postId = 1L;
        long userId = 2L;
        String userRole = "editor";
        String mail = "john.doe@gmail.com";
        Type type = Type.APPROVAL;
        String[] validStates = {"SUBMITTED"};

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        Post post = new Post();
        post.setUserId(postId);
        post.setState("DRAFTED");
        Mockito.when(postClient.getPostById(postId)).thenReturn(post);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> reviewService.createReview(reviewRequest, userId, userRole, mail, type, validStates));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Post with id " + postId + " does not have the right state.", exception.getReason());
    }


    @Test
    public void createReview_ShouldCreateReview_andSendMessageToReviewQueue() {
        long postId = 1L;
        long userId = 2L;
        String userRole = "editor";
        String mail = "john.doe@gmail.com";
        Type type = Type.APPROVAL;
        String[] validStates = {"SUBMITTED"};

        ReviewRequest reviewRequest = ReviewRequest.builder()
                .postId(postId)
                .content("Content...")
                .build();

        Post post = new Post();
        post.setUserId(postId);
        post.setState("SUBMITTED");
        Mockito.when(postClient.getPostById(postId)).thenReturn(post);

        Review savedReview = new Review(1L, userId, postId, reviewRequest.getContent(), LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), type);

        Mockito.when(reviewRepository.save(any())).thenReturn(savedReview);

        String expectedMessage = "{\"reviewerId\":2,\"" +
                "executedAt\":\"2024-12-29T20:36:52.005590569\"," +
                "\"reviewType\":\"APPROVAL\"," +
                "\"comment\":\"Content...\"," +
                "\"postId\":1," +
                "\"reviewId\":1}";

        assertDoesNotThrow(() -> reviewService.createReview(reviewRequest, userId, userRole, mail, type, validStates));
        verify(rabbitTemplate, times(1)).convertAndSend("reviewQueue", expectedMessage);
    }
}
