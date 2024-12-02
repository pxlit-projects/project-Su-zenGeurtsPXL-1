package be.pxl.services.domain.dto;

import be.pxl.services.domain.Category;
import be.pxl.services.domain.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Category category;
    private LocalDateTime createdAt;
    private State state;
}
