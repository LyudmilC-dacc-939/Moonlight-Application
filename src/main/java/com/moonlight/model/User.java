package com.moonlight.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity(name = "USERS")
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
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
    @Size(min = 8, max = 100, message = "User's password must be between {min} and {max} symbols")
    private String password;

    private Instant dateCreated;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @JsonBackReference
    private UserRole userRole;
}
