package ua.aleh1s.postsservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.postsservice.dto.*;
import ua.aleh1s.postsservice.service.CommentService;
import ua.aleh1s.postsservice.service.LikeService;
import ua.aleh1s.postsservice.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

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

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> like(
            @PathVariable String postId
    ) {
        likeService.like(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<Void> unlike(
            @PathVariable String postId
    ) {
        likeService.unlike(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PopulatedPostDto>> getPostsByRequest(GetPostsRequest request) {
        List<PopulatedPostDto> posts = postService.getPostsByRequest(request);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PopulatedPostDto>> getPostsFeed(GetPostsFeedRequest request) {
        Page<PopulatedPostDto> posts = postService.getPostsFeed(request);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/statistics")
    public ResponseEntity<PostsStatistics> getPostsStatistics(
            @RequestParam String ownerId
    ) {
        return ResponseEntity.ok(
                postService.getPostsStatisticsByOwnerId(ownerId)
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<List<PostWithCounts>> getLatestPosts(
            @RequestParam String ownerId,
            @RequestParam(required = false, defaultValue = "3") Integer limit
    ) {
        return ResponseEntity.ok(
                postService.getLatestPosts(ownerId, limit)
        );
    }
}
