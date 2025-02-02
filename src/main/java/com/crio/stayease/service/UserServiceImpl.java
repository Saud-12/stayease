package com.crio.stayease.service;

import com.crio.stayease.dto.UpdatePasswordRequest;
import com.crio.stayease.dto.UpdateUserRoleRequest;
import com.crio.stayease.dto.UserDto;
import com.crio.stayease.entity.User;
import com.crio.stayease.exception.ResourceNotFoundException;
import com.crio.stayease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Fetching user with email: "+username);
       User user=userRepository.findByEmail(username).orElseThrow(()->new ResourceNotFoundException("User with email: "+username+" does not exists!"));
       log.info("Successfully fetched user with email: "+username);
       return user;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #id==principal.id)")
    public UserDto getUserById(Long id) {
        log.info("Fetching user with id: "+id);
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with id: "+id+" does not exists!"));
        log.info("Successfully fetched user with id: "+id);
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    public User findUserById(Long id) {
        log.info("Fetching user with id: {}",id);
        return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with id: "+id+" does not exists!"));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        log.info("Fetching All Users");
        return userRepository.findAll().stream()
                .map(user->modelMapper.map(user,UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #id==principal.id)")
    public UserDto updateUserById(Long id, UserDto userDto) {
        log.info("Fetching user with id: "+id);
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with id: "+id+" does not exists!"));
        log.info("Successfully fetched user with id: "+id);

        log.info("Attempting to update user with id: "+id);
        modelMapper.map(userDto,user);
        user=userRepository.save(user);
        log.info("Successfully updated user with id: "+id);
        return modelMapper.map(user,UserDto.class);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(Long id) {
        log.info("Fetching user with id: "+id);
        if(!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User with id: "+id+" does not exists!");
        }
        log.info("Attempting to delete user by id: "+id);
        userRepository.deleteById(id);
        log.info("Successfully deleted user by id: "+id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateUserRole(Long id, UpdateUserRoleRequest request) {
        log.info("Fetching user with id: "+id);
        User user=userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User with id: "+id+" does not exists!"));
        log.info("Attempting to update role of user with id: "+id);
        user.setRole(request.getRole());
        user=userRepository.save(user);
        log.info("Successfully updated user role with user id: "+id);
        return modelMapper.map(user,UserDto.class);
    }

}
