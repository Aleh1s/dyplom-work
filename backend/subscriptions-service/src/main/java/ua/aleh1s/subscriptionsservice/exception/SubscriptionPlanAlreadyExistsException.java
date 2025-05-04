package ua.aleh1s.subscriptionsservice.exception;

public class SubscriptionPlanAlreadyExistsException extends RuntimeException {
    public SubscriptionPlanAlreadyExistsException(String identifier) {
        super("Subscription plan %s already exists".formatted(identifier));
    }
}
