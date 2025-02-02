package com.crio.stayease.controller;

import com.crio.stayease.dto.HotelDto;
import com.crio.stayease.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        return new ResponseEntity<>(hotelService.createNewHotel(hotelDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id){
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping()
    public ResponseEntity<List<HotelDto>> getAllHotel(){
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id,@RequestBody HotelDto hotelDto){
        return ResponseEntity.ok(hotelService.updateHotelById(id,hotelDto));
    }

    @PutMapping("/{hotelId}/assign-hotel_manager/{userId}")
    public ResponseEntity<HotelDto> assignHotelManager(@PathVariable Long hotelId,@PathVariable Long userId){
        return ResponseEntity.ok(hotelService.assignHotelManager(hotelId,userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long id){
        hotelService.deleteHotelById(id);
        return ResponseEntity.noContent().build();
    }
}
