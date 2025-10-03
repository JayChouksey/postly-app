package com.coditas.postly_app.controller;

import com.coditas.postly_app.dto.ReviewLogDto;
import com.coditas.postly_app.service.ReviewLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review-logs")
@RequiredArgsConstructor
public class ReviewLogController {

//    private final ReviewLogService reviewLogService;

//    @GetMapping
//    public ResponseEntity<List<ReviewLogDto>> getAllLogs() {
//        return ResponseEntity.ok(reviewLogService.getAllLogs());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ReviewLogDto> getLogById(@PathVariable Long id) {
//        return ResponseEntity.ok(reviewLogService.getLogById(id));
//    }
}

