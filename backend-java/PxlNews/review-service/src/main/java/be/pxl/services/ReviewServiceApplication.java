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
public class ReviewServiceApplication
{
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceApplication.class);

    public static void main( String[] args )
    {
        SpringApplication.run(ReviewServiceApplication.class, args);
        logger.info("ReviewServiceApplication started");
    }
}