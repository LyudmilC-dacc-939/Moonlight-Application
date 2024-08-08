package com.moonlight.controller;

import com.moonlight.dto.LoginRequest;
import com.moonlight.dto.UserRequest;
import com.moonlight.model.User;
import com.moonlight.repository.UserRepository;
import com.moonlight.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "User API ", description = "API for managing users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "User Registration", description = "Registers new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully added",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))})
    @PostMapping(path = "/register")
    ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest) {
        userService.registerUser(userRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Searches an user by their id", description = "Returns user information by id")
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully found with id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "This user does not have permission to do that",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))})
    ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserById(id));
    }

    @Operation(summary = "Searches an user by email", description = "Returns user information by email")
    @GetMapping(path = "/get-by-email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully found with email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "This user does not have permission to do that",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))})
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    ResponseEntity<User> getUser(@RequestParam("userEmail") String userEmail) {
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserByEmail(userEmail));
    }

    @Operation(summary = "Deletes user by id", description = "Deletes user information by id")
    @DeleteMapping(path = "/account-deletion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully deleted",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "This user does not have permission to do that",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))})
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    ResponseEntity<?> deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Operation(summary = "User provides email and password credentials to login",
            description = "Based on user input, for login if successful, generates a JWTToken(String) which the user " +
                    "authenticates himself as logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully logged",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))})
    @PostMapping(path = "/login")
    ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequest));
    }
}
