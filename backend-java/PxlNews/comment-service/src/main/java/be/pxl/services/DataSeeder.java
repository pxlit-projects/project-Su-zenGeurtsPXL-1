package be.pxl.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class DataSeeder implements CommandLineRunner {
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void run(String... args) {
        if (commentRepository.count() == 0) {
            logger.info("Seeding comments");

            Comment review1 = Comment.builder()
                    .userId(10L)
                    .postId(2L)
                    .content("Amazing article!")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 3,9,15,0))
                    .build();

            commentRepository.save(review1);

            Comment review2 = Comment.builder()
                    .userId(8L)
                    .postId(2L)
                    .content("Love reading your articles.")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 3,12,24,0))
                    .build();

            commentRepository.save(review2);

            Comment review3 = Comment.builder()
                    .userId(1L)
                    .postId(2L)
                    .content("Thankyou :)")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 4,8,8,0))
                    .build();

            commentRepository.save(review3);

            Comment review4 = Comment.builder()
                    .userId(4L)
                    .postId(2L)
                    .content("Interesting")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 6,11,26,0))
                    .build();

            commentRepository.save(review4);

            Comment review5 = Comment.builder()
                    .userId(9L)
                    .postId(5L)
                    .content("Love this!")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 10,10,37))
                    .build();

            commentRepository.save(review5);

            Comment review6 = Comment.builder()
                    .userId(7L)
                    .postId(5L)
                    .content("Thanks for writing about it :)")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 10,11,15,0))
                    .build();

            commentRepository.save(review6);

            Comment review7 = Comment.builder()
                    .userId(4L)
                    .postId(5L)
                    .content("It was not easy…")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 11,11,53))
                    .build();

            commentRepository.save(review7);

            Comment review8 = Comment.builder()
                    .userId(2L)
                    .postId(5L)
                    .content("Can you write about the opening as well?")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 15,12,18))
                    .build();

            commentRepository.save(review8);
        }
    }
}
