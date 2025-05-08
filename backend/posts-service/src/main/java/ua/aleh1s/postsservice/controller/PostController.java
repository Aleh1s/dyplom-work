package ua.aleh1s.postsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.service.CommentService;
import ua.aleh1s.postsservice.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<PostDto> save(@RequestBody NewPost newPost) {
        PostDto save = postService.save(newPost);
        return ResponseEntity.ok(save);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<CommentDto> comment(
            @PathVariable String postId,
            @RequestBody NewComment newComment
    ) {
        CommentDto comment = commentService.save(postId, newComment);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getPostsByRequest(GetPostsRequest request) {
        List<PostDto> posts = postService.getPostsByRequest(request);
        return ResponseEntity.ok(posts);
    }
}
