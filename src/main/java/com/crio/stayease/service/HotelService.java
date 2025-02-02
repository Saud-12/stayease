package com.crio.stayease.service;

import com.crio.stayease.dto.HotelDto;

import java.util.List;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    List<HotelDto> getAllHotels();
    HotelDto updateHotelById(Long id,HotelDto hotelDto);
    HotelDto assignHotelManager(Long hotelId,Long userId);
    void deleteHotelById(Long id);

}
