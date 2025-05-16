package ua.aleh1s.postsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.aleh1s.postsservice.config.AuthTokenInterceptor;
import ua.aleh1s.postsservice.dto.Subscription;

import java.util.List;

@FeignClient(name = "subscriptions", configuration = AuthTokenInterceptor.class)
public interface SubscriptionsApiClient {
    @GetMapping("/api/v1/subscriptions")
    List<Subscription> getSubscriptions(@RequestParam String subscriberId);
}
