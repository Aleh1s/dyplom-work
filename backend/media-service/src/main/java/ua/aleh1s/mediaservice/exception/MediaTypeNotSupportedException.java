package ua.aleh1s.mediaservice.exception;

public class MediaTypeNotSupportedException extends RuntimeException {
    public MediaTypeNotSupportedException(String message) {
        super(message);
    }
}
