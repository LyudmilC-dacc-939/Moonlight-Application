package com.moonlight.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotNull(message = "Current password cannot be null!")
    @Size(min = 8, max = 30, message = "User's current password must be between {min} and {max} symbols")
    private String currentPassword;

    @NotNull(message = "New password cannot be null!")
    @Size(min = 8, max = 30, message = "User's new password must be between {min} and {max} symbols")
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$",
            message = "The user’s new password must consist at least one numeric, one lowercase, one uppercase and one special characters")
    private String newPassword;
}
