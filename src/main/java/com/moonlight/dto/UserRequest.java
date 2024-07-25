package com.moonlight.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@Getter
@Setter
public class UserRequest {
    @NotNull(message = "First name cannot be null!")
    @Size(min = 2, max = 255, message = "First name must be between 2 and 255 characters")
    private String firstName;
    @NotNull(message = "Last name cannot be null!")
    @Size(min = 2, max = 255, message = "Last name must be between 2 and 255 characters")
    private String lastName;
    @NotNull(message = "Email cannot be null!")
    @Size(min = 5, max = 255, message = "Mail must be between 5 and 255 characters")
    @Email
    private String email;
    @NotNull
    @Size(max = 15, message = "phone number must start with '00' and must be maximum 15 characters")
    private String phoneNumber;
    @NotNull
    @Size(min = 6,max = 30, message = "Password must be between 6 and 30 characters")
    private String password;
    @NotNull
    @Size(min = 6,max = 30, message = "Repeated password must be between 6 and 30 characters")
    private String repeatPassword;
    private Boolean isAgreedGDPR;
    private Boolean isAgreedEULA;

}
