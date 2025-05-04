package ua.aleh1s.usersservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.aleh1s.usersservice.dto.User;
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
}
