package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;

import java.util.List;

public interface UserService {
    List<UserCreateResponseDto> getAllUsers();
    UserByIdResponseDto getUserById(Long id);
    String deleteUser(Long id);

    UserCreateRequestResponseDto createModeratorRequest(Long userId);
    List<UserRequestResponseDto> getAllModeratorPendingRequests();
    UserRequestUpdateResponseDto reviewModeratorRequest(Long requestId, ActionRequestDto actionRequestDto);
    String resignAsModerator(Long userId);

    ApiResponseDto<?> createAdminRequest(UserCreateRequestDto userCreateRequestDto);
    List<UserRequestResponseDto> getAllAdminPendingRequests();
    UserRequestUpdateResponseDto reviewAdminRequest(Long requestId, ActionRequestDto actionRequestDto);

}
