package ua.aleh1s.mediaservice.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdDateGeneratorComponent {

    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
