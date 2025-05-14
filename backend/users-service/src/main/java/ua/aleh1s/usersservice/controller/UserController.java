package ua.aleh1s.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.usersservice.dto.UpdateUser;
import ua.aleh1s.usersservice.dto.User;
import ua.aleh1s.usersservice.dto.UserProfile;
import ua.aleh1s.usersservice.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/me")
    public ResponseEntity<User> getMe() {
        return ResponseEntity.ok(
                userService.getUserInfoOrCreate()
        );
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMe(@RequestBody UpdateUser updateUser) {
        return ResponseEntity.ok(
                userService.updateUserInfo(updateUser)
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfile> getUserProfileByUsername(@PathVariable String username) {
        return ResponseEntity.ok(
                userService.getUserProfileByUsername(username)
        );
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) String usernameLike,
            @RequestParam(required = false) Set<String> ids
    ) {
        List<User> users = new ArrayList<>();

        if (Objects.nonNull(usernameLike) && !usernameLike.isBlank()) {
            users = userService.getUsersByUsernameLike(usernameLike);
        } else if (Objects.nonNull(ids) && !ids.isEmpty()) {
            users = userService.getUsersByIds(ids);
        }

        return ResponseEntity.ok(users);
    }

}
