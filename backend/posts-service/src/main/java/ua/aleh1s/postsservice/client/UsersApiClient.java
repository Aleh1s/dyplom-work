package ua.aleh1s.postsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ua.aleh1s.postsservice.config.AuthTokenInterceptor;
import ua.aleh1s.postsservice.dto.UserDto;
import ua.aleh1s.postsservice.dto.UserProfile;

import java.util.List;
import java.util.Set;

@FeignClient(name = "users", configuration = AuthTokenInterceptor.class)
public interface UsersApiClient {
    @GetMapping("/api/v1/users/{username}")
    UserProfile getUserProfileByUsername(@PathVariable String username);

    @GetMapping("/api/v1/users")
    List<UserDto> getUsers(@RequestParam String usernameLike, @RequestParam Set<String> ids);
}
