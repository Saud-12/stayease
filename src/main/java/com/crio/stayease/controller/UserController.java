package com.crio.stayease.controller;

import com.crio.stayease.dto.UpdatePasswordRequest;
import com.crio.stayease.dto.UpdateUserRoleRequest;
import com.crio.stayease.dto.UserDto;
import com.crio.stayease.service.PasswordService;
import com.crio.stayease.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordService passwordService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping()
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/update-user/{id}")
    public ResponseEntity<UserDto> updateUserById(@PathVariable Long id,@Valid @RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.updateUserById(id,userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/update-user-role/{id}")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable Long id, @RequestBody UpdateUserRoleRequest request){
        return ResponseEntity.ok(userService.updateUserRole(id,request));
    }
    @PutMapping("/update-user-password/{id}")
    public ResponseEntity<UserDto> updateUserPassword(@PathVariable Long id, @RequestBody UpdatePasswordRequest request){
        return ResponseEntity.ok(passwordService.updateUserPassword(id,request));
    }
}
