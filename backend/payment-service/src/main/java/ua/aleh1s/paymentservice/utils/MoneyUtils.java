package ua.aleh1s.paymentservice.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtils {
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal CENTS_IN_DOLLAR = BigDecimal.valueOf(100);

    private MoneyUtils() {
    }

    public static BigDecimal centsToDollars(long cents) {
        return BigDecimal.valueOf(cents)
                .divide(CENTS_IN_DOLLAR, 2, ROUNDING_MODE);
    }

    public static Long dollarsToCents(BigDecimal dollars) {
        return dollars.multiply(CENTS_IN_DOLLAR)
                .setScale(0, ROUNDING_MODE)
                .longValue();
    }
}
