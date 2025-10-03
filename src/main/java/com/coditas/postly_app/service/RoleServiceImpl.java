package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.RoleDto;
import com.coditas.postly_app.entity.Role;
import com.coditas.postly_app.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        Role role = new Role();
        role.setName(Role.RoleName.valueOf(roleDto.getName()));
        role.setDescription(roleDto.getDescription());
        return mapToDto(roleRepository.save(role));
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private RoleDto mapToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(String.valueOf(role.getName()));
        dto.setDescription(role.getDescription());
        return dto;
    }
}
