package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.LoginDto;
import com.coditas.postly_app.dto.LoginRequestDto;
import com.coditas.postly_app.dto.UserDto;
import com.coditas.postly_app.dto.UserRequestDto;
import com.coditas.postly_app.entity.Role;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
import com.coditas.postly_app.util.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserDto registerUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // TODO: Its a Jugaad, Fix it Later
        Role defaultRole = roleRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User();
        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setRole(defaultRole);

        return mapToDto(userRepository.save(user));
    }

    @Override
    public LoginDto login(LoginRequestDto request) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword())
                );

        String jwtToken = "";
        if(authentication.isAuthenticated()){
            jwtToken = jwtService.generateToken(request.getEmail());
        }

        User savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();

        LoginDto loginDto = new LoginDto();
        loginDto.setId(savedUser.getId());
        loginDto.setEmail(savedUser.getEmail());
        loginDto.setUsername(savedUser.getUsername());
        loginDto.setIsModerator(savedUser.getIsModerator());
        loginDto.setRole(String.valueOf(savedUser.getRole().getName()));
        loginDto.setToken(jwtToken);

        return loginDto;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id).map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDto applyForModerator(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        // user.setModeratorRequested(true);
        return mapToDto(userRepository.save(user));
    }

    @Override
    public UserDto resignAsModerator(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsModerator(false);
        return mapToDto(userRepository.save(user));
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole().getName()));
        dto.setModerator(user.getIsModerator());
        return dto;
    }
}

