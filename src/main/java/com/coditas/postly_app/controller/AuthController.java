package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.LoginDto;
import com.coditas.postly_app.dto.LoginRequestDto;
import com.coditas.postly_app.dto.UserRequestDto;
import com.coditas.postly_app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<String>> register(@Valid @RequestBody UserRequestDto request) {

        String data = userService.registerUser(request);
        ApiResponseDto<String> responseBody = new ApiResponseDto<>(true, "User Created Successfully", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginDto>> login(@Valid @RequestBody LoginRequestDto request) {

        LoginDto data = userService.login(request);

        ApiResponseDto<LoginDto> responseBody = new ApiResponseDto<>(true, "User logged in successfully", data);

        return ResponseEntity.ok(responseBody);
    }
}
