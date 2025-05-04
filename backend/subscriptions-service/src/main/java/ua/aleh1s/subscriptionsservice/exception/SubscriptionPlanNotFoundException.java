package ua.aleh1s.subscriptionsservice.exception;

public class SubscriptionPlanNotFoundException extends RuntimeException {
    public SubscriptionPlanNotFoundException(String identifier) {
        super("Subscription plan %s not found".formatted(identifier));
    }
}
