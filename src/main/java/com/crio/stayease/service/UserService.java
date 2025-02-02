package com.crio.stayease.service;

import com.crio.stayease.dto.UpdatePasswordRequest;
import com.crio.stayease.dto.UpdateUserRoleRequest;
import com.crio.stayease.dto.UserDto;
import com.crio.stayease.entity.User;
import com.crio.stayease.entity.enums.Role;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto getUserById(Long id);
    User findUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUserById(Long id,UserDto userDto);
    void deleteUserById(Long id);
    UserDto updateUserRole(Long id, UpdateUserRoleRequest request);
}
