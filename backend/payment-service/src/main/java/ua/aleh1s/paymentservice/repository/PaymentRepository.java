package ua.aleh1s.paymentservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ua.aleh1s.paymentservice.model.Payment;

import java.time.Instant;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    boolean existsBySessionId(String sessionId);

    List<Payment> findAllBySubscribedOnIdAndCreatedAtBetween(String subscribedOnId, Instant createdAtAfter, Instant createdAtBefore);
}
