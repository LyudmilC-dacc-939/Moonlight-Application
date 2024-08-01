package com.moonlight.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotNull(message = "Email cannot be null!")
    private String eMail;
    @NotNull(message = "Password cannot be null!")
    private String password;
}
