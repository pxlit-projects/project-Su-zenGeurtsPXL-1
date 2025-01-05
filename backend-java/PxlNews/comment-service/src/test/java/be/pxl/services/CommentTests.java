package be.pxl.services;

import be.pxl.services.domain.Comment;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentTests {

    @Test
    public void commentBuilder_shouldBeImplemented() {
        Comment comment = Comment.builder()
                .userId(123456L)
                .postId(123456L)
                .content("Content...")
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(comment);
        assertEquals(123456L, comment.getUserId());
        assertEquals(123456L, comment.getPostId());
        assertEquals("Content...", comment.getContent());
        assertNotNull(comment.getCreatedAt());
    }

    @Test
    public void comment_shouldBeCreatedCorrectly() {
        Comment comment = new Comment();
        comment.setUserId(123456L);
        comment.setPostId(123456L);
        comment.setContent("Content...");
        comment.setCreatedAt(LocalDateTime.now());

        assertNotNull(comment);
        assertEquals(123456L, comment.getUserId());
        assertEquals(123456L, comment.getPostId());
        assertEquals("Content...", comment.getContent());
        assertNotNull(comment.getCreatedAt());
    }
}
