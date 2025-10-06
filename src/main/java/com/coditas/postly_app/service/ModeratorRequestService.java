package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ModeratorRequestDto;
import com.coditas.postly_app.dto.ModeratorUpdateRequestDto;

import java.util.List;

public interface ModeratorRequestService {
    String createRequest(Long userId);
    List<ModeratorRequestDto> getAllPendingRequests();
    ModeratorRequestDto reviewRequest(Long requestId, ModeratorUpdateRequestDto moderatorUpdateRequestDto); // APPROVE / REJECT
}

