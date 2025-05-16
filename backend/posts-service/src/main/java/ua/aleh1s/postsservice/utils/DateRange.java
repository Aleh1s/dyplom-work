package ua.aleh1s.postsservice.utils;

import java.time.Instant;

public record DateRange(
        Instant from,
        Instant to
) { }
