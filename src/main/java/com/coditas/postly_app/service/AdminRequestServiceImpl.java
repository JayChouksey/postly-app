package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.AdminRequestDto;
import com.coditas.postly_app.entity.AdminRequest;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.AdminRequestRepository;
import com.coditas.postly_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminRequestServiceImpl{
//public class AdminRequestServiceImpl implements AdminRequestService {

    private final AdminRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminRequestServiceImpl(AdminRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

//    @Override
//    public AdminRequestDto createRequest(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        AdminRequest request = new AdminRequest();
//        request.setUser(user);
//        request.setStatus(AdminRequest.Status.PENDING);
//        return mapToDto(requestRepository.save(request));
//    }
//
//    @Override
//    public List<AdminRequestDto> getAllRequests() {
//        return requestRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public AdminRequestDto reviewRequest(Long requestId, Long superAdminId, boolean approved) {
//        AdminRequest request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
//        request.setStatus(approved ? AdminRequest.Status.APPROVED : AdminRequest.Status.REJECTED);
//        return mapToDto(requestRepository.save(request));
//    }
//
//    private AdminRequestDto mapToDto(AdminRequest request) {
//        AdminRequestDto dto = new AdminRequestDto();
//        dto.setId(request.getId());
//        dto.setStatus(String.valueOf(request.getStatus()));
//        dto.setUsername(request.getUser().getUsername());
//        dto.setRequestedAt(request.getRequestedAt());
//        dto.setReviewedBy(request.getReviewedBy());
//        return dto;
//    }
}

