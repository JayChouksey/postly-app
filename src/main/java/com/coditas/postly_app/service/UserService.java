package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.LoginDto;
import com.coditas.postly_app.dto.LoginRequestDto;
import com.coditas.postly_app.dto.UserDto;
import com.coditas.postly_app.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    String registerUser(UserRequestDto userRequestDto);
    LoginDto login(LoginRequestDto loginRequestDto);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    void deleteUser(Long id);
    String resignAsModerator(Long userId);
}
