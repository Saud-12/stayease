package com.crio.stayease.service;

import com.crio.stayease.dto.HotelDto;
import com.crio.stayease.entity.Hotel;
import com.crio.stayease.entity.User;
import com.crio.stayease.entity.enums.Role;
import com.crio.stayease.exception.ResourceNotFoundException;
import com.crio.stayease.exception.UnauthorizedAccessException;
import com.crio.stayease.repository.HotelRepository;
import com.crio.stayease.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public HotelDto createNewHotel(HotelDto hotelDto) {
        log.info("Attempting to create new Hotel");
        Hotel hotel=modelMapper.map(hotelDto,Hotel.class);
        hotelDto=modelMapper.map(hotelRepository.save(hotel), HotelDto.class);
        log.info("Successfully created Hotel with id: "+hotelDto.getId());
        return hotelDto;
    }

    @Override
    @PreAuthorize("permitAll")
    public HotelDto getHotelById(Long id) {
        log.info("Fetching hotel with id: {}",id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel with id "+id+" does not exists!"));
        log.info("Successfully fetched hotel with id: {}",id);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    @PreAuthorize("permitAll")
    public List<HotelDto> getAllHotels() {
        log.info("Attempting to fetch all the hotels");
        return hotelRepository.findAll().stream()
                .map(hotel->modelMapper.map(hotel,HotelDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasRole(`HOTEL_MANAGER`)")
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Fetching hotel with id: {}",id);
        Hotel hotel=hotelRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel with id "+id+" does not exists!"));
        User currentuser=getCurrentUser();

        if(!hotel.getHotelManager().getId().equals(currentuser.getId())){
            throw new UnauthorizedAccessException("You are not authorized to perform this operation!");
        }

        log.info("Attempting to update hotel with id: {}",id);
        modelMapper.map(hotelDto,hotel);
        hotel=hotelRepository.save(hotel);
        log.info("Successfully updated hotel with id: {}",id);
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public HotelDto assignHotelManager(Long hotelId,Long userId) {
       User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User with id: "+userId+" does not exists!"));
       Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("Hotel with id "+hotelId+" does not exists!"));

       if(user.getRole()==Role.HOTEL_MANAGER && hotel.getHotelManager()!=null && hotel.getHotelManager().getId().equals(user.getId())){
           throw new IllegalStateException("User is already hotel manager for this hotel");
       }
       user.setRole(Role.HOTEL_MANAGER);
       hotel.setHotelManager(user);

       hotelRepository.save(hotel);
       return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteHotelById(Long id) {
        log.info("Fetching hotel with id: {}",id);
        if(!hotelRepository.existsById(id)){
            throw new ResourceNotFoundException("Hotel with id: "+id+" does not exists!");
        }
        log.info("Attempting to delete hotel with id: "+id);
        hotelRepository.deleteById(id);
        log.info("Successfully deleted hotel with id: "+id);
    }
    private User getCurrentUser(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
