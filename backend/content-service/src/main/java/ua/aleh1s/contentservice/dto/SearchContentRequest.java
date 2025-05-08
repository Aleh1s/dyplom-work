package ua.aleh1s.contentservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SearchContentRequest {
    private Set<String> ids;
    private String ownerId;
}
