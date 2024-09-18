package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moonlight.model.enums.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bars")
@Data
public class Bar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bar_name")
    @NotNull(message = "bar must have distinct name")
    private String barName;

    @ElementCollection(targetClass = Screen.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "bar_screens", joinColumns = @JoinColumn(name = "bar_id"))
    @Column(name = "screens")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Bar must have at least 1 screen")
    private Set<Screen> screens;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bar")
    @JsonManagedReference
    private List<Seat> seats;
}
