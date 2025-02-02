package com.crio.stayease.dto;

import com.crio.stayease.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @Email
    @NotEmpty(message = "email cannot be null or empty")
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
