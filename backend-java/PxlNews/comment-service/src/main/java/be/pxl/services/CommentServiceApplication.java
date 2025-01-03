package be.pxl.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CommentServiceApplication
{
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceApplication.class);

    public static void main( String[] args )
    {
        SpringApplication.run(CommentServiceApplication.class, args);
        logger.info("CommentServiceApplication started");
    }
}
