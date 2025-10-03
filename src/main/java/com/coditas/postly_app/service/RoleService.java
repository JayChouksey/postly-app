package com.coditas.postly_app.service;

import com.coditas.postly_app.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto createRole(RoleDto roleDto);
    List<RoleDto> getAllRoles();
}
