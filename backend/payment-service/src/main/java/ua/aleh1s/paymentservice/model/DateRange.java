package ua.aleh1s.paymentservice.model;

import java.time.Instant;

public record DateRange(
        Instant from,
        Instant to
) { }
