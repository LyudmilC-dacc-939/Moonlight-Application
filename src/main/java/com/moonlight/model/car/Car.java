package com.moonlight.model.car;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moonlight.model.enums.CarType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "CARS")
@Table(name = "cars")
@Data
@NoArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Car type cannot be null")
    @Column(name = "Car_type", nullable = false)
    private CarType type;

    @NotEmpty(message = "Car brand cannot be empty")
    @Size(max = 50, message = "Car brand cannot exceed 50 characters")
    @Column(name = "Car_brand", nullable = false, length = 50)
    private String carBrand;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Column(name = "image_url")
    private List<FileResource> fileResources = new ArrayList<>();

    @Column(name = "Reservation_date")
    private LocalDate reservationDate;

}
