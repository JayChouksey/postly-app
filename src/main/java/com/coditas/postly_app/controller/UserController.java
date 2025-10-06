package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.UserDto;
import com.coditas.postly_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Resign as moderator
    @PutMapping("/{userId}/resign")
    public ResponseEntity<String> resignAsModerator(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.resignAsModerator(userId));
    }
}
