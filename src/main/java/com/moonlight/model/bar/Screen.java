package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moonlight.model.enums.ScreenName;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "screens")
@Data
@NoArgsConstructor
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bar Bar;

    @Enumerated(EnumType.STRING)
    private ScreenName screenName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "screen")
    @JsonManagedReference
    private List<Event> events;

    private Integer seatNumber;

    private final double seatPrice = 10.0;
}