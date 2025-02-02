package com.crio.stayease.service;

import com.crio.stayease.dto.UpdatePasswordRequest;
import com.crio.stayease.dto.UserDto;
import com.crio.stayease.entity.User;
import com.crio.stayease.exception.ResourceNotFoundException;
import com.crio.stayease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @PreAuthorize("hasRole('CUSTOMER') and #id==principal.id")
    public UserDto updateUserPassword(Long id, UpdatePasswordRequest request) {
        log.info("Fetching user with id: "+id);
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with id: "+id+" does not exists!"));
        log.info("Successfully fetched user with id: "+id);
        if(!matches(request.getOldPassword(), user.getPassword())){
            throw new IllegalArgumentException("Old password is incorrect!");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty!");
        }
        log.info("Attempting to set new password");
        user.setPassword(encodePassword(request.getPassword()));
        log.info("New Password is set successfully");
        return modelMapper.map(userRepository.save(user),UserDto.class);
    }

    private String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    private boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
