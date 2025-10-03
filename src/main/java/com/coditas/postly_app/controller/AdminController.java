package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.AdminRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

//    private final AdminService adminService;
//
//    @PostMapping("/request")
//    public ResponseEntity<AdminRequestDto> requestAdmin() {
//        return ResponseEntity.ok(adminService.requestAdminRole());
//    }
//
//    @GetMapping("/requests")
//    public ResponseEntity<List<AdminRequestDto>> getAllRequests() {
//        return ResponseEntity.ok(adminService.getAllRequests());
//    }
//
//    @PostMapping("/approve/{id}")
//    public ResponseEntity<AdminRequestDto> approveRequest(@PathVariable Long id) {
//        return ResponseEntity.ok(adminService.approveRequest(id));
//    }
//
//    @PostMapping("/reject/{id}")
//    public ResponseEntity<AdminRequestDto> rejectRequest(@PathVariable Long id) {
//        return ResponseEntity.ok(adminService.rejectRequest(id));
//    }
}

