package com.moonlight.model.bar;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Entity
@Table(name = "bars")
@Data
public class Bar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String barName;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "bar",
            fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Screen> screens;
}
