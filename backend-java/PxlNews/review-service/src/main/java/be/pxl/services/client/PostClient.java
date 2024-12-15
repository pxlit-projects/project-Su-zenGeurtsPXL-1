package be.pxl.services.client;

import be.pxl.services.domain.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service")
public interface PostClient {
    @GetMapping(path = "/api/post/{id}")
    Post getPostById(@PathVariable Long id);
}
