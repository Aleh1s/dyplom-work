package ua.aleh1s.subscriptionsservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscribeRequest {
    private String subscribeOnId;
}
