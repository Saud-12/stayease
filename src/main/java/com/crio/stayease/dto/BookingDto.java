package com.crio.stayease.dto;

import com.crio.stayease.entity.enums.BookingStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private BookingStatus bookingStatus;
    @Size(min=0,max=2, message = "Can have no guests or can have maximum 2 guests")
    private List<GuestDto> guests;
    private LocalDateTime createdAt;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
