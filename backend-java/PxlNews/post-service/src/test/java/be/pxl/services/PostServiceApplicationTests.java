package be.pxl.services;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class PostServiceApplicationTests {
    @Test
    public void applicationContext_shouldLoadSuccessfully() {
        assertDoesNotThrow(() -> PostServiceApplication.main(new String[] {}));
    }
}
