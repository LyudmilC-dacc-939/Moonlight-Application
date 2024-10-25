package com.moonlight.asset;

import com.moonlight.model.user.User;
import com.moonlight.model.user.UserRole;
import com.moonlight.repository.user.UserRepository;
import com.moonlight.repository.user.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Optional;

@Component
@Order(2)
public class AdminAsset implements CommandLineRunner {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Autowire PasswordEncoder

    @Override
    public void run(String... args) throws Exception {
        // Read admin details from the file
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("assetDocs/admin-details.txt")) {
            if (resourceStream == null) {
                throw new FileNotFoundException("Resource file not found: admin-details.txt");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length % 5 != 0) {
                    // Handle the error appropriately if the data does not have exactly 5 parts
                    throw new IllegalArgumentException("Incorrect data format in 'admin-details.txt' the data array length is not a multiple of 5");
                }

                // Every 5 data indexes are a new user, the admin-details.txt must 100% have its data separated by 5's
                for (int i = 0; i < data.length; i += 5) {
                    User newAdmin = new User();
                    newAdmin.setFirstName(data[i].trim());
                    newAdmin.setLastName(data[i + 1].trim());
                    newAdmin.setEmailAddress(data[i + 2].trim());
                    newAdmin.setPhoneNumber(data[i + 3].trim());
                    newAdmin.setPassword(passwordEncoder.encode(data[i + 4].trim())); // Encode the password
                    newAdmin.setDateCreated(Instant.now());

                    // Fetch the UserRole for ROLE_ADMIN
                    Optional<UserRole> adminRole = userRoleRepository.findByUserRole("ROLE_ADMIN");
                    if (adminRole.isPresent()) {
                        newAdmin.setUserRole(adminRole.get());
                    } else {
                        throw new IllegalStateException("UserRole ROLE_ADMIN not found in the database");
                    }

                    // Checks if user with that email does not exist, it will then save it to the DB, otherwise it will do nothing.
                    if (userRepository.findByEmailAddress(newAdmin.getEmailAddress()).isEmpty()) {
                        userRepository.save(newAdmin);
                    }
                }

            }
        }
    }
}