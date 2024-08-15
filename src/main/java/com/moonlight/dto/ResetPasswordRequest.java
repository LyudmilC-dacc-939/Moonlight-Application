package com.moonlight.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotNull(message = "Email cannot be null!")
    @Size(min = 5, max = 255, message = "Mail must be between {min} and {max} characters")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Invalid Email")
    private String email;

}
