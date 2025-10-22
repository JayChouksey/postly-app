package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.entity.*;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.AdminRequestRepository;
import com.coditas.postly_app.repository.ModeratorRequestRepository;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModeratorRequestRepository moderatorRequestRepository;
    private final AdminRequestRepository adminRequestRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModeratorRequestRepository moderatorRequestRepository, AdminRequestRepository adminRequestRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.moderatorRequestRepository = moderatorRequestRepository;
        this.adminRequestRepository = adminRequestRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserCreateResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserByIdResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return mapToUserDtoById(user);
    }


    @Override
    @Transactional
    public String deleteUser(Long id) {

        userRepository.deleteById(id);

        return "User deleted successfully!";
    }

    @Override
    @Transactional
    public UserCreateRequestResponseDto createModeratorRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        // Prevent duplicate pending requests
        boolean hasPending = moderatorRequestRepository.findByUser(user).stream()
                .anyMatch(req -> req.getStatus() == ModeratorRequest.Status.PENDING);
        if (hasPending) {
            throw new CustomException("You already have a pending moderator request.", HttpStatus.CONFLICT);
        }

        ModeratorRequest request = ModeratorRequest.builder()
                .user(user)
                .status(ModeratorRequest.Status.PENDING)
                .build();

        ModeratorRequest savedModeratorRequest = moderatorRequestRepository.save(request);

        return mapToDto(savedModeratorRequest, user);
    }

    @Override
    public List<UserRequestResponseDto> getAllModeratorPendingRequests() {
        return moderatorRequestRepository.findAllByStatus(ModeratorRequest.Status.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserRequestUpdateResponseDto reviewModeratorRequest(Long requestId, ActionRequestDto actionRequestDto) {
        ModeratorRequest request = moderatorRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Request not found", HttpStatus.NOT_FOUND));

        String currentAdminName = SecurityContextHolder.getContext().getAuthentication().getName();

        User admin = userRepository.findByEmail(currentAdminName)
                .orElseThrow(() -> new CustomException("Admin not found", HttpStatus.NOT_FOUND));

        if (request.getStatus() != ModeratorRequest.Status.PENDING)
            throw new CustomException("Request already reviewed.", HttpStatus.CONFLICT);

        String action = actionRequestDto.getAction();

        if (action.equalsIgnoreCase("APPROVED")) {
            request.setStatus(ModeratorRequest.Status.APPROVED);
            request.setReviewedBy(admin);
            request.setReviewedAt(LocalDateTime.now());

            // Update user role to MODERATOR
            Role moderatorRole = roleRepository.findByName(Role.RoleName.MODERATOR)
                    .orElseThrow(() -> new CustomException("MODERATOR role not found", HttpStatus.NOT_FOUND));
            User user = request.getUser();
            user.setRole(moderatorRole);
            userRepository.save(user);
        } else if (action.equalsIgnoreCase("REJECTED")) {
            request.setStatus(ModeratorRequest.Status.REJECTED);
            request.setReviewedBy(admin);
            request.setReviewedAt(LocalDateTime.now());
        } else {
            throw new CustomException("Invalid action. Use APPROVE or REJECT.", HttpStatus.NOT_FOUND);
        }

        ModeratorRequest updated = moderatorRequestRepository.save(request);
        return mapToDtoOnUpdate(updated);
    }

    @Override
    @Transactional
    public String resignAsModerator(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        Role authorRole = roleRepository.findByName(Role.RoleName.AUTHOR)
                .orElseThrow(() -> new CustomException("Author role not found", HttpStatus.NOT_FOUND));

        user.setRole(authorRole);
        userRepository.save(user);

        return "Resigned Successfully!";
    }

    @Override
    @Transactional
    public ApiResponseDto<?> createAdminRequest(UserCreateRequestDto userCreateRequestDto) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User requestedByUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException("Requester user not found", HttpStatus.NOT_FOUND));

        // Check if request is by Super-Admin
        String role = String.valueOf(requestedByUser.getRole().getName());
        Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                .orElseThrow(() -> new CustomException("Admin Role not found", HttpStatus.NOT_FOUND));

        if(role.equals("SUPER_ADMIN")){
            User newAdmin = User.builder()
                    .username(userCreateRequestDto.getUsername())
                    .email(userCreateRequestDto.getEmail().toLowerCase())
                    .password(passwordEncoder.encode(userCreateRequestDto.getPassword()))
                    .role(adminRole)
                    .build();

            // then save the admin directly
            User savedAdmin = userRepository.save(newAdmin);

            UserCreateResponseDto userCreateResponseDto = mapToDto(savedAdmin);

            return new ApiResponseDto<>(true, "Admin Created Successfully", userCreateResponseDto);
        }

        AdminRequest adminRequest = AdminRequest.builder()
                .username(userCreateRequestDto.getUsername())
                .email(userCreateRequestDto.getEmail())
                .tempPassword(passwordEncoder.encode(userCreateRequestDto.getPassword()))
                .requestedBy(requestedByUser)
                .build();

        adminRequestRepository.save(adminRequest);

        UserCreateRequestResponseDto userCreateRequestResponseDto = mapToDto(adminRequest, requestedByUser);

        return new ApiResponseDto<>(true, "Admin request sent successfully", userCreateRequestResponseDto);
    }

    @Override
    public List<UserRequestResponseDto> getAllAdminPendingRequests() {
        return adminRequestRepository.findAllByStatus(AdminRequest.Status.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserRequestUpdateResponseDto reviewAdminRequest(Long requestId, ActionRequestDto actionRequestDto) {

        // Fetch the request
        AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Admin request not found", HttpStatus.NOT_FOUND));

        // Ensure the request is still pending
        if (adminRequest.getStatus() != AdminRequest.Status.PENDING) {
            throw new CustomException("Request is already reviewed", HttpStatus.CONFLICT);
        }

        // Get the reviewer (Super Admin) from the SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Reviewer not found", HttpStatus.NOT_FOUND));

        // Handle approval or rejection
        String action = actionRequestDto.getAction().toUpperCase();
        Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                .orElseThrow(() -> new CustomException("ADMIN role not found", HttpStatus.NOT_FOUND));

        if ("APPROVED".equalsIgnoreCase(action)) {
            // Create new Admin user
            User newAdmin = User.builder()
                    .username(adminRequest.getUsername())
                    .email(adminRequest.getEmail())
                    .password(adminRequest.getTempPassword()) // already encoded
                    .role(adminRole)
                    .build();

            userRepository.save(newAdmin);
            adminRequest.setStatus(AdminRequest.Status.APPROVED);
        } else if ("REJECTED".equalsIgnoreCase(action))  {
            adminRequest.setStatus(AdminRequest.Status.REJECTED);
        } else {
            throw new CustomException("Invalid action. Must be 'APPROVE' or 'REJECT'.", HttpStatus.NOT_FOUND);
        }

        // Update metadata
        adminRequest.setReviewedBy(reviewer);
        adminRequest.setReviewedAt(LocalDateTime.now());

        // Save the updated request
        AdminRequest savedRequest = adminRequestRepository.save(adminRequest);

        // Map to DTO and return
        return mapToDtoOnUpdate(savedRequest);
    }

    private UserCreateResponseDto mapToDto(User user) {
        return UserCreateResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole().getName()))
                .build();
    }

    private UserByIdResponseDto mapToUserDtoById(User user) {

        boolean hasRequested = moderatorRequestRepository.existsByUserIdAndStatus(
                user.getId(), ModeratorRequest.Status.PENDING
        );

        return UserByIdResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole().getName()))
                .hasRequestedModerator(hasRequested)
                .build();
    }

    private UserRequestResponseDto mapToDto(ModeratorRequest request) {
        return UserRequestResponseDto.builder()
                .id(request.getId())
                .status(request.getStatus().name())
                .username(request.getUser().getUsername())
                .requestedAt(request.getRequestedAt())
                .build();
    }

    private UserRequestUpdateResponseDto mapToDtoOnUpdate(ModeratorRequest request){
            return UserRequestUpdateResponseDto.builder()
                    .id(request.getId())
                    .status(request.getStatus().name())
                    .username(request.getUser().getUsername())
                    .requestedAt(request.getRequestedAt())
                    .reviewedBy(request.getReviewedBy().getUsername())
                    .build();
    }

    private UserCreateRequestResponseDto mapToDto(ModeratorRequest moderatorRequest, User user){
        return UserCreateRequestResponseDto.builder()
                .requestId(moderatorRequest.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .requestedRole("Moderator")
                .status(String.valueOf(moderatorRequest.getStatus()))
                .build();
    }

    private UserRequestResponseDto mapToDto(AdminRequest request) {
        return UserRequestResponseDto.builder()
                .id(request.getId())
                .status(request.getStatus().name())
                .username(request.getUsername())
                .requestedAt(request.getRequestedAt())
                .build();
    }

    private UserRequestUpdateResponseDto mapToDtoOnUpdate(AdminRequest request){
        return UserRequestUpdateResponseDto.builder()
                .id(request.getId())
                .status(request.getStatus().name())
                .username(request.getUsername())
                .requestedAt(request.getRequestedAt())
                .reviewedBy(request.getReviewedBy().getUsername())
                .build();
    }

    private UserCreateRequestResponseDto mapToDto(AdminRequest adminRequest, User user){
        return UserCreateRequestResponseDto.builder()
                .requestId(adminRequest.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .requestedRole("Admin")
                .status(String.valueOf(adminRequest.getStatus()))
                .build();
    }
}

