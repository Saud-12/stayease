package com.crio.stayease.dto;

import com.crio.stayease.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto {
    @Email
    @NotEmpty(message = "name cannot be null or empty")
    private String email;

    @NotEmpty(message="first name cannot be null or empty")
    @Size(min=4,max=15, message = "first name should have 4-15 range of characters")
    private String firstName;

    @NotEmpty(message="last name cannot be null or empty")
    @Size(min=4,max=15, message = "last name should have 4-15 range of characters")
    private String lastName;

    private String password;

    private Role role;
}
