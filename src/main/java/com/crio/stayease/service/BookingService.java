package com.crio.stayease.service;

import com.crio.stayease.dto.AddGuestRequest;
import com.crio.stayease.dto.BookingDto;
import com.crio.stayease.dto.RemoveGuestRequest;
import com.crio.stayease.dto.UpdateBookingStatusRequest;
import com.crio.stayease.entity.Booking;

import java.util.List;

public interface BookingService {
    BookingDto createNewBooking(Long hotelId);
    BookingDto getBookingById(Long bookingId);
    List<BookingDto> getAllBookingsOfUser(Long userId);
    BookingDto updateBookingStatusById(Long bookingId, UpdateBookingStatusRequest updateBookingStatusRequest);
    void deleteBookingById(Long bookingId);
    BookingDto cancelBooking(Long bookingId);
    BookingDto addGuests(Long bookingId,AddGuestRequest request);
    BookingDto removeGuests(Long bookingId, RemoveGuestRequest request);
    BookingDto checkInBooking(Long bookingId);
    BookingDto checkOutBooking(Long bookingId);
}
