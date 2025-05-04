package ua.aleh1s.usersservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.aleh1s.usersservice.config.AuthTokenInterceptor;
import ua.aleh1s.usersservice.dto.Subscription;

@FeignClient(name = "subscriptions", configuration = AuthTokenInterceptor.class)
public interface SubscriptionsClient {
    @GetMapping("/api/v1/subscriptions/active")
    Subscription getActiveSubscription(@RequestParam String subscribedOnId);
}
