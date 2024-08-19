package com.moonlight.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotNull(message = "Email cannot be null!")
    private String email;
    @NotNull(message = "Password cannot be null!")
    private String password;
}
