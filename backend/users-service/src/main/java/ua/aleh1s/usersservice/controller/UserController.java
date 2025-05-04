package ua.aleh1s.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.aleh1s.usersservice.dto.UpdateUser;
import ua.aleh1s.usersservice.dto.User;
import ua.aleh1s.usersservice.dto.UserProfile;
import ua.aleh1s.usersservice.service.UserService;

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

    @PutMapping
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
}
