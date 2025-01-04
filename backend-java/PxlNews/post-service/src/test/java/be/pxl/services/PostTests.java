package be.pxl.services;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.State;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PostTests {
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
    public void post_shouldBeCreatedCorrectly() {
        Post post = new Post();
        post.setTitle("Post title");
        post.setContent("Content...");
        post.setUserId(123456L);
        post.setCategory(Category.ALUMNI);
        post.setCreatedAt(LocalDateTime.now());
        post.setState(State.DRAFTED);

        assertNotNull(post);
        assertEquals("Post title", post.getTitle());
        assertEquals("Content...", post.getContent());
        assertEquals(123456L, post.getUserId());
        assertEquals(Category.ALUMNI, post.getCategory());
        assertNotNull(post.getCreatedAt());
        assertEquals(State.DRAFTED, post.getState());

    }
}
