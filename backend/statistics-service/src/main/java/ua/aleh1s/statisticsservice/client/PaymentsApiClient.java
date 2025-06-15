package ua.aleh1s.statisticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.aleh1s.statisticsservice.config.AuthTokenInterceptor;
import ua.aleh1s.statisticsservice.dto.PaymentsAnalytics;
import ua.aleh1s.statisticsservice.dto.PaymentsStatistics;

import java.time.Instant;

@FeignClient(name = "payments", configuration = AuthTokenInterceptor.class)
public interface PaymentsApiClient {

    @GetMapping("/api/v1/payments/statistics")
    PaymentsStatistics getPaymentsStatistics(@RequestParam String subscribedOnId);

    @GetMapping("/api/v1/payments/analytics")
    PaymentsAnalytics getPaymentsAnalytics(@RequestParam String subscribedOnId, @RequestParam Instant from, @RequestParam Instant to);
}
