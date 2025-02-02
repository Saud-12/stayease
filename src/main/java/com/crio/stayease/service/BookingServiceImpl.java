package com.crio.stayease.service;

import com.crio.stayease.dto.*;
import com.crio.stayease.entity.Booking;
import com.crio.stayease.entity.Guest;
import com.crio.stayease.entity.Hotel;
import com.crio.stayease.entity.User;
import com.crio.stayease.entity.enums.BookingStatus;
import com.crio.stayease.entity.enums.Role;
import com.crio.stayease.exception.MaximumGuestLimitReachedException;
import com.crio.stayease.exception.NoAvailableRoomsException;
import com.crio.stayease.exception.ResourceNotFoundException;
import com.crio.stayease.exception.UnauthorizedAccessException;
import com.crio.stayease.repository.BookingRepository;
import com.crio.stayease.repository.GuestRepository;
import com.crio.stayease.repository.HotelRepository;
import com.crio.stayease.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service("bookingService")
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService{

    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    @PreAuthorize("hasAnyRole('ADMIN','HOTEL_MANAGER','CUSTOMER')")
    public BookingDto createNewBooking(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(() -> new ResourceNotFoundException("Hotel with id " + hotelId + " does not exists!"));
        User currentUser=getCurrentUser();
        if(hotel.getRoomsCount()<=0){
            throw new NoAvailableRoomsException("Rooms are not available for this hotel, cannot book the room!");
        }
        hotel.setRoomsCount(hotel.getRoomsCount()-1);
        Booking booking=Booking.builder()
                .user(currentUser)
                .hotel(hotel)
                .bookingStatus(BookingStatus.ACTIVE)
                .build();
        bookingRepository.save(booking);
        //hotelRepository.save(hotel);

        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','HOTEL_MANAGER') and " +
            "(hasRole('ADMIN') or " +
            "@bookingService.isHotelManagerOfBooking(#bookingId) or " +
            "@bookingService.isBookingOwner(#bookingId))")
    public BookingDto getBookingById(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking with id: "+bookingId+" not found!"));
        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<BookingDto> getAllBookingsOfUser(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User with id: "+userId+" does not exists!"));
       User currentUser=getCurrentUser();
       Role currentUserRole=currentUser.getRole();
       if(currentUser.getId().equals(userId) || currentUserRole==Role.ADMIN){
           return bookingRepository.findByUser(user).stream()
                   .map(booking-> modelMapper.map(booking,BookingDto.class))
                   .collect(Collectors.toList());
       }
       throw new UnauthorizedAccessException("You are not authorized to perform this operation");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','HOTEL_MANAGER') and " +
            "(hasRole('ADMIN') or " +
            "@bookingService.isHotelManagerOfBooking(#bookingId))")
    public BookingDto updateBookingStatusById(Long bookingId, UpdateBookingStatusRequest request) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking with id: "+bookingId+" not found!"));
        if(booking.getBookingStatus()==BookingStatus.CANCELLED){
            throw new IllegalStateException("Booking with id: "+bookingId+" has already been cancelled!");
        }
        booking.setBookingStatus(request.getBookingStatus());
        return modelMapper.map(bookingRepository.save(booking),BookingDto.class);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBookingById(Long bookingId) {
        log.info("fetching booking with id: {}",bookingId);
        if(!bookingRepository.existsById(bookingId)){
            throw new ResourceNotFoundException("Booking with id: "+bookingId+" does not exists!");
        }
        log.info("Successfully fetched booking with id: {}",bookingId);
        bookingRepository.deleteById(bookingId);
        log.info("Successfully deleted booking with id: {}",bookingId);
    }

    @Override
    @PreAuthorize("hasRole(`HOTEL_MANAGER`) and "+
            "@bookingService.isHotelManagerOfBooking(#bookingId)")
    public BookingDto cancelBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking with id: "+bookingId+" does not exists!"));
        if(booking.getBookingStatus()==BookingStatus.CANCELLED){
            throw new IllegalStateException("Booking has already been cancelled");
        }
        if(booking.getBookingStatus()!=BookingStatus.ACTIVE){
            throw new IllegalStateException("Booking cannot be cancelled in current status");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);
        return modelMapper.map(bookingRepository.save(booking),BookingDto.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','HOTEL_MANAGER') and " +
            "(hasRole('ADMIN') or " +
            "@bookingService.isHotelManagerOfBooking(#bookingId) or " +
            "@bookingService.isBookingOwner(#bookingId))")
    public BookingDto addGuests(Long bookingId,AddGuestRequest request) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking with id: "+bookingId+" does not exists!"));
        if(booking.getBookingStatus()==BookingStatus.CANCELLED){
            throw new IllegalStateException("Booking has already been cancelled");
        }
        if(request.getGuests()==null || request.getGuests().isEmpty()){
            throw new IllegalStateException("No Guests to add is found!");
        }
        if(booking.getBookingStatus()!=BookingStatus.ACTIVE){
            throw new IllegalStateException("Booking is not in active state to add guests!");
        }
        int MAX_GUESTS_PER_BOOKING = 2;
        if(booking.getGuests().size()+request.getGuests().size()> MAX_GUESTS_PER_BOOKING){
            throw new MaximumGuestLimitReachedException("Cannot add more than 2 guests!");
        }
        List<Guest> guestsList=request.getGuests().stream()
                .map(guestDto ->{
                    Guest guest=modelMapper.map(guestDto,Guest.class);
                    guest.setBooking(booking);
                    return guest;
                } )
                .collect(Collectors.toList());
        booking.getGuests().addAll(guestsList);
        return modelMapper.map(bookingRepository.save(booking),BookingDto.class);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','HOTEL_MANAGER') and " +
            "(hasRole('ADMIN') or " +
            "@bookingService.isHotelManagerOfBooking(#bookingId) or " +
            "@bookingService.isBookingOwner(#bookingId))")
    public BookingDto removeGuests(Long bookingId, RemoveGuestRequest request) {
        Booking booking=bookingRepository.findById(bookingId).orElseThrow(()->new ResourceNotFoundException("Booking with id: "+bookingId+" does not exists!"));
        if(booking.getBookingStatus()==BookingStatus.CANCELLED){
            throw new IllegalStateException("Booking has already been cancelled");
        }
        if(booking.getBookingStatus()!=BookingStatus.ACTIVE){
            throw new IllegalStateException("Booking is not in active state to remove guests!");
        }
        if(request.getGuestIds()==null || request.getGuestIds().isEmpty()){
            throw new IllegalStateException("Provide 1 or 2 guests to be removed");
        }

        List<Guest> guestsToRemove=guestRepository.findAllById(request.getGuestIds());
        booking.getGuests().removeAll(guestsToRemove);
        return modelMapper.map(bookingRepository.save(booking),BookingDto.class);
    }

    @Override
    @PreAuthorize("hasRole('HOTEL_MANAGER') and "+
            "@bookingService.isHotelManagerOfBooking(#bookingId)")
    public BookingDto checkInBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking with id: "+bookingId+" does not exists!"));
        if(booking.getBookingStatus()!=BookingStatus.ACTIVE){
            throw new IllegalStateException("Booking cannot be checked in due to current status");
        }
        booking.setBookingStatus(BookingStatus.CHECKED_IN);
        booking.setCheckInTime(LocalDateTime.now());
        return modelMapper.map(bookingRepository.save(booking), BookingDto.class);
    }

    @Override
    @PreAuthorize("hasRole('HOTEL_MANAGER') and "+
            "@bookingService.isHotelManagerOfBooking(#bookingId)")
    public BookingDto checkOutBooking(Long bookingId) {
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking with id: "+bookingId+" does not exists!"));
        if(booking.getBookingStatus()!=BookingStatus.CHECKED_IN){
            throw new IllegalStateException("Booking cannot be checked in due to current status");
        }
        booking.setBookingStatus(BookingStatus.CHECKED_OUT);
        booking.setCheckOutTime(LocalDateTime.now());
        return modelMapper.map(bookingRepository.save(booking),BookingDto.class);
    }

    public boolean isHotelManagerOfBooking(Long bookingId){
        return bookingRepository.findById(bookingId)
                .map(booking -> {
                    User currentUser=getCurrentUser();
                    return currentUser.getRole()==Role.HOTEL_MANAGER &&
                            booking.getHotel().getHotelManager().getId().equals(currentUser.getId());
                }).orElse(false);
    }

    public boolean isBookingOwner(Long bookingId){
        return bookingRepository.findById(bookingId)
                .map(booking -> booking.getUser().getId().equals(getCurrentUser().getId()))
                .orElse(false);
    }

    private User getCurrentUser(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
