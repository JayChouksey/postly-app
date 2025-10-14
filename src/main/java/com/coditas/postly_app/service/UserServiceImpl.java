package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.*;
import com.coditas.postly_app.entity.*;
import com.coditas.postly_app.exception.CustomException;
import com.coditas.postly_app.repository.AdminRequestRepository;
import com.coditas.postly_app.repository.ModeratorRequestRepository;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
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
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public UserDtoById getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return mapToUserDtoById(user);
    }


    @Override
    public String deleteUser(Long id) {

        userRepository.deleteById(id);

        return "User deleted successfully!";
    }

    @Override
    public String createModeratorRequest(Long userId) {
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

        moderatorRequestRepository.save(request);
        return "Request sent successfully!";
    }

    @Override
    public List<ModeratorRequestDto> getAllModeratorPendingRequests() {
        return moderatorRequestRepository.findAllByStatus(ModeratorRequest.Status.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ModeratorRequestDto reviewModeratorRequest(Long requestId, ModeratorUpdateRequestDto moderatorUpdateRequestDto) {
        ModeratorRequest request = moderatorRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("Request not found", HttpStatus.NOT_FOUND));

        String currentAdminName = SecurityContextHolder.getContext().getAuthentication().getName();

        User admin = userRepository.findByEmail(currentAdminName)
                .orElseThrow(() -> new CustomException("Admin not found", HttpStatus.NOT_FOUND));

        if (request.getStatus() != ModeratorRequest.Status.PENDING)
            throw new CustomException("Request already reviewed.", HttpStatus.CONFLICT);

        String action = moderatorUpdateRequestDto.getAction();

        if (action.equalsIgnoreCase("APPROVED")) {
            request.setStatus(ModeratorRequest.Status.APPROVED);
            request.setReviewedBy(admin);
            request.setReviewedAt(LocalDateTime.now());

            // Update user role to MODERATOR
            Role moderatorRole = roleRepository.findById(2L)
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
        return mapToDto(updated);
    }

    @Override
    public String resignAsModerator(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        Role authorRole = roleRepository.findById(1L)
                .orElseThrow(() -> new CustomException("Author role not found", HttpStatus.NOT_FOUND));

        user.setRole(authorRole);
        userRepository.save(user);

        return "Resigned Successfully!";
    }

    @Override
    public String createAdminRequest(UserRequestDto userRequestDto) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User requestedByUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new CustomException("Requester user not found", HttpStatus.NOT_FOUND));

        // Check if request is by Super-Admin
        String role = String.valueOf(requestedByUser.getRole().getName());
        Role adminRole = roleRepository.findById(3L)
                .orElseThrow(() -> new CustomException("Admin Role not found", HttpStatus.NOT_FOUND));

        if(role.equals("SUPER_ADMIN")){
            User newAdmin = User.builder()
                    .username(userRequestDto.getUsername())
                    .email(userRequestDto.getEmail())
                    .password(passwordEncoder.encode(userRequestDto.getPassword()))
                    .role(adminRole)
                    .build();

            // then save the admin directly
            userRepository.save(newAdmin);

            return "Admin Created Successfully";
        }

        AdminRequest adminRequest = AdminRequest.builder()
                .username(userRequestDto.getUsername())
                .email(userRequestDto.getEmail())
                .tempPassword(passwordEncoder.encode(userRequestDto.getPassword()))
                .requestedBy(requestedByUser)
                .build();

        adminRequestRepository.save(adminRequest);

        return "Admin request sent successfully";
    }

    @Override
    public List<AdminRequestDto> getAllAdminPendingRequests() {
        return adminRequestRepository.findAllByStatus(AdminRequest.Status.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminRequestDto reviewAdminRequest(Long requestId, AdminUpdateRequestDto adminUpdateRequestDto) {

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
        String action = adminUpdateRequestDto.getAction().toUpperCase();
        Role adminRole = roleRepository.findById(3L)
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
        return mapToDto(savedRequest);
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

    private ModeratorRequestDto mapToDto(ModeratorRequest request) {
        ModeratorRequestDto dto = new ModeratorRequestDto();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus().name());
        dto.setUsername(request.getUser().getUsername());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setReviewedBy(request.getReviewedBy() != null ? request.getReviewedBy().getUsername() : null);
        return dto;
    }

    private AdminRequestDto mapToDto(AdminRequest request) {
        AdminRequestDto dto = new AdminRequestDto();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus().name());
        dto.setUsername(request.getUsername());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setReviewedBy(request.getReviewedBy() != null ? request.getReviewedBy().getUsername() : null);
        return dto;
    }
}

