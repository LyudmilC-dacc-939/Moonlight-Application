package com.moonlight.service.impl;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.LoginRequest;
import com.moonlight.dto.UserRequest;
import com.moonlight.model.User;
import com.moonlight.model.UserRole;
import com.moonlight.repository.UserRepository;
import com.moonlight.repository.UserRoleRepository;
import com.moonlight.security.JwtService;
import com.moonlight.service.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CurrentUserImpl currentUserImpl;

    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, @Lazy CurrentUserImpl currentUserImpl, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.currentUserImpl = currentUserImpl;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @SneakyThrows
    public void registerUser(UserRequest userRequest) {
        if (userRepository.findByEmailAddress(userRequest.getEmail()).isPresent()) {
            throw new ConstraintViolationException("Email is already taken", null);
        }
        User user = new User();
        UserRole role = new UserRole();
        // Temporary setting the regular use to "ROLE_CLIENT", it's not decided what it will be called yet, and we have not
        // created the ROLES table/id/names yet - TBD
        role.setUserRole("ROLE_CLIENT");
        user.setEmailAddress(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setDateCreated(Instant.now());
        user.setUserRole(userRoleRepository.findByUserRole("ROLE_CLIENT").get());

        if (userRequest.getIsAgreedEULA() && userRequest.getIsAgreedGDPR()) {
            userRepository.save(user);
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
}
