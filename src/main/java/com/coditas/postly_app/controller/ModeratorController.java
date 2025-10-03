package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ModeratorRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
public class ModeratorController {

//    private final ModeratorService moderatorService;
//
//    @PostMapping("/request")
//    public ResponseEntity<ModeratorRequestDto> requestModerator() {
//        return ResponseEntity.ok(moderatorService.requestModeratorRole());
//    }
//
//    @GetMapping("/requests")
//    public ResponseEntity<List<ModeratorRequestDto>> getAllRequests() {
//        return ResponseEntity.ok(moderatorService.getAllRequests());
//    }
//
//    @PostMapping("/approve/{id}")
//    public ResponseEntity<ModeratorRequestDto> approveRequest(@PathVariable Long id) {
//        return ResponseEntity.ok(moderatorService.approveRequest(id));
//    }
//
//    @PostMapping("/reject/{id}")
//    public ResponseEntity<ModeratorRequestDto> rejectRequest(@PathVariable Long id) {
//        return ResponseEntity.ok(moderatorService.rejectRequest(id));
//    }
}

