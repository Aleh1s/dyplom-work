package ua.aleh1s.paymentservice.utils;

import ua.aleh1s.paymentservice.model.DateRange;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class DateUtils {

    public static DateRange getThisMonthDateRange() {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate now = LocalDate.now(zoneId);

        Instant startOfMonth = now.withDayOfMonth(1)
                .atStartOfDay(zoneId)
                .toInstant();
        Instant endOfMonth = now.withDayOfMonth(now.lengthOfMonth())
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant();

        return new DateRange(startOfMonth, endOfMonth);
    }

    public static DateRange getLastMonthDateRange() {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate now = LocalDate.now(zoneId);

        LocalDate prevMonthDate = now.minusMonths(1);

        Instant startOfPrevMonth = prevMonthDate.withDayOfMonth(1)
                .atStartOfDay(zoneId)
                .toInstant();

        Instant endOfPrevMonth = prevMonthDate.withDayOfMonth(prevMonthDate.lengthOfMonth())
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant();

        return new DateRange(startOfPrevMonth, endOfPrevMonth);
    }
}
