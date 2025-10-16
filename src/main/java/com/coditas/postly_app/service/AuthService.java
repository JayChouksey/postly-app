package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;

public interface AuthService {
    UserCreateResponseDto registerUser(UserCreateRequestDto userCreateRequestDto);
    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);
    RefreshTokenDto getRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
    String logoutUser(RefreshTokenRequestDto refreshTokenRequestDto);
}
