package com.moonlight.service.impl;

import com.moonlight.advice.exception.RecordNotFoundException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
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
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));
        userRepository.deleteById(id);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RecordNotFoundException("User not found"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }
}
