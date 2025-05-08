package ua.aleh1s.postsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ua.aleh1s.postsservice.config.AuthTokenInterceptor;
import ua.aleh1s.postsservice.dto.ContentDto;
import ua.aleh1s.postsservice.dto.SearchContentRequest;

import java.util.List;

@FeignClient(name = "content", configuration = AuthTokenInterceptor.class)
public interface ContentApiClient {
    @GetMapping("/api/v1/content/{id}")
    ContentDto getContentById(@PathVariable String id);
    @GetMapping("/api/v1/content")
    List<ContentDto> getAllBySearchRequest(@SpringQueryMap SearchContentRequest request);
}
