package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "bars")
@RequiredArgsConstructor
@Getter
public class Bar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String barName;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "Bar",
            fetch = FetchType.EAGER)
    @JsonManagedReference
    @NotNull
    private Set<Screen> screens;

}
