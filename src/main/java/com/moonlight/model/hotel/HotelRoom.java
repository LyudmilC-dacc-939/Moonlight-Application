package com.moonlight.model.hotel;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moonlight.model.enums.RoomBedType;
import com.moonlight.model.enums.RoomType;
import com.moonlight.model.enums.RoomView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @NotNull(message = "Room view is mandatory")
    private RoomView roomView;

    @Column(name = "bed_type")
    @Enumerated(EnumType.STRING)
    private RoomBedType bedType;

    @ManyToMany
    @JoinTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @NotEmpty(message = "Must include at least one amenity")
    private Set<Amenity> amenities = new HashSet<>();

    @OneToMany(mappedBy = "hotelRoom")
    @JsonManagedReference
    private List<HotelRoomReservation> hotelRoomReservations;

}
