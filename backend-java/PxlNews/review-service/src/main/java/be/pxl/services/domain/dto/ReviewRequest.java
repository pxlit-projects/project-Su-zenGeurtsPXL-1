package be.pxl.services.domain.dto;

import be.pxl.services.domain.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    private Long userId;
    private String userRole;
    private Long postId;
    private String content;
}