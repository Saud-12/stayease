package com.crio.stayease.dto;

import com.crio.stayease.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingStatusRequest {
    private BookingStatus bookingStatus;
}
