package com.moonlight.service.impl.user;

import com.moonlight.advice.exception.IllegalAccessException;
import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.user.*;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.user.User;
import com.moonlight.model.user.UserRole;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.user.UserRepository;
import com.moonlight.repository.user.UserRoleRepository;
import com.moonlight.security.ApplicationConfiguration;
import com.moonlight.security.JwtService;
import com.moonlight.service.EmailService;
import com.moonlight.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CurrentUserImpl currentUserImpl;
    private final EmailService emailService;
    private final UserRoleRepository userRoleRepository;
    private final ApplicationConfiguration applicationConfiguration;
    private final HotelRoomReservationRepository hotelRoomReservationRepository;
    private final CarReservationRepository carReservationRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           @Lazy CurrentUserImpl currentUserImpl,
                           EmailService emailService,
                           UserRoleRepository userRoleRepository,
                           ApplicationConfiguration applicationConfiguration, HotelRoomReservationRepository hotelRoomReservationRepository, CarReservationRepository carReservationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.currentUserImpl = currentUserImpl;
        this.emailService = emailService;
        this.userRoleRepository = userRoleRepository;
        this.applicationConfiguration = applicationConfiguration;
        this.hotelRoomReservationRepository = hotelRoomReservationRepository;
        this.carReservationRepository = carReservationRepository;
    }

    @Override
    @SneakyThrows
    public void registerUser(UserRequest userRequest) {
        if (userRepository.findByEmailAddress(userRequest.getEmail()).isPresent()) {
            throw new ConstraintViolationException("Email is already taken", null);
        }
        User user = new User();
        Optional<UserRole> role = userRoleRepository.findByUserRole("ROLE_CLIENT");
        if (role.isPresent()) {
            user.setUserRole(role.get());
        } else {
            throw new IllegalStateException("UserRole ROLE_CLIENT not found in the database");
        }
        user.setEmailAddress(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setDateCreated(Instant.now());

        if (userRequest.getIsAgreedEULA() && userRequest.getIsAgreedGDPR()) {
            userRepository.save(user);

            String subject = "Welcome to Moonlight";
            String text = "Dear " +
                    userRequest.getLastName() + ",\n\nYour registration was successful!\nUsername: " +
                    userRequest.getEmail() + "\nPassword: " +
                    userRequest.getPassword() + "\n\nBest regards, \nYour service Team";
            emailService.sendEmailUponRegister(userRequest.getEmail(), subject, text);
        } else {
            throw new InvalidInputException("You must agree to our EULA and to the GDPR to register");
        }
    }

    @Override
    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RecordNotFoundException(String.format("User with id %s not exist", id)));
        boolean canGetUserById = currentUserImpl.isCurrentUserMatch(user);
        canGetUserById |= currentUserImpl.isCurrentUserARole("ROLE_ADMIN");
        if (!canGetUserById) {
            throw new RecordNotFoundException("This user is not authorize to proceed this operation");
        } else {
            return user;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmailAddress(email).orElseThrow(() ->
                new RecordNotFoundException(String.format("User with email %s not exist", email)));
        boolean canGetUserByEmail = currentUserImpl.isCurrentUserMatch(user);
        canGetUserByEmail |= currentUserImpl.isCurrentUserARole("ROLE_ADMIN");
        if (!canGetUserByEmail) {
            throw new RecordNotFoundException("This user is not authorize to proceed this operation");
        } else {
            return user;
        }
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
        boolean canDeleteUser = currentUserImpl.isCurrentUserMatch(user);
        canDeleteUser |= currentUserImpl.isCurrentUserARole("ROLE_ADMIN");
        if (!canDeleteUser) {
            throw new RecordNotFoundException("This user is not authorize to proceed this operation");
        } else {
            userRepository.deleteById(id);
        }
    }

    @Override
    public String login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        var user = userRepository.findByEmailAddress(loginRequest.getEmail())
                .orElseThrow(() -> new RecordNotFoundException("User not found or wrong password"));
        return jwtService.generateJwtToken(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmailAddress(email).orElseThrow(() -> new RecordNotFoundException("No results found"));
    }

    @Override
    public java.util.List<User> getPeageableUsersList(int skip, int take) {
        Pageable pageable = PageRequest.of(skip, take);
        Page<User> pagedResult = userRepository.findAll(pageable);
        return pagedResult.toList();
    }

    @Override
    @SneakyThrows
    public User changePassword(ChangePasswordRequest changePasswordRequest) {
        User currentUser = currentUserImpl.extractCurrentUser();
        if (!currentUserImpl.isCurrentUserMatch(currentUser)) {
            throw new IllegalAccessException("Unauthorized Access!");
        }
        if (applicationConfiguration.matchesEncodedPassword(changePasswordRequest.getCurrentPassword(), currentUser.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(currentUser);
            return currentUser;
        } else {
            throw new InvalidInputException("Current password does not match");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest passwordRequest) {
        User userWithForgottenPassword = userRepository.findByEmailAddress(passwordRequest.getEmail()).orElseThrow(() ->
                new RecordNotFoundException("Provided email is invalid"));
        String generatedPassword = "";
        while (!generatedPassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$")) {
            generatedPassword = RandomStringUtils.randomAscii(25);
        }
        userWithForgottenPassword.setPassword(passwordEncoder.encode(generatedPassword));
        userRepository.save(userWithForgottenPassword);
        emailService.sendEmailForForgottenPassword(passwordRequest.getEmail(), generatedPassword);
    }

    @Override
    public Map<String, Object> getUserReservations(User user) {
        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
        boolean canGetUserById = currentUserImpl.isCurrentUserMatch(foundUser);
        if (!canGetUserById) {
            throw new RecordNotFoundException("This user is not authorize to proceed this operation");
        } else {
            List<HotelRoomReservation> hotelRoomReservations = hotelRoomReservationRepository.findByUserId(foundUser.getId());
            List<CarReservation> carReservations = carReservationRepository.findByUserId(foundUser.getId());
            Map<String, Object> reservations = new HashMap<>();
            if (!hotelRoomReservations.isEmpty()) {
                reservations.put("hotelRoomReservations", hotelRoomReservations);
            } else {
                reservations.put("hotelRoomReservations", "No hotel room reservations found");
            }

            if (!carReservations.isEmpty()) {
                reservations.put("carReservations", carReservations);
            } else {
                reservations.put("carReservations", "No car reservations found");
            }
            return reservations;
        }
    }

    @Override
    public User updateUser(UpdateUserRequest updateUserRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RecordNotFoundException(String.format("User with id %S not found", userId)));
        boolean canUpdateUser = currentUserImpl.isCurrentUserMatch(user);
        if (!canUpdateUser) {
            throw new RecordNotFoundException("This user is not authorize to proceed this operation");
        }
        if (updateUserRequest.getFirstName() != null) {
            user.setFirstName(updateUserRequest.getFirstName());
        }
        if (updateUserRequest.getLastName() != null) {
            user.setLastName(updateUserRequest.getLastName());
        }
        if (updateUserRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }
        return userRepository.save(user);
    }
}
