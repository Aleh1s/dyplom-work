package ua.aleh1s.usersservice.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    private String description;
}
