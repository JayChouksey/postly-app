package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.entity.ModeratorRequest;
import com.coditas.postly_app.entity.Role;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.exception.EmailAlreadyExistsException;
import com.coditas.postly_app.repository.ModeratorRequestRepository;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
import com.coditas.postly_app.util.JwtService;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModeratorRequestRepository moderatorRequestRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModeratorRequestRepository moderatorRequestRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.moderatorRequestRepository = moderatorRequestRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @Override
    public String registerUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // TODO: Its a Jugaad, Fix it Later
        Role defaultRole = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(defaultRole);

        userRepository.save(user);
        return "Sign Up Successful";
    }

    @Override
    public LoginDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                throw new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }

            // Fetch the user safely
            User savedUser = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

            // Generate JWT
            String jwtToken = jwtService.generateToken(request.getEmail());

            // Build response DTO
            LoginDto loginDto = new LoginDto();
            loginDto.setId(savedUser.getId());
            loginDto.setEmail(savedUser.getEmail());
            loginDto.setUsername(savedUser.getUsername());
            loginDto.setRole(String.valueOf(savedUser.getRole().getName()));
            loginDto.setToken(jwtToken);

            return loginDto;

        } catch (BadCredentialsException ex) {
            throw new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        } catch (CustomException ex) {
            throw ex; // GlobalExceptionHandler will handle it
        } catch (Exception ex) {
            throw new CustomException("Something went wrong during login", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDtoById getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToUserDtoById(user);
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public String resignAsModerator(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Role authorRole = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Author role not found"));

        user.setRole(authorRole);
        userRepository.save(user);

        return "Resigned Successfully!";
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole().getName()));
        return dto;
    }

    private UserDtoById mapToUserDtoById(User user) {
        UserDtoById dto = new UserDtoById();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole().getName()));


        boolean hasRequested = moderatorRequestRepository.existsByUserIdAndStatus(
                user.getId(), ModeratorRequest.Status.PENDING
        );

        dto.setHasRequestedModerator(hasRequested);
        return dto;
    }
}

