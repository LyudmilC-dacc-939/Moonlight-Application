package com.moonlight.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moonlight.model.car.CarReservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "USERS")
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @Column(name = "FIRST_NAME", nullable = false)
    @Size(min = 2, max = 255, message = "First name must be between {min} and {max} characters")
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    @Size(min = 2, max = 255, message = "Last name must be between {min} and {max} characters")
    private String lastName;

    @Column(name = "EMAIL", unique = true, nullable = false)
    @Size(min = 5, max = 255, message = "Email address must be between {min} and {max} characters")
    private String emailAddress;

    @Column(name = "PHONE_NUMBER", nullable = false)
    @Size(max = 15, message = "Phone number length must be at most {max} characters and must start with '+' symbol")
    private String phoneNumber;

    @Column(name = "PASSWORD", nullable = false)
    @Size(min = 8, message = "User's password must be at least 8 characters long")
    private String password;

    private Instant dateCreated;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "role_id")
    @JsonBackReference
    private UserRole userRole;

    // This is not necessary, but I am adding it in case we want two-way connection between User/CarReservation
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<CarReservation> reservations = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.getUserRole()));
    }

    @Override
    public String getUsername() {
        return emailAddress;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
