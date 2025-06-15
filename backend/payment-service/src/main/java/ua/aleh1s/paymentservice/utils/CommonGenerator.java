package ua.aleh1s.paymentservice.utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class CommonGenerator {
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    public Instant now() {
        return Instant.now();
    }
}