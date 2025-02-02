package com.crio.stayease.dto;

import com.crio.stayease.entity.Booking;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
