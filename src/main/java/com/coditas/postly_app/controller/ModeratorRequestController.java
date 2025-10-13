package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ApiResponseDto;
import com.coditas.postly_app.dto.ModeratorRequestDto;
import com.coditas.postly_app.dto.ModeratorUpdateRequestDto;
import com.coditas.postly_app.service.UserService;
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
    public ResponseEntity<ApiResponseDto<String>> createRequest(@PathVariable Long userId) {

        String data = userService.createModeratorRequest(userId);

        ApiResponseDto<String> responseBody = new ApiResponseDto<>(true, "Request sent to Admin", data);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<ModeratorRequestDto>>> getAllPendingRequests() {

        List<ModeratorRequestDto> data = userService.getAllModeratorPendingRequests();

        ApiResponseDto<List<ModeratorRequestDto>> responseBody = new ApiResponseDto<>(true, "Request fetched successfully", data);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/{requestId}/review")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponseDto<ModeratorRequestDto>> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody ModeratorUpdateRequestDto moderatorUpdateRequestDto
            ) {

        ModeratorRequestDto data = userService.reviewModeratorRequest(requestId, moderatorUpdateRequestDto);

        ApiResponseDto<ModeratorRequestDto> responseBody = new ApiResponseDto<>(true, "Request status updated", data);

        return ResponseEntity.ok(responseBody);
    }
}


