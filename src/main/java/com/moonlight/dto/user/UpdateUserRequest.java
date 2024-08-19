package com.moonlight.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @Size(min = 2, max = 255, message = "First name must be between {min} and {max} characters")
    private String firstName;
    @Size(min = 2, max = 255, message = "Last name must be between {min} and {max} characters")
    private String lastName;
    @Size(max = 15, message = "Phone number length must be at most {max} characters and must start with '+' symbol")
    @Pattern(regexp = "^(\\+|00)[0-9-]{1,15}$", message = "Invalid phone number format")
    private String phoneNumber;
}
