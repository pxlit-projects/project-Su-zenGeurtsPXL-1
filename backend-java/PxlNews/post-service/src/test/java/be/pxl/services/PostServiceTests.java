package be.pxl.services;

import be.pxl.services.domain.Notification;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.State;
import be.pxl.services.repository.NotificationRepository;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.service.IPostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostServiceTests {
    @Autowired
    private IPostService postService;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @Test
    public void listen_withRejection_shouldCreateNotification_andUpdatePostContentStateToRejected() {
        String message = "{\"reviewerId\":2,\"" +
                "executedAt\":\"2024-12-29T20:36:52.5590569\"," +
                "\"reviewType\":\"REJECTION\"," +
                "\"comment\":\"Content\"," +
                "\"postId\":3," +
                "\"reviewId\":102}";

        Post post = new Post();
        post.setState(State.SUBMITTED);
        post.setUserId(1L);

        Mockito.when(postRepository.findById(3L)).thenReturn(Optional.of(post));

        Notification expectedNotification = new Notification(null, 3L, 1L, 2L, "Content", "REJECTION", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);
        Notification savedNotification = new Notification(1L, 3L, 1L, 2L,"Content", "REJECTION", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);

        Mockito.when(notificationRepository.save(expectedNotification)).thenReturn(savedNotification);

        Post savedPost = new Post();
        savedPost.setId(3L);
        savedPost.setUserId(1L);
        savedPost.setState(State.REJECTED);
        Mockito.when(postRepository.save(savedPost)).thenReturn(savedPost);

        assertDoesNotThrow(() -> postService.listen(message));
    }

    @Test
    public void listen_withApproval_shouldCreateNotification_andUpdatePostContentStateToApproved() {
        String message = "{\"reviewerId\":2,\"" +
                "executedAt\":\"2024-12-29T20:36:52.5590569\"," +
                "\"reviewType\":\"APPROVAL\"," +
                "\"comment\":\"Content\"," +
                "\"postId\":3," +
                "\"reviewId\":102}";

        Post post = new Post();
        post.setState(State.SUBMITTED);
        post.setUserId(1L);

        Mockito.when(postRepository.findById(3L)).thenReturn(Optional.of(post));

        Notification expectedNotification = new Notification(null, 3L, 1L, 2L, "Content", "APPROVAL", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);
        Notification savedNotification = new Notification(1L, 3L, 1L, 2L,"Content", "APPROVAL", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);

        Mockito.when(notificationRepository.save(expectedNotification)).thenReturn(savedNotification);

        Post savedPost = new Post();
        savedPost.setId(3L);
        savedPost.setUserId(1L);
        savedPost.setState(State.APPROVED);
        Mockito.when(postRepository.save(savedPost)).thenReturn(savedPost);

        assertDoesNotThrow(() -> postService.listen(message));
    }

    @Test
    public void listen_withInvalidMessage_shouldCreateNotification_andUpdatePostContentStateToApproved() {
        String message = "This is an invalid message.";

        Post post = new Post();
        post.setState(State.SUBMITTED);
        post.setUserId(1L);

        Mockito.when(postRepository.findById(3L)).thenReturn(Optional.of(post));

        Notification expectedNotification = new Notification(null, 3L, 1L, 2L, "Content", "APPROVAL", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);
        Notification savedNotification = new Notification(1L, 3L, 1L, 2L,"Content", "APPROVAL", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);

        Mockito.when(notificationRepository.save(expectedNotification)).thenReturn(savedNotification);

        Post savedPost = new Post();
        savedPost.setId(3L);
        savedPost.setUserId(1L);
        savedPost.setState(State.APPROVED);
        Mockito.when(postRepository.save(savedPost)).thenReturn(savedPost);

        assertDoesNotThrow(() -> postService.listen(message));
    }

    @Test
    public void listen_withInvalidReviewType_shouldCreateNotification_andUpdatePostContentStateToApproved() {
        String message = "{\"reviewerId\":2,\"" +
                "executedAt\":\"2024-12-29T20:36:52.5590569\"," +
                "\"reviewType\":\"INVALID\"," +
                "\"comment\":\"Content\"," +
                "\"postId\":3," +
                "\"reviewId\":102}";

        Post post = new Post();
        post.setState(State.SUBMITTED);
        post.setUserId(1L);

        Mockito.when(postRepository.findById(3L)).thenReturn(Optional.of(post));

        Notification expectedNotification = new Notification(null, 3L, 1L, 2L, "Content", "APPROVAL", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);
        Notification savedNotification = new Notification(1L, 3L, 1L, 2L,"Content", "APPROVAL", LocalDateTime.of(2024, 12, 29, 20, 36, 52, 5590569), false);

        Mockito.when(notificationRepository.save(expectedNotification)).thenReturn(savedNotification);

        Post savedPost = new Post();
        savedPost.setId(3L);
        savedPost.setUserId(1L);
        savedPost.setState(State.APPROVED);
        Mockito.when(postRepository.save(savedPost)).thenReturn(savedPost);

        assertDoesNotThrow(() -> postService.listen(message));
    }

    @Test
    public void checksUserRole_withInvalidUserRole_shouldThrowAResponseStatusException() {
        String userRole = "user";

        assertThrows(ResponseStatusException.class, () -> postService.checksUserRole(userRole));
    }

    @Test
    public void checksToUpdatePost_Content_withInvalidId_shouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = 1L;
        boolean ownerIsAllowed = false;
        State[] validStates = {State.DRAFTED};

        Mockito.when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> postService.checksToUpdatePost(id, userId, ownerIsAllowed, validStates));
    }

    @Test
    public void checksToUpdatePost_Content_withOwnerNotAllowed_shouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = 1L;
        boolean ownerIsAllowed = false;
        State[] validStates = {State.DRAFTED};

        Post post = new Post();
        post.setUserId(userId);

        Mockito.when(postRepository.findById(id)).thenReturn(Optional.of(post));

        assertThrows(ResponseStatusException.class, () -> postService.checksToUpdatePost(id, userId, ownerIsAllowed, validStates));
    }

    @Test
    public void checksToUpdatePost_Content_withInvalidState_shouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = 1L;
        boolean ownerIsAllowed = false;
        State[] validStates = {State.DRAFTED};

        Post post = new Post();
        post.setUserId(userId + 1);
        post.setState(State.SUBMITTED);

        Mockito.when(postRepository.findById(id)).thenReturn(Optional.of(post));

        assertThrows(ResponseStatusException.class, () -> postService.checksToUpdatePost(id, userId, ownerIsAllowed, validStates));
    }

    @Test
    public void checksToUpdateNotification_withInvalidId_shouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = 1L;

        Mockito.when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> postService.checksToUpdateNotification(id, userId));
    }

    @Test
    public void checksToUpdateNotification_withInvalidUserId_shouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = 1L;

        Notification notification = new Notification();
        notification.setReceiverId(userId + 1);

        Mockito.when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        assertThrows(ResponseStatusException.class, () -> postService.checksToUpdateNotification(id, userId));
    }

    @Test
    public void checksToUpdateNotification_withNotificationAlreadyRead_shouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = 1L;

        Notification notification = new Notification();
        notification.setReceiverId(userId);
        notification.setIsRead(true);

        Mockito.when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        assertThrows(ResponseStatusException.class, () -> postService.checksToUpdateNotification(id, userId));
    }
}
