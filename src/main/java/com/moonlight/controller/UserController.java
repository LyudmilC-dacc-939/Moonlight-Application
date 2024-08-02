package com.moonlight.controller;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.LoginRequest;
import com.moonlight.dto.UserRequest;
import com.moonlight.model.User;
import com.moonlight.repository.UserRepository;
import com.moonlight.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Creates new user", description = "Returns created user")
    @PostMapping
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(userRequest));
    }

    @Operation(summary = "Searches an user by their id", description = "Returns user")
    @GetMapping(path = "/{id}")
    //@PreAuthorize
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserById(id));
    }

    @Operation(summary = "Searches an user by email", description = "Returns user")
    @GetMapping(path = "/get-by-email")
    //@PreAuthorize
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<User> getUser(@RequestParam("userEmail") String userEmail) {
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserByEmail(userEmail));
    }

    @Operation(summary = "Deletes user by id", description = "Deletes user")
    @DeleteMapping(path = "/account-deletion")
    //@PreAuthorize
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<?> deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Operation(summary = "User provides email and password credentials to login",
            description = "Based on user input, for login if successful, generates a JWTToken(String) which the user " +
                    "authenticates himself as logged in")
    @PostMapping(path = "/auth")
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequest));
    }
}
