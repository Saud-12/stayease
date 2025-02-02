package com.crio.stayease.entity;

import com.crio.stayease.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="hotel_id",nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @OneToMany(mappedBy ="booking",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Guest> guests=new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
