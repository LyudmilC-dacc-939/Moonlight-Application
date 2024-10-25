package com.moonlight.model.hotel;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "Amenity")
@Table(name = "amenities")
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @NotEmpty(message = "Must include an amenity")
    private String amenity;

    @ManyToMany(mappedBy = "amenities", fetch = FetchType.LAZY)
    private Set<HotelRoom> hotelRooms = new HashSet<>();
}
