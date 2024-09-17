package com.moonlight.model.bar;

import jakarta.persistence.*;
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

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "Bar",
            fetch = FetchType.EAGER)
    private Set<Screen> screens;

}
