package com.crio.stayease.dto;

import com.crio.stayease.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {

    private Long id;
    private String name;
    private String location;
    private String description;
    private int roomsCount;
    private UserDto hotelManager;
}
