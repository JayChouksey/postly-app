package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.UserDto;
import com.coditas.postly_app.dto.UserDtoById;
import com.coditas.postly_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<UserDto>>> getAllUsers() {
        List<UserDto> data = userService.getAllUsers();
        ApiResponseDto<List<UserDto>> responseBody = new ApiResponseDto<>(true, "Users fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    // Get user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<UserDtoById>> getUserById(@PathVariable Long id) {
        UserDtoById data = userService.getUserById(id);
        ApiResponseDto<UserDtoById> responseBody = new ApiResponseDto<>(true, "User fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{userId}/resign")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ApiResponseDto<String>> resignAsModerator(@PathVariable Long userId) {

        String data = userService.resignAsModerator(userId);

        ApiResponseDto<String> responseBody = new ApiResponseDto<>(true, "Moderator Resigned", data);

        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('MODERATOR') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
