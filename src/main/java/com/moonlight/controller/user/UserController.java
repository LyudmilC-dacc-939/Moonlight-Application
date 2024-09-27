package com.moonlight.controller.user;

import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.user.*;
import com.moonlight.model.bar.BarReservation;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.restaurant.RestaurantReservation;
import com.moonlight.model.user.User;
import com.moonlight.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "User API", description = "API for managing users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private HotelRoomReservationService roomReservationService;
    @Autowired
    private CarReservationService carReservationService;
    @Autowired
    private RestaurantReservationService restaurantReservationService;
    @Autowired
    private BarReservationService barReservationService;

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
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserById(id));
    }

    @Operation(summary = "Searches an user by email", description = "Returns user information by email")
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
    @GetMapping(path = "/get-by-email")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<User> getUser(@RequestParam("userEmail") String userEmail) {
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserByEmail(userEmail));
    }

    @Operation(summary = "Deletes user by id", description = "Deletes user information by id")
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
    @DeleteMapping(path = "/account-deletion")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @SecurityRequirement(name = "bearerAuth")
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

    @Operation(summary = "Updating user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User successfully updated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))
    })
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<User> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest, @PathVariable("id") Long userId) {
        return new ResponseEntity<>(userService.updateUser(updateUserRequest, userId), HttpStatus.OK);
    }

    @Operation(summary = "List all users", description = "Provide pageable list of users to admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully found with id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: You do not have the necessary permissions",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))})
    @GetMapping(path = "/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<List<User>> getPageableUsersList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<User> users = userService.getPageableUsersList(page, size);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Authenticated user changes password by request",
            description = "Changes password for user's personal account, when provided with current password and " +
                    "designated new password, by his request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully changed password",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChangePasswordRequest.class))),
            @ApiResponse(responseCode = "401", description = "Access Denied. Unauthenticated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChangePasswordRequest.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden. Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChangePasswordRequest.class)))
    })
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping(path = "/change-password")
    ResponseEntity<User> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userService.changePassword(changePasswordRequest));
    }

    @Operation(summary = "Unauthenticated user requests a password reset",
            description = "Changes password for user, when provided with valid email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful password change request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordRequest.class))),
            @ApiResponse(responseCode = "400", description = "Format is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordRequest.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    //Unauthorized is an indication that the implementation has an issue
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordRequest.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordRequest.class)))
    })
    @PutMapping(path = "/reset-password")
    ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        userService.resetPassword(resetPasswordRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Finding User his own reservations", description = "Returns User all reservations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All user reservations found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "This user does not have permission to do that",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)))})
    @GetMapping(path = "/reservation")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<Map<String, Object>> getUserReservations(@AuthenticationPrincipal User user) {
        Map<String, Object> reservations = userService.getUserReservations(user);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @Operation(summary = "Admin can list reservations for a specific user",
            description = "Admin can choose an user and list their reservations by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(anyOf =
                                    {CarReservation.class, HotelRoomReservation.class, RestaurantReservation.class}))),
            @ApiResponse(responseCode = "204", description = "Successfully fetched, no data present",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(anyOf =
                                    {CarReservation.class, HotelRoomReservation.class, RestaurantReservation.class}))),
            @ApiResponse(responseCode = "400", description = "Request is not valid",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(anyOf =
                                    {CarReservation.class, HotelRoomReservation.class, RestaurantReservation.class}))),
            @ApiResponse(responseCode = "403", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(anyOf =
                                    {CarReservation.class, HotelRoomReservation.class, RestaurantReservation.class}))),
            @ApiResponse(responseCode = "404", description = "No data matching input found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(anyOf =
                                    {CarReservation.class, HotelRoomReservation.class})))
    })
    @SneakyThrows
    @GetMapping(path = "/list-reservations/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    ResponseEntity<?> getReservations(@RequestParam(value = "userId", required = false) Long userId,
                                      @RequestParam(value = "reservationType", defaultValue = "all", required = false) String reservationType) {
        if (userId != null) {
            Optional.ofNullable(userService.getUserById(userId)).orElseThrow(() ->
                    new RecordNotFoundException("User with id: " + userId + " not exist"));
        }

        Map<String, List<?>> resultMap = new HashMap<>();
        switch (reservationType) {
            case "hotel rooms", "hotel", "rooms":
                List<HotelRoomReservation> roomReservations = roomReservationService.getRoomReservationsByUserId(userId);
                if (roomReservations.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User has no room reservations");
                }
                resultMap.put("Hotel reservations: ", roomReservations);
                return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(resultMap);
            case "cars", "car":
                List<CarReservation> carReservations = carReservationService.getCarReservationsByUserId(userId);
                if (carReservations.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User has no car reservations");
                }
                resultMap.put("Car reservations: ", carReservations);
                return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(resultMap);
            case "restaurant":
                List<RestaurantReservation> restaurantReservations = restaurantReservationService.getRestaurantReservationsByUserId(userId);
                if (restaurantReservations.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User has no restaurant reservations");
                }
                resultMap.put("Restaurant reservations: ", restaurantReservations);
                return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(resultMap);
            case "bar":
                List<BarReservation> barReservations = barReservationService.getBarReservationsByUserId(userId);
                if (barReservations.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User has no restaurant reservations");
                }
                resultMap.put("Bar reservations: ", barReservations);
                return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(resultMap);
            case "all":
                List<HotelRoomReservation> allRoomReservations = roomReservationService.getRoomReservationsByUserId(userId);
                List<CarReservation> allCarReservations = carReservationService.getCarReservationsByUserId(userId);
                List<RestaurantReservation> allRestaurantReservations = restaurantReservationService.getRestaurantReservationsByUserId(userId);
                List<BarReservation> allBarReservations = barReservationService.getBarReservationsByUserId(userId);
                resultMap.put("Hotel reservations: ", allRoomReservations);
                resultMap.put("Car reservations: ", allCarReservations);
                resultMap.put("Restaurant reservations: ", allRestaurantReservations);
                resultMap.put("Bar Reservations: ", allBarReservations);
                boolean isEmpty = resultMap.values().stream()
                        .allMatch(CollectionUtils::isEmpty);
                if (isEmpty) {
                    return ResponseEntity.status(HttpStatusCode.valueOf(204)).body("User has no reservations");
                }
                return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(resultMap);
            default:
                return ResponseEntity.status(HttpStatusCode.valueOf(400)).build();
        }
    }
}
