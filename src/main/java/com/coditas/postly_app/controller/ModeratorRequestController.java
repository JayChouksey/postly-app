package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderator-requests")
@RequiredArgsConstructor
public class ModeratorRequestController {

    private final UserService userService;

    @PostMapping("/request/{userId}")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<ApiResponseDto<UserCreateRequestResponseDto>> createRequest(@PathVariable Long userId) {

        UserCreateRequestResponseDto data = userService.createModeratorRequest(userId);

        ApiResponseDto<UserCreateRequestResponseDto> responseBody = new ApiResponseDto<>(true, "Request sent to Admin", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<UserRequestResponseDto>>> getAllPendingRequests() {

        List<UserRequestResponseDto> data = userService.getAllModeratorPendingRequests();

        ApiResponseDto<List<UserRequestResponseDto>> responseBody = new ApiResponseDto<>(true, "Request fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{requestId}/review")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<UserRequestUpdateResponseDto>> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody @Valid ActionRequestDto actionRequestDto
            ) {

        UserRequestUpdateResponseDto data = userService.reviewModeratorRequest(requestId, actionRequestDto);

        ApiResponseDto<UserRequestUpdateResponseDto> responseBody = new ApiResponseDto<>(true, "Request status updated", data);

        return ResponseEntity.ok(responseBody);
    }
}


