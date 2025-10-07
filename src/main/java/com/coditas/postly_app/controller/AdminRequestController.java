package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.service.AdminRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-requests")
@RequiredArgsConstructor
public class AdminRequestController {

    private final AdminRequestService adminRequestService;

    @PostMapping
    public ResponseEntity<String> createAdmin(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(adminRequestService.createRequest(userRequestDto));
    }

    // Super-Admin views all requests
    @GetMapping
    public ResponseEntity<List<AdminRequestDto>> getAllPendingRequests() {
        return ResponseEntity.ok(adminRequestService.getAllPendingRequests());
    }

    // Super-Admin approves or rejects a request
    @PutMapping("/{requestId}/review")
    public ResponseEntity<AdminRequestDto> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody AdminUpdateRequestDto adminUpdateRequestDto
    ) {
        return ResponseEntity.ok(adminRequestService.reviewRequest(requestId, adminUpdateRequestDto));
    }
}

