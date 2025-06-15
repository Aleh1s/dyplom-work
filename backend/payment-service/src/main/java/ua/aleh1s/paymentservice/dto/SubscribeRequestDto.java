package ua.aleh1s.paymentservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscribeRequestDto {
    private String subscribeOnId;
}
