package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.entity.RefreshToken;
import com.coditas.postly_app.entity.Role;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.RefreshTokenRepository;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
import com.coditas.postly_app.util.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authManager, JwtService jwtService, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public UserCreateResponseDto registerUser(UserCreateRequestDto userCreateRequestDto) {
        if (userRepository.existsByEmail(userCreateRequestDto.getEmail())) {
            throw new CustomException("Email already exists", HttpStatus.CONFLICT);
        }

        // TODO: Its a Jugaad, Fix it Later
        Role defaultRole = roleRepository.findById(1L)
                .orElseThrow(() -> new CustomException("Default role not found", HttpStatus.NOT_FOUND));

        User user = new User();
        user.setUsername(userCreateRequestDto.getUsername());
        user.setEmail(userCreateRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateRequestDto.getPassword()));
        user.setRole(defaultRole);

        User savedUser = userRepository.save(user);

        return UserCreateResponseDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(String.valueOf(savedUser.getRole().getName()))
                .build();
    }

    @Override
    public LoginResponseDto loginUser(LoginRequestDto request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Fetch the user safely
            User savedUser = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

            if (!authentication.isAuthenticated()) {
                throw new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }


            // Generate JWT
            String jwtToken = jwtService.generateToken(request.getEmail());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

            // Build response DTO
            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setId(savedUser.getId());
            loginResponseDto.setEmail(savedUser.getEmail());
            loginResponseDto.setUsername(savedUser.getUsername());
            loginResponseDto.setRole(String.valueOf(savedUser.getRole().getName()));
            loginResponseDto.setRefreshToken(refreshToken.getToken());
            loginResponseDto.setAccessToken(jwtToken);

            return loginResponseDto;

        } catch (BadCredentialsException ex) {
            throw new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        } catch (CustomException ex) {
            throw ex; // GlobalExceptionHandler will handle it
        } catch (Exception ex) {
            throw new CustomException("Something went wrong during login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RefreshTokenDto getRefreshToken(RefreshTokenRequestDto refreshTokenRequestDto){
        String requestToken = refreshTokenRequestDto.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new CustomException("Invalid Refresh Token", HttpStatus.BAD_REQUEST));

        if(refreshTokenService.isTokenExpired(refreshToken)){
            throw new CustomException("Refresh token expired. Please login again.", HttpStatus.BAD_REQUEST);
        }

        String newAccessToken = jwtService.generateToken(refreshToken.getUser().getEmail());

        RefreshTokenDto refreshTokenDto = new RefreshTokenDto();
        refreshTokenDto.setAccessToken(newAccessToken);

        return refreshTokenDto;
    }

    // TODO: Need to check for it as well
    @Override
    public String logoutUser(RefreshTokenRequestDto refreshTokenRequestDto) {
        String requestToken = refreshTokenRequestDto.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow( () -> new CustomException("Invalid refresh token.", HttpStatus.BAD_REQUEST));

        refreshTokenRepository.delete(refreshToken);

        return "Logged out successfully.";
    }


}
