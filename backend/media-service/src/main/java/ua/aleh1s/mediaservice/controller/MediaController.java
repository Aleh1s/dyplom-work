package ua.aleh1s.mediaservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.aleh1s.mediaservice.domain.Resource;
import ua.aleh1s.mediaservice.dto.MediaUrl;
import ua.aleh1s.mediaservice.dto.SaveSafeMediaResponse;
import ua.aleh1s.mediaservice.service.MediaService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/media")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<MediaUrl> uploadMedia(@RequestParam("file") MultipartFile media) {
        return ResponseEntity.ok(
                mediaService.upload(media)
        );
    }

    @PostMapping("/safe/upload")
    public ResponseEntity<SaveSafeMediaResponse> uploadSafeMedia(@RequestParam("file") MultipartFile media) {
        return ResponseEntity.ok(
                mediaService.processAndSaveMedia(media)
        );
    }

    @GetMapping("/{bucketName}/{fileName}")
    public ResponseEntity<InputStreamResource> download(
            @PathVariable String bucketName,
            @PathVariable String fileName
    ) {
        Resource resource = mediaService.getResource(bucketName, fileName);

        MediaType mediaType = MediaType.parseMediaType(resource.getContentType());
        InputStreamResource body = new InputStreamResource(resource.getData());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(body);
    }
}
