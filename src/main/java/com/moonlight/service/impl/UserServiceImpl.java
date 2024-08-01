package com.moonlight.service.impl;

import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.LoginRequest;
import com.moonlight.dto.UserRequest;
import com.moonlight.model.User;
import com.moonlight.model.UserRole;
import com.moonlight.repository.UserRepository;
import com.moonlight.service.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRequest userRequest) {
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
        user.setUserRole(role);

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RecordNotFoundException(String.format("User with id %s not exist", id)));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmailAddress(email).orElseThrow(() ->
                new RecordNotFoundException(String.format("User with email %s not exist", email)));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
        userRepository.deleteById(id);
    }

    @Override
    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByEmailAddress(loginRequest.getEMail()).orElseThrow(() -> new RecordNotFoundException("User not found"));
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return "STRING GENERATED FROM JWT TOKEN SHOULD BE HERE";
            //Will be appended
            //todo add initial logic after security is properly handled
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
