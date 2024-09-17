package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @JsonBackReference
    @JoinColumn(name = "bar_id", nullable = false)
    private Bar Bar;

    @NotNull
    @Column(unique = true)
    private String screenName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "screen")
    @JsonManagedReference
    private List<Event> events;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "screen")
    @JsonManagedReference
    private List<Seat> seats;

}