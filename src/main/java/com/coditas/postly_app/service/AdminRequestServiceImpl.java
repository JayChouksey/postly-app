package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.AdminRequestDto;
import com.coditas.postly_app.dto.AdminUpdateRequestDto;
import com.coditas.postly_app.dto.UserRequestDto;
import com.coditas.postly_app.entity.Role;
import com.coditas.postly_app.entity.User;
import com.coditas.postly_app.repository.AdminRequestRepository;
import com.coditas.postly_app.repository.RoleRepository;
import com.coditas.postly_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRequestServiceImpl implements AdminRequestService {

    AdminRequestRepository adminRequestRepository;

    @Override
    public String createRequest(UserRequestDto userRequestDto) {
        return "";
    }

    @Override
    public List<AdminRequestDto> getAllPendingRequests() {
        return List.of();
    }

    @Override
    public AdminRequestDto reviewRequest(Long requestId, AdminUpdateRequestDto adminUpdateRequestDto) {
        return null;
    }
}

