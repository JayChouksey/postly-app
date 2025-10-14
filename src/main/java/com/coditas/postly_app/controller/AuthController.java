package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.repository.RefreshTokenRepository;
import com.coditas.postly_app.service.AuthService;
import com.coditas.postly_app.service.RefreshTokenService;
import com.coditas.postly_app.service.UserService;
import com.coditas.postly_app.util.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<String>> registerUser(@Valid @RequestBody UserRequestDto request) {

        String data = authService.registerUser(request);
        ApiResponseDto<String> responseBody = new ApiResponseDto<>(true, "User Created Successfully", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginDto>> loginUser(@Valid @RequestBody LoginRequestDto request) {

        LoginDto data = authService.loginUser(request);

        ApiResponseDto<LoginDto> responseBody = new ApiResponseDto<>(true, "User logged in successfully", data);

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
