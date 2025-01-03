package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long postId;
    private Long receiverId;
    private Long executorId;
    private String content;
    private String action;
    private LocalDateTime executedAt;
    private Boolean isRead;
}
