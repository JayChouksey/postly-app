package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-requests")
@RequiredArgsConstructor
public class AdminRequestController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> createAdmin(@RequestBody @Valid UserCreateRequestDto userCreateRequestDto) {

        ApiResponseDto<?> responseBody = userService.createAdminRequest(userCreateRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UserRequestResponseDto>>> getAllPendingRequests() {

        List<UserRequestResponseDto> data = userService.getAllAdminPendingRequests();

        ApiResponseDto<List<UserRequestResponseDto>> responseBody = new ApiResponseDto<>(true, "Requests fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{requestId}/review")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<UserRequestUpdateResponseDto>> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody @Valid ActionRequestDto actionRequestDto
    ) {

        UserRequestUpdateResponseDto data = userService.reviewAdminRequest(requestId, actionRequestDto);
        ApiResponseDto<UserRequestUpdateResponseDto> responseBody = new ApiResponseDto<>(true, "Request reviewed", data);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}

