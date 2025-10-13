package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.service.UserService;
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
    public ResponseEntity<ApiResponseDto<String>> createAdmin(@RequestBody UserRequestDto userRequestDto) {

        String data = userService.createAdminRequest(userRequestDto);
        ApiResponseDto<String> responseBody = new ApiResponseDto<>(true, "Admin request sent successfully", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<AdminRequestDto>>> getAllPendingRequests() {

        List<AdminRequestDto> data = userService.getAllAdminPendingRequests();

        ApiResponseDto<List<AdminRequestDto>> responseBody = new ApiResponseDto<>(true, "Requests fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{requestId}/review")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<AdminRequestDto>> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody AdminUpdateRequestDto adminUpdateRequestDto
    ) {

        AdminRequestDto data = userService.reviewAdminRequest(requestId, adminUpdateRequestDto);
        ApiResponseDto<AdminRequestDto> responseBody = new ApiResponseDto<>(true, "Request reviewed", data);

        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}

