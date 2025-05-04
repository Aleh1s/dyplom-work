package ua.aleh1s.subscriptionsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.subscriptionsservice.dto.*;
import ua.aleh1s.subscriptionsservice.service.SubscriptionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionPlan> createSubscriptionPlan(
            @RequestBody CreateSubscriptionPlan createSubscriptionPlan
    ) {
        return ResponseEntity.ok(
                subscriptionService.createSubscriptionPlan(createSubscriptionPlan)
        );
    }

    @PutMapping
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(
            @RequestBody UpdateSubscriptionPlan updateSubscriptionPlan
    ) {
        return ResponseEntity.ok(
                subscriptionService.updateSubscriptionPlan(updateSubscriptionPlan)
        );
    }

    @GetMapping
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
}
