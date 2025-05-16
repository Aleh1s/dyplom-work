package ua.aleh1s.subscriptionsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.subscriptionsservice.dto.*;
import ua.aleh1s.subscriptionsservice.mapper.SubscriptionMapper;
import ua.aleh1s.subscriptionsservice.service.SubscriptionService;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionMapper subscriptionMapper;

    @PutMapping("/plan")
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(
            @RequestBody UpdateSubscriptionPlan updateSubscriptionPlan
    ) {
        return ResponseEntity.ok(
                subscriptionService.updateSubscriptionPlan(updateSubscriptionPlan)
        );
    }

    @PostMapping("/plan")
    public ResponseEntity<SubscriptionPlan> getMySubscriptionPlan() {
        return ResponseEntity.ok(
                subscriptionService.getMySubscriptionPlanOrCreateNew()
        );
    }

    @GetMapping("/plan")
    public ResponseEntity<SubscriptionPlan> getSubscriptionPlan(
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(
                subscriptionService.getSubscriptionPlanByUserId(userId)
        );
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Subscription> subscribe(
            @RequestBody SubscribeRequest subscribeRequest
    ) {
        return ResponseEntity.ok(
                subscriptionService.subscribe(subscribeRequest)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<Subscription> getActiveSubscription(
            @RequestParam String subscribedOnId
    ) {
        return ResponseEntity.ok(
                subscriptionService.getActiveSubscription(subscribedOnId)
        );
    }

    @GetMapping("/subscribers-info")
    public ResponseEntity<SubscribersInfo> getSubscribersInfo(
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(
                subscriptionService.getSubscribersInfo(userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<Subscription>> getSubscriptions(
            @RequestParam String subscriberId
    ) {
        List<Subscription> subscriptions = subscriptionService.getActiveSubscriptionsBySubscriberId(subscriberId).stream()
                .map(subscriptionMapper::toSubscription)
                .toList();

        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/statistics")
    public ResponseEntity<SubscriptionsStatistics> getMonthSubscriptionsStatistics(@RequestParam String userId) {
        return ResponseEntity.ok(
                subscriptionService.getMonthSubscriptionsStatistics(userId)
        );
    }

    @GetMapping("/analytics")
    public ResponseEntity<SubscriptionAnalytics> getSubscriptionAnalytics(
            @RequestParam String userId,
            @RequestParam Instant from,
            @RequestParam Instant to
    ) {
        return ResponseEntity.ok(
                subscriptionService.getSubscriptionAnalytics(userId, from, to)
        );
    }
}
