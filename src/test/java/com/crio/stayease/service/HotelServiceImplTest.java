package com.crio.stayease.service;

import com.crio.stayease.dto.HotelDto;
import com.crio.stayease.dto.UserDto;
import com.crio.stayease.entity.Hotel;
import com.crio.stayease.entity.User;
import com.crio.stayease.entity.enums.Role;
import com.crio.stayease.exception.ResourceNotFoundException;
import com.crio.stayease.exception.UnauthorizedAccessException;
import com.crio.stayease.repository.HotelRepository;
import com.crio.stayease.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceImplTest {
    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel testHotel;
    private HotelDto testHotelDto;
    private User testUser;
    private UserDto testUserDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("manager@hotel.com");
        testUser.setRole(Role.HOTEL_MANAGER);

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setEmail("manager@hotel.com");
        testUserDto.setRole(Role.HOTEL_MANAGER);

        // Setup test hotel
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");
        testHotel.setLocation("Test Location");
        testHotel.setRoomsCount(100);
        testHotel.setDescription("Test Description");
        testHotel.setHotelManager(testUser);

        testHotelDto = new HotelDto();
        testHotelDto.setId(1L);
        testHotelDto.setName("Test Hotel");
        testHotelDto.setLocation("Test Location");
        testHotelDto.setRoomsCount(100);
        testHotelDto.setDescription("Test Description");
        testHotelDto.setHotelManager(testUserDto);

        // Setup security context
        authentication = new UsernamePasswordAuthenticationToken(testUser, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_HOTEL_MANAGER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Create new hotel when user is ADMIN")
    @WithMockUser(roles = "ADMIN")
    void createNewHotel_Success() {
        // Arrange
        when(modelMapper.map(testHotelDto, Hotel.class)).thenReturn(testHotel);
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);
        when(modelMapper.map(testHotel, HotelDto.class)).thenReturn(testHotelDto);

        // Act
        HotelDto result = hotelService.createNewHotel(testHotelDto);

        // Assert
        assertNotNull(result);
        assertEquals(testHotelDto.getName(), result.getName());
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    @DisplayName("Get hotel by ID")
    void getHotelById_Success() {
        // Arrange
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(modelMapper.map(testHotel, HotelDto.class)).thenReturn(testHotelDto);

        // Act
        HotelDto result = hotelService.getHotelById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testHotelDto.getId(), result.getId());
        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Throw exception when hotel not found")
    void getHotelById_NotFound() {
        // Arrange
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                hotelService.getHotelById(999L));
        verify(hotelRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Get all hotels")
    void getAllHotels_Success() {
        // Arrange
        List<Hotel> hotels = Arrays.asList(testHotel);
        when(hotelRepository.findAll()).thenReturn(hotels);
        when(modelMapper.map(any(Hotel.class), eq(HotelDto.class))).thenReturn(testHotelDto);

        // Act
        List<HotelDto> result = hotelService.getAllHotels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hotelRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Throw exception when unauthorized user attempts to update hotel")
    @WithMockUser(roles = "HOTEL_MANAGER")
    void updateHotelById_Unauthorized() {
        // Arrange
        User differentManager = new User();
        differentManager.setId(2L);
        differentManager.setRole(Role.HOTEL_MANAGER);

        Hotel hotelWithDifferentManager = testHotel;
        hotelWithDifferentManager.setHotelManager(differentManager);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotelWithDifferentManager));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () ->
                hotelService.updateHotelById(1L, testHotelDto));
    }

    @Test
    @DisplayName("Assign hotel manager successfully")
    @WithMockUser(roles = "ADMIN")
    void assignHotelManager_Success() {
        // Arrange
        User newManager = new User();
        newManager.setId(2L);
        newManager.setRole(Role.CUSTOMER);

        Hotel hotelWithoutManager = testHotel;
        hotelWithoutManager.setHotelManager(null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(newManager));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotelWithoutManager));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(testHotel);
        when(modelMapper.map(testHotel, HotelDto.class)).thenReturn(testHotelDto);

        // Act
        HotelDto result = hotelService.assignHotelManager(1L, 2L);

        // Assert
        assertNotNull(result);
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }

    @Test
    @DisplayName("Throw exception when assigning already assigned manager")
    @WithMockUser(roles = "ADMIN")
    void assignHotelManager_AlreadyAssigned() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                hotelService.assignHotelManager(1L, 1L));
    }

    @Test
    @DisplayName("Delete hotel when user is ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteHotelById_Success() {
        // Arrange
        when(hotelRepository.existsById(1L)).thenReturn(true);
        doNothing().when(hotelRepository).deleteById(1L);

        // Act
        hotelService.deleteHotelById(1L);

        // Assert
        verify(hotelRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Throw exception when deleting non-existent hotel")
    @WithMockUser(roles = "ADMIN")
    void deleteHotelById_NotFound() {
        // Arrange
        when(hotelRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                hotelService.deleteHotelById(999L));
    }
}
