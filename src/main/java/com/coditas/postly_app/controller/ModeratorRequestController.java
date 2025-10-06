package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ModeratorRequestDto;
import com.coditas.postly_app.dto.ModeratorUpdateRequestDto;
import com.coditas.postly_app.service.ModeratorRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderator-requests")
@RequiredArgsConstructor
public class ModeratorRequestController {

    private final ModeratorRequestService moderatorRequestService;

    // User creates a request
    @PostMapping("/request/{userId}")
    public ResponseEntity<String> createRequest(@PathVariable Long userId) {
        return ResponseEntity.ok(moderatorRequestService.createRequest(userId));
    }

    // Admin views all requests
    @GetMapping
    public ResponseEntity<List<ModeratorRequestDto>> getAllPendingRequests() {
        return ResponseEntity.ok(moderatorRequestService.getAllPendingRequests());
    }

    // Admin approves or rejects a request
    @PutMapping("/{requestId}/review")
    public ResponseEntity<ModeratorRequestDto> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody ModeratorUpdateRequestDto moderatorUpdateRequestDto
            ) {
        return ResponseEntity.ok(moderatorRequestService.reviewRequest(requestId, moderatorUpdateRequestDto));
    }
}


