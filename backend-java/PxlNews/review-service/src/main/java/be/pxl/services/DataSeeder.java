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
        }
    }
}

