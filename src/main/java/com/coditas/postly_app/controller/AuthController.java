package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.LoginRequestDto;
import com.coditas.postly_app.dto.UserDto;
import com.coditas.postly_app.dto.UserRequestDto;
import com.coditas.postly_app.service.UserService;
import com.coditas.postly_app.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserRequestDto request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

}
