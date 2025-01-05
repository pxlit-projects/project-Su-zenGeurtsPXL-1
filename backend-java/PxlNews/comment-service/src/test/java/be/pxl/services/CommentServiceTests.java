package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.Post;
import be.pxl.services.repository.CommentRepository;
import be.pxl.services.service.ICommentService;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentServiceTests {
    @Autowired
    private ICommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private PostClient postClient;

    @Test
    public void checksUserRole_WithValidUserRole_ShouldSucceed() {
        assertDoesNotThrow(() -> commentService.checksUserRole("editor"));
        assertDoesNotThrow(() -> commentService.checksUserRole("user"));
    }

    @Test
    public void checksUserRole_WithInvalidUserRole_ShouldThrowAResponseStatusException() {
        String userRole = "invalid";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> commentService.checksUserRole(userRole));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("User is not logged in.", exception.getReason());
    }

    @Test
    public void checkPost_withInvalidId_ShouldThrowAResponseStatusException() {
        long id = 1L;

        Mockito.when(postClient.getPostById(id)).thenThrow(new FeignException.NotFound(
                "Post not found",
                Request.create(Request.HttpMethod.GET, "/api/post/" + id,
                        java.util.Collections.emptyMap(), null, StandardCharsets.UTF_8),
                null, null
        ));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> commentService.checkPost(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Post with id " + id + " not found.", exception.getReason());
    }

    @Test
    public void checkPost_withInvalidState_ShouldThrowAResponseStatusException() {
        long id = 1L;

        Post post = new Post();
        post.setState("DRAFTED");

        Mockito.when(postClient.getPostById(id)).thenReturn(post);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> commentService.checkPost(id));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Post with id " + id + " is not published yet.", exception.getReason());
    }

    @Test
    public void checkComment_withInvalidId_ShouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = id + 1;

        Mockito.when(commentRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> commentService.checkComment(id, userId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Comment with id " + id + " not found", exception.getReason());
    }

    @Test
    public void checkComment_withInvalidUserId_ShouldThrowAResponseStatusException() {
        long id = 1L;
        long userId = id + 1;

        Comment comment = new Comment();
        comment.setUserId(id);

        Mockito.when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> commentService.checkComment(id, userId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals( "User with id " + userId + " does not have access to this comment", exception.getReason());
    }
}
