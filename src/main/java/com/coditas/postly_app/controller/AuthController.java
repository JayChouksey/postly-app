package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserCreateResponseDto>> registerUser(@Valid @RequestBody UserCreateRequestDto request) {

        UserCreateResponseDto data = authService.registerUser(request);
        ApiResponseDto<UserCreateResponseDto> responseBody = new ApiResponseDto<>(true, "User Created Successfully", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto request) {

        LoginResponseDto data = authService.loginUser(request);

        ApiResponseDto<LoginResponseDto> responseBody = new ApiResponseDto<>(true, "User logged in successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDto<RefreshTokenDto>> getRefreshToken(@RequestBody @Valid  RefreshTokenRequestDto payload) {
        RefreshTokenDto data = authService.getRefreshToken(payload);

        ApiResponseDto<RefreshTokenDto> responseBody = new ApiResponseDto<>(true, "New Access Token fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<String>> logoutUser(@RequestBody @Valid RefreshTokenRequestDto payload) {
        String data = authService.logoutUser(payload);

        ApiResponseDto<String> responseBody = new ApiResponseDto<>(true, "User logged out successfully", data);

        return ResponseEntity.ok(responseBody);
    }
}
