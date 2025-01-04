package be.pxl.services;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewTests {

    @Test
    public void reviewBuilder_shouldBeImplemented() {
        Review review = Review.builder()
                .userId(123456L)
                .postId(123456L)
                .content("Content...")
                .createdAt(LocalDateTime.now())
                .type(Type.APPROVAL)
                .build();

        assertNotNull(review);
        assertEquals(123456L, review.getUserId());
        assertEquals(123456L, review.getPostId());
        assertEquals("Content...", review.getContent());
        assertNotNull(review.getCreatedAt());
        assertEquals(Type.APPROVAL, review.getType());
    }

    @Test
    public void review_shouldBeCreatedCorrectly() {
        Review review = new Review();
        review.setUserId(123456L);
        review.setPostId(123456L);
        review.setContent("Content...");
        review.setCreatedAt(LocalDateTime.now());
        review.setType(Type.APPROVAL);

        assertNotNull(review);
        assertEquals(123456L, review.getUserId());
        assertEquals(123456L, review.getPostId());
        assertEquals("Content...", review.getContent());
        assertNotNull(review.getCreatedAt());
        assertEquals(Type.APPROVAL, review.getType());
    }
}
