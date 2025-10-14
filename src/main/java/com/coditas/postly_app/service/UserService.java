package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDtoById getUserById(Long id);
    String deleteUser(Long id);

    String createModeratorRequest(Long userId);
    List<ModeratorRequestDto> getAllModeratorPendingRequests();
    ModeratorRequestDto reviewModeratorRequest(Long requestId, ModeratorUpdateRequestDto moderatorUpdateRequestDto);
    String resignAsModerator(Long userId);

    String createAdminRequest(UserRequestDto userRequestDto);
    List<AdminRequestDto> getAllAdminPendingRequests();
    AdminRequestDto reviewAdminRequest(Long requestId, AdminUpdateRequestDto adminUpdateRequestDto);

}
