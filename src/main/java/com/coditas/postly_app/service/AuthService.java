package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.entity.RefreshToken;

public interface AuthService {
    String registerUser(UserRequestDto userRequestDto);
    LoginDto loginUser(LoginRequestDto loginRequestDto);
    RefreshTokenDto getRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
    String logoutUser(RefreshTokenRequestDto refreshTokenRequestDto);
}
