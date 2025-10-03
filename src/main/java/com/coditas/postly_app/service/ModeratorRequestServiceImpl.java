package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.ModeratorRequestDto;
import com.coditas.postly_app.entity.ModeratorRequest;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.ModeratorRequestRepository;
import com.coditas.postly_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModeratorRequestServiceImpl{
//public class ModeratorRequestServiceImpl implements ModeratorRequestService {

    private final ModeratorRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Autowired
    public ModeratorRequestServiceImpl(ModeratorRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

//    @Override
//    public ModeratorRequestDto createRequest(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        ModeratorRequest request = new ModeratorRequest();
//        request.setUser(user);
//        request.setStatus(ModeratorRequest.Status.PENDING);
//        return mapToDto(requestRepository.save(request));
//    }
//
//    @Override
//    public List<ModeratorRequestDto> getAllRequests() {
//        return requestRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public ModeratorRequestDto reviewRequest(Long requestId, Long adminId, boolean approved) {
//        ModeratorRequest request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
//        request.setStatus(approved ? ModeratorRequest.Status.APPROVED : ModeratorRequest.Status.REJECTED);
//        User user = request.getUser();
//        if (approved) {
//            user.setModerator(true);
//        }
//        return mapToDto(requestRepository.save(request));
//    }
//
//    private ModeratorRequestDto mapToDto(ModeratorRequest request) {
//        ModeratorRequestDto dto = new ModeratorRequestDto();
//        dto.setId(request.getId());
//        dto.setStatus(String.valueOf(request.getStatus()));
//        dto.setUsername(request.getUser().getUsername());
//        dto.setRequestedAt(request.getRequestedAt());
//        dto.setReviewedBy(request.getReviewedBy());
//        return dto;
//    }
}
