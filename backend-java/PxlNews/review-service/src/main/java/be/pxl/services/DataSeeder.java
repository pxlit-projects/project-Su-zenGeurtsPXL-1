package be.pxl.services;

import be.pxl.services.domain.Review;
import be.pxl.services.domain.Type;
import be.pxl.services.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ReviewRepository reviewRepository;
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    public DataSeeder(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        if (reviewRepository.count() == 0) {
            logger.info("Seeding reviews");

            Review review1 = Review.builder()
                    .userId(1L)
                    .postId(1L)
                    .content("Focus on the results.")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 9, 0, 0))
                    .type(Type.REJECTION)
                    .build();

            reviewRepository.save(review1);

            Review review2 = Review.builder()
                    .userId(5L)
                    .postId(1L)
                    .content("And implement more details.")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1, 9, 5, 0))
                    .type(Type.COMMENT)
                    .build();

            reviewRepository.save(review2);

            Review review3 = Review.builder()
                    .userId(5L)
                    .postId(1L)
                    .content("Great!")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1,15,35,0))
                    .type(Type.APPROVAL)
                    .build();

            reviewRepository.save(review3);

            Review review4 = Review.builder()
                    .userId(7L)
                    .postId(1L)
                    .content("I also love it!")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1,15,53,0))
                    .type(Type.COMMENT)
                    .build();

            reviewRepository.save(review4);

            Review review5 = Review.builder()
                    .userId(3L)
                    .postId(1L)
                    .content("Well done!")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 1,15,58,0))
                    .type(Type.COMMENT)
                    .build();

            reviewRepository.save(review5);

            Review review6 = Review.builder()
                    .userId(6L)
                    .postId(3L)
                    .content("This is way too short!")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 5,16,28,0))
                    .type(Type.REJECTION)
                    .build();

            reviewRepository.save(review6);

            Review review7 = Review.builder()
                    .userId(1L)
                    .postId(4L)
                    .content("Also focus on the reason why.")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 14,17,13,0))
                    .type(Type.REJECTION)
                    .build();

            reviewRepository.save(review7);

            Review review8 = Review.builder()
                    .userId(7L)
                    .postId(6L)
                    .content("This is way too short!")
                    .createdAt(LocalDateTime.of(2024, Month.DECEMBER, 8,15,42,0))
                    .type(Type.REJECTION)
                    .build();

            reviewRepository.save(review8);
        }
    }
}

