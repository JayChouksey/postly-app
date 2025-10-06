package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.AdminRequestDto;
import com.coditas.postly_app.dto.AdminUpdateRequestDto;
import com.coditas.postly_app.dto.UserRequestDto;

import java.util.List;

public interface AdminRequestService {
    String createRequest(UserRequestDto userRequestDto);
    List<AdminRequestDto> getAllPendingRequests();
    AdminRequestDto reviewRequest(Long requestId, AdminUpdateRequestDto adminUpdateRequestDto);
}


