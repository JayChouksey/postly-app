package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ModeratorRequestDto;

import java.util.List;

public interface ModeratorRequestService {
    ModeratorRequestDto createRequest(Long userId);
    List<ModeratorRequestDto> getAllRequests();
    ModeratorRequestDto reviewRequest(Long requestId, Long adminId, boolean approved);
}

