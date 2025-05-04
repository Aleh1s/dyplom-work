package ua.aleh1s.usersservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String identifier) {
        super("User with identifier %s not found".formatted(identifier));
    }
}
