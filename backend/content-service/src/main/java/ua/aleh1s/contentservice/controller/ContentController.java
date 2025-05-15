package ua.aleh1s.contentservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.contentservice.dto.ContentDto;
import ua.aleh1s.contentservice.dto.NewContentDto;
import ua.aleh1s.contentservice.dto.SearchContentRequest;
import ua.aleh1s.contentservice.mapper.ContentDtoMapper;
import ua.aleh1s.contentservice.model.Content;
import ua.aleh1s.contentservice.service.ContentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ContentService service;
    private final ContentDtoMapper mapper;

    @PostMapping
    public ResponseEntity<ContentDto> createContent(@RequestBody NewContentDto newContent) {
        Content content = service.save(newContent);
        return ResponseEntity.ok(mapper.map(content));
    }

    @GetMapping
    public ResponseEntity<List<ContentDto>> getAllBySearchRequest(SearchContentRequest request) {
        List<ContentDto> resultList = service.getAllBySearchRequest(request).stream()
                .map(mapper::map)
                .toList();

        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentDto> getContentById(@PathVariable String id) {
        Content content = service.getContentById(id);
        return ResponseEntity.ok(mapper.map(content));
    }

    @GetMapping("/my-gallery")
    public ResponseEntity<List<ContentDto>> getMyGallery() {
        List<ContentDto> content = service.getMyGallery().stream()
                .map(mapper::map)
                .toList();
        return ResponseEntity.ok(content);
    }
}
