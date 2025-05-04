package ua.aleh1s.usersservice.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.aleh1s.usersservice.exception.ApiError;
import ua.aleh1s.usersservice.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleException(UserNotFoundException e) {
        ApiError apiError = ApiError.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.badRequest()
                .body(apiError);
    }
}
