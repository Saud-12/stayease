package com.crio.stayease.service;

import com.crio.stayease.dto.AddGuestRequest;
import com.crio.stayease.dto.BookingDto;
import com.crio.stayease.dto.GuestDto;
import com.crio.stayease.dto.UpdateBookingStatusRequest;
import com.crio.stayease.entity.Booking;
import com.crio.stayease.entity.Guest;
import com.crio.stayease.entity.Hotel;
import com.crio.stayease.entity.User;
import com.crio.stayease.entity.enums.BookingStatus;
import com.crio.stayease.entity.enums.Role;
import com.crio.stayease.exception.MaximumGuestLimitReachedException;
import com.crio.stayease.exception.NoAvailableRoomsException;
import com.crio.stayease.repository.BookingRepository;
import com.crio.stayease.repository.GuestRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User testUser;
    private Hotel testHotel;
    private Booking testBooking;
    private BookingDto testBookingDto;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setRole(Role.CUSTOMER);

        // Setup test hotel
        testHotel = new Hotel();
        testHotel.setId(1L);
        testHotel.setName("Test Hotel");
        testHotel.setRoomsCount(5);

        // Setup test booking
        testBooking = Booking.builder()
                .id(1L)
                .user(testUser)
                .hotel(testHotel)
                .bookingStatus(BookingStatus.ACTIVE)
                .guests(new ArrayList<>())
                .build();

        testBookingDto = new BookingDto();
        // Set DTO properties...

        // Setup security context
        authentication = new UsernamePasswordAuthenticationToken(testUser, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Create new booking successfully")
    @WithMockUser(roles = "CUSTOMER")
    void createNewBooking_Success() {
        // Arrange
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(modelMapper.map(any(Booking.class), eq(BookingDto.class))).thenReturn(testBookingDto);

        // Act
        BookingDto result = bookingService.createNewBooking(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testHotel.getRoomsCount(), 4); // Decreased by 1
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Throw exception when no rooms available")
    @WithMockUser(roles = "CUSTOMER")
    void createNewBooking_NoRoomsAvailable() {
        // Arrange
        testHotel.setRoomsCount(0);
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(testHotel));

        // Act & Assert
        assertThrows(NoAvailableRoomsException.class, () ->
                bookingService.createNewBooking(1L));
    }

    @Test
    @DisplayName("Get booking by ID when authorized")
    @WithMockUser(roles = "CUSTOMER")
    void getBookingById_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(modelMapper.map(testBooking, BookingDto.class)).thenReturn(testBookingDto);

        // Act
        BookingDto result = bookingService.getBookingById(1L);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get all bookings of user")
    @WithMockUser(roles = "CUSTOMER")
    void getAllBookingsOfUser_Success() {
        // Arrange
        List<Booking> bookings = Arrays.asList(testBooking);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(bookingRepository.findByUser(testUser)).thenReturn(bookings);
        when(modelMapper.map(any(Booking.class), eq(BookingDto.class))).thenReturn(testBookingDto);

        // Act
        List<BookingDto> result = bookingService.getAllBookingsOfUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Update booking status successfully")
    @WithMockUser(roles = "HOTEL_MANAGER")
    void updateBookingStatusById_Success() {
        // Arrange
        UpdateBookingStatusRequest request = new UpdateBookingStatusRequest();
        request.setBookingStatus(BookingStatus.CHECKED_IN);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(modelMapper.map(testBooking, BookingDto.class)).thenReturn(testBookingDto);

        // Act
        BookingDto result = bookingService.updateBookingStatusById(1L, request);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Add guests successfully")
    @WithMockUser(roles = "CUSTOMER")
    void addGuests_Success() {
        // Arrange
        AddGuestRequest request = new AddGuestRequest();
        List<GuestDto> guestDtos = Arrays.asList(new GuestDto());
        request.setGuests(guestDtos);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(modelMapper.map(testBooking, BookingDto.class)).thenReturn(testBookingDto);
        when(modelMapper.map(any(GuestDto.class), eq(Guest.class))).thenReturn(new Guest()); // Fix

        // Act
        BookingDto result = bookingService.addGuests(1L, request);

        // Assert
        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Throw exception when adding more than maximum guests")
    @WithMockUser(roles = "CUSTOMER")
    void addGuests_ExceedsMaximum() {
        // Arrange
        AddGuestRequest request = new AddGuestRequest();
        List<GuestDto> guestDtos = Arrays.asList(new GuestDto(), new GuestDto(), new GuestDto());
        request.setGuests(guestDtos);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // Act & Assert
        assertThrows(MaximumGuestLimitReachedException.class, () ->
                bookingService.addGuests(1L, request));
    }

    @Test
    @DisplayName("Check in booking successfully")
    @WithMockUser(roles = "HOTEL_MANAGER")
    void checkInBooking_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(modelMapper.map(testBooking, BookingDto.class)).thenReturn(testBookingDto);

        // Act
        BookingDto result = bookingService.checkInBooking(1L);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.CHECKED_IN, testBooking.getBookingStatus());
        assertNotNull(testBooking.getCheckInTime());
    }

    @Test
    @DisplayName("Check out booking successfully")
    @WithMockUser(roles = "HOTEL_MANAGER")
    void checkOutBooking_Success() {
        // Arrange
        testBooking.setBookingStatus(BookingStatus.CHECKED_IN);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(modelMapper.map(testBooking, BookingDto.class)).thenReturn(testBookingDto);

        // Act
        BookingDto result = bookingService.checkOutBooking(1L);

        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.CHECKED_OUT, testBooking.getBookingStatus());
        assertNotNull(testBooking.getCheckOutTime());
    }

    @Test
    @DisplayName("Verify hotel manager authorization")
    void isHotelManagerOfBooking_Success() {
        // Arrange
        User hotelManager = new User();
        hotelManager.setId(2L);
        hotelManager.setRole(Role.HOTEL_MANAGER);
        testHotel.setHotelManager(hotelManager);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // Act
        boolean result = bookingService.isHotelManagerOfBooking(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Verify booking owner")
    void isBookingOwner_Success() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        // Act
        boolean result = bookingService.isBookingOwner(1L);

        // Assert
        assertTrue(result);
    }
}
