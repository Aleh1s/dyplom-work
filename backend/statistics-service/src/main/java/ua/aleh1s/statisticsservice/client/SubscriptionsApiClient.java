package ua.aleh1s.statisticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.aleh1s.statisticsservice.config.AuthTokenInterceptor;
import ua.aleh1s.statisticsservice.dto.SubscriptionsStatistics;

@FeignClient(name = "subscriptions", configuration = AuthTokenInterceptor.class)
public interface SubscriptionsApiClient {
    @GetMapping("/api/v1/subscriptions/statistics")
    SubscriptionsStatistics getMonthSubscriptionsStatistics(@RequestParam String userId);
}
