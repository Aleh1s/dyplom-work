package ua.aleh1s.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ua.aleh1s.paymentservice.config.AuthTokenInterceptor;
import ua.aleh1s.paymentservice.dto.SubscribeRequestDto;
import ua.aleh1s.paymentservice.dto.SubscriptionDto;
import ua.aleh1s.paymentservice.dto.SubscriptionPlanDto;

@FeignClient(name = "subscriptions", configuration = AuthTokenInterceptor.class)
public interface SubscriptionsClient {
    @GetMapping("/api/v1/subscriptions/plan")
    SubscriptionPlanDto getSubscriptionPlan(@RequestParam String userId);

    @PostMapping("/api/v1/subscriptions/subscribe")
    SubscriptionDto subscribe(@RequestBody SubscribeRequestDto subscribeRequest);
}
