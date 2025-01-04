package be.pxl.services;

import be.pxl.services.domain.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotificationTest {
    @Test
    public void notificationBuilder_shouldBeImplemented() {
        Notification notification = Notification.builder()
                .postId(123456L)
                .receiverId(123456L)
                .executorId(123456L)
                .content("Content...")
                .action("action")
                .executedAt(LocalDateTime.now())
                .isRead(false)
                .build();

        assertNotNull(notification);
        assertEquals(123456L, notification.getPostId());
        assertEquals(123456L, notification.getReceiverId());
        assertEquals(123456L, notification.getExecutorId());
        assertEquals("Content...", notification.getContent());
        assertEquals("action", notification.getAction());
        assertNotNull(notification.getExecutedAt());
        assertFalse(notification.getIsRead());
    }

    @Test
    public void notification_shouldBeCreatedCorrectly() {
        Notification notification = new Notification();
        notification.setPostId(123456L);
        notification.setReceiverId(123456L);
        notification.setExecutorId(123456L);
        notification.setContent("Content...");
        notification.setAction("action");
        notification.setExecutedAt(LocalDateTime.now());
        notification.setIsRead(false);

        assertNotNull(notification);
        assertEquals(123456L, notification.getPostId());
        assertEquals(123456L, notification.getReceiverId());
        assertEquals(123456L, notification.getExecutorId());
        assertEquals("Content...", notification.getContent());
        assertEquals("action", notification.getAction());
        assertNotNull(notification.getExecutedAt());
        assertFalse(notification.getIsRead());
    }
}
