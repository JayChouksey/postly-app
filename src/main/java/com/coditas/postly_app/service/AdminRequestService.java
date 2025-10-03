package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.AdminRequestDto;

import java.util.List;

public interface AdminRequestService {
    AdminRequestDto createRequest(Long userId);
    List<AdminRequestDto> getAllRequests();
    AdminRequestDto reviewRequest(Long requestId, Long superAdminId, boolean approved);
}


