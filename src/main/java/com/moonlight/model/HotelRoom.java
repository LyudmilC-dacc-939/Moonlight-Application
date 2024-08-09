package com.moonlight.model;

import com.moonlight.model.enums.RoomBedType;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(name = "HOTEL_ROOMS")
@Table(name = "rooms")
@Data
@NoArgsConstructor
public class HotelRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true)
    @Positive(message = "Room number must be a positive number")
    @NotNull(message = "Room should have room number")
    private Long roomNumber;

    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Room type is mandatory")
    private RoomType roomType;

    @Column(name = "room_view")
    @Enumerated(EnumType.STRING)
    private RoomView roomView;

    @Column(name = "bed_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Bed type is mandatory")
    private RoomBedType bedType;

    @NotEmpty(message = "Must include at least one amenity")
    private String description;
}
