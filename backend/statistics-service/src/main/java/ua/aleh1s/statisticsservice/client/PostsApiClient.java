package ua.aleh1s.statisticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.aleh1s.statisticsservice.config.AuthTokenInterceptor;
import ua.aleh1s.statisticsservice.dto.PostsStatistics;
import ua.aleh1s.statisticsservice.dto.UserPostAnalytics;

import java.time.Instant;

@FeignClient(name = "posts", configuration = AuthTokenInterceptor.class)
public interface PostsApiClient {
    @GetMapping("/api/v1/posts/statistics")
    PostsStatistics getPostsStatistics(@RequestParam String ownerId);

    @GetMapping("/api/v1/posts/analytics")
    UserPostAnalytics getAnalytics(
            @RequestParam String userId,
            @RequestParam Instant from,
            @RequestParam Instant to
    );
}
