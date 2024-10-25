package com.moonlight.service;

import com.moonlight.dto.user.*;
import com.moonlight.model.enums.ReservationStatus;
import com.moonlight.model.user.User;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

public interface UserService {
    @Operation(summary = "Register a new user", description = "Creates a new user in the database.")
    void registerUser(UserRequest userRequest);

    @Operation(summary = "Get user by ID", description = "Retrieves an user by their ID.")
    User getUserById(Long id);

    @Operation(summary = "Retrieve a user by email", description = "Returns a User account information found by their email address.")
    User getUserByEmail(String email);

    @Operation(summary = "Deletes an User by ID", description = "Deletes an user from the database by their ID")
    void deleteUser(Long id);

    @Operation(summary = "Logs in an Existing User", description = "Login an user using their email and password")
    String login(LoginRequest loginRequest);

    @Operation(summary = "Gets user email", description = "Gets user by his email.")
    User findByEmail(String email);

    @Operation(summary = "User updates information", description = "User can update his own information")
    User updateUser(UpdateUserRequest updateUserRequest, Long userId);

    @Operation(summary = "List all users", description = "Provides a pageable list of users to admin")
    List<User> getPageableUsersList(int skip, int take);

    @Operation(summary = "Logged user changes personal password", description = "Changes password for user's personal account by his request")
    User changePassword(ChangePasswordRequest changePasswordRequest);

    @Operation(summary = "Generates new password for user", description = "Generates a new password if given a registered email" +
            " then saves the changes in the database")
    void resetPassword(ResetPasswordRequest passwordRequest);

    @Operation(summary = "User can see his own reservation", description = "User can find all of his own reservations, and filter by paiment status")
    Map<String, Object> getUserReservations(User user, ReservationStatus reservationStatus);
}
