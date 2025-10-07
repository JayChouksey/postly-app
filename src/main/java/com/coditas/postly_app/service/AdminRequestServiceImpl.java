package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.AdminRequestDto;
import com.coditas.postly_app.dto.AdminUpdateRequestDto;
import com.coditas.postly_app.dto.ModeratorRequestDto;
import com.coditas.postly_app.dto.UserRequestDto;
import com.coditas.postly_app.entity.AdminRequest;
import com.coditas.postly_app.entity.ModeratorRequest;
import com.coditas.postly_app.entity.Role;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.AdminRequestRepository;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRequestServiceImpl implements AdminRequestService {

    private final AdminRequestRepository adminRequestRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public String createRequest(UserRequestDto userRequestDto) {

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User requestedByUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new RuntimeException("Requester user not found"));

        // Check if request is by Super-Admin
        String role = String.valueOf(requestedByUser.getRole().getName());
        Role adminRole = roleRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Admin Role not found"));

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
    public List<AdminRequestDto> getAllPendingRequests() {
        return adminRequestRepository.findAllByStatus(AdminRequest.Status.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminRequestDto reviewRequest(Long requestId, AdminUpdateRequestDto adminUpdateRequestDto) {

        // Fetch the request
        AdminRequest adminRequest = adminRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Admin request not found"));

        // Ensure the request is still pending
        if (adminRequest.getStatus() != AdminRequest.Status.PENDING) {
            throw new RuntimeException("Request is already " + adminRequest.getStatus());
        }

        // Get the reviewer (Super Admin) from the SecurityContext
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        // Handle approval or rejection
        String action = adminUpdateRequestDto.getAction().toUpperCase();
        Role adminRole = roleRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

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
            throw new RuntimeException("Invalid action. Must be 'APPROVE' or 'REJECT'.");
        }

        // Update metadata
        adminRequest.setReviewedBy(reviewer);
        adminRequest.setReviewedAt(LocalDateTime.now());

        // Save the updated request
        AdminRequest savedRequest = adminRequestRepository.save(adminRequest);

        // Map to DTO and return
        return mapToDto(savedRequest);
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

