package com.moonlight.service;

import com.moonlight.dto.LoginRequest;
import com.moonlight.dto.UserRequest;
import com.moonlight.model.User;
import io.swagger.v3.oas.annotations.Operation;


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
    @Operation(summary = "Gets user email",description = "Gets user by his email.")
    User findByEmail(String email);
}
