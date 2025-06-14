package ua.aleh1s.postsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class PostsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostsServiceApplication.class, args);
    }
}
