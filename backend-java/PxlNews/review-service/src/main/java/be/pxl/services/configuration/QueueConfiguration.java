package be.pxl.services.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {
    @Bean
    public Queue myQueue() {
        return new Queue("reviewQueue", false);
    }
}