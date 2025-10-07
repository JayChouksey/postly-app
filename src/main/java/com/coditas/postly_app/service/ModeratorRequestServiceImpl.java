package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ModeratorRequestDto;
import com.coditas.postly_app.dto.ModeratorUpdateRequestDto;
import com.coditas.postly_app.entity.ModeratorRequest;
import com.coditas.postly_app.entity.Role;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.ModeratorRequestRepository;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
import com.coditas.postly_app.service.ModeratorRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModeratorRequestServiceImpl implements ModeratorRequestService {

    private final ModeratorRequestRepository moderatorRequestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public String createRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent duplicate pending requests
        boolean hasPending = moderatorRequestRepository.findByUser(user).stream()
                .anyMatch(req -> req.getStatus() == ModeratorRequest.Status.PENDING);
        if (hasPending) {
            throw new RuntimeException("You already have a pending moderator request.");
        }

        ModeratorRequest request = ModeratorRequest.builder()
                .user(user)
                .status(ModeratorRequest.Status.PENDING)
                .build();

        moderatorRequestRepository.save(request);
        return "Request sent successfully!";
    }

    @Override
    public List<ModeratorRequestDto> getAllPendingRequests() {
        return moderatorRequestRepository.findAllByStatus(ModeratorRequest.Status.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ModeratorRequestDto reviewRequest(Long requestId, ModeratorUpdateRequestDto moderatorUpdateRequestDto) {
        ModeratorRequest request = moderatorRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        User admin = userRepository.findById(moderatorUpdateRequestDto.getAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (request.getStatus() != ModeratorRequest.Status.PENDING)
            throw new RuntimeException("Request already reviewed.");

        String action = moderatorUpdateRequestDto.getAction();

        if (action.equalsIgnoreCase("APPROVED")) {
            request.setStatus(ModeratorRequest.Status.APPROVED);
            request.setReviewedBy(admin);
            request.setReviewedAt(LocalDateTime.now());

            // Update user role to MODERATOR
            Role moderatorRole = roleRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("MODERATOR role not found"));
            User user = request.getUser();
            user.setRole(moderatorRole);
            userRepository.save(user);
        } else if (action.equalsIgnoreCase("REJECTED")) {
            request.setStatus(ModeratorRequest.Status.REJECTED);
            request.setReviewedBy(admin);
            request.setReviewedAt(LocalDateTime.now());
        } else {
            throw new RuntimeException("Invalid action. Use APPROVE or REJECT.");
        }

        ModeratorRequest updated = moderatorRequestRepository.save(request);
        return mapToDto(updated);
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
}

