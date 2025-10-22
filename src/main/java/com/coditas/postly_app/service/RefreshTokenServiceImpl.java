package com.coditas.postly_app.service;

import com.coditas.postly_app.entity.RefreshToken;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.RefreshTokenRepository;
import com.coditas.postly_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{
    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository repo, UserRepository userRepo) {
        this.refreshTokenRepository = repo;
        this.userRepository = userRepo;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        var token = new RefreshToken();
        User user = userRepository.findById(userId).
                orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(token);
    }

    @Override
    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
