package ua.aleh1s.subscriptionsservice.exception;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String identifier) {
        super("Subscription %s not found".formatted(identifier));
    }
}
