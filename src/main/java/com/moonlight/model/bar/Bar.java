package com.moonlight.model.bar;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Entity
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
    @NotNull
    private Set<Screen> screens;

}
