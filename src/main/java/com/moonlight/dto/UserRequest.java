package com.moonlight.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @Column(name = "FIRST_NAME", nullable = false)
    @NotNull(message = "First name cannot be null!")
    @Size(min = 2, max = 255, message = "First name must be between 2 and 255 characters")
    private String firstName;
    @Column(name = "LAST_NAME", nullable = false)
    @NotNull(message = "Last name cannot be null!")
    @Size(min = 2, max = 255, message = "Last name must be between 2 and 255 characters")
    private String lastName;
    @Column(name = "EMAIL", nullable = false)
    @NotNull(message = "Email cannot be null!")
    @Size(min = 5, max = 255, message = "Mail must be between 5 and 255 characters")
    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "Invalid Email")
    private String email;
    @Column(name = "PHONE_NUMBER", nullable = false)
    @Size(max = 15, message = "phone number must start with '00' and must be maximum 15 characters")
    private String phoneNumber;
    @Column(name = "PASSWORD", nullable = false)
    @Size(min = 8,max = 255, message = "User's password must be between 8 and 255 symbols")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$",
            message = "The user’s password must consist at least one numeric, one lowercase, one uppercase and one special characters")
    private String password;
    @Column(name = "REPEAT_PASSWORD", nullable = false)
    @Size(min = 8,max = 255, message = "Repeated password must be between 8 and 255 symbols")
    private String repeatPassword;
    private Boolean isAgreedGDPR;
    private Boolean isAgreedEULA;
}
