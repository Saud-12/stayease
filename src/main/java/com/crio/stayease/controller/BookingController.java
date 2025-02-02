package com.crio.stayease.controller;

import com.crio.stayease.dto.AddGuestRequest;
import com.crio.stayease.dto.BookingDto;
import com.crio.stayease.dto.RemoveGuestRequest;
import com.crio.stayease.dto.UpdateBookingStatusRequest;
import com.crio.stayease.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/hotels/{hotelId}")
    public ResponseEntity<BookingDto> createNewBooking(@PathVariable Long hotelId){
        return new ResponseEntity<>(bookingService.createNewBooking(hotelId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<BookingDto>> getAllBookingsOfUser(@PathVariable Long userId){
        return ResponseEntity.ok(bookingService.getAllBookingsOfUser(userId));
    }

    @PutMapping("/update-booking_status/{id}")
    public ResponseEntity<BookingDto> updateBookingStatusById(@PathVariable Long id, @RequestBody UpdateBookingStatusRequest request){
        return ResponseEntity.ok(bookingService.updateBookingStatusById(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookingById(@PathVariable Long id){
       bookingService.deleteBookingById(id);
       return ResponseEntity.noContent().build();
    }

    @PutMapping("/cancel-booking/{id}")
    public ResponseEntity<BookingDto> cancelBookingById(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PostMapping("/add-guests/{id}")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long id, @RequestBody AddGuestRequest request){
        return ResponseEntity.ok(bookingService.addGuests(id,request));
    }

    @PutMapping("/remove-guests/{id}")
    public ResponseEntity<BookingDto> removeGuests(@PathVariable Long id, @RequestBody RemoveGuestRequest request){
        return ResponseEntity.ok(bookingService.removeGuests(id,request));
    }

    @PatchMapping("/check-in-booking/{id}")
    public ResponseEntity<BookingDto> checkInBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.checkInBooking(id));
    }

    @PatchMapping("/check-out-booking/{id}")
    public ResponseEntity<BookingDto> checkOutBooking(@PathVariable Long id){
        return ResponseEntity.ok(bookingService.checkOutBooking(id));
    }
}
