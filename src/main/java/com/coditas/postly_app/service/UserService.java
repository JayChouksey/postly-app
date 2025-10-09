package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;

import java.util.List;

public interface UserService {
    String registerUser(UserRequestDto userRequestDto);
    LoginDto login(LoginRequestDto loginRequestDto);
    List<UserDto> getAllUsers();
    UserDtoById getUserById(Long id);
    void deleteUser(Long id);
    String resignAsModerator(Long userId);
}
