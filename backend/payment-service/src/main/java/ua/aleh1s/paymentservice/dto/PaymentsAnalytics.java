package ua.aleh1s.paymentservice.dto;

import lombok.Builder;
import lombok.Data;
import ua.aleh1s.paymentservice.model.KeyValue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PaymentsAnalytics {
    List<KeyValue<Instant, BigDecimal>> amountsSumByDate;
}
