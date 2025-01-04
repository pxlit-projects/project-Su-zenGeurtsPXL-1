package be.pxl.services;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class ReviewServiceApplicationTests {
    @Test
    public void applicationContext_shouldLoadSuccessfully() {
        assertDoesNotThrow(() -> ReviewServiceApplication.main(new String[]{}));
    }
}
