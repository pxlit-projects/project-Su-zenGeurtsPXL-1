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
public class PostServiceApplication
{
    private static final Logger logger = LoggerFactory.getLogger(PostServiceApplication.class);

    public static void main( String[] args )
    {
        SpringApplication.run(PostServiceApplication.class, args);
        logger.info("PostServiceApplication started");
    }
}
