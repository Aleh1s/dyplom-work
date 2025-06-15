package ua.aleh1s.paymentservice.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String description;
}
