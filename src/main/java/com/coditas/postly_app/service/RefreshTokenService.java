package com.coditas.postly_app.service;

import com.coditas.postly_app.entity.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);
    boolean isTokenExpired(RefreshToken token);
}