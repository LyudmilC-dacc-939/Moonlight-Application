package com.moonlight.asset;

import com.moonlight.exception.UserAlreadyExistsException;
import com.moonlight.model.User;
import com.moonlight.model.UserRole;
import com.moonlight.repository.UserRepository;
import com.moonlight.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Instant;
import java.util.Optional;

@Component
public class AdminAsset implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Override
    public void run(String... args) throws Exception {
        UserRole userRole = userRoleRepository.findByUserRole("ROLE_ADMIN");
        Optional<User> user = userRepository.findByUserRole(userRole);
        if (user.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with role admin already exists"));
        }   else {
            String filePath = "Text file path here";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                User newAdmin = new User();
                newAdmin.setFirstName(data[0].trim());
                newAdmin.setLastName(data[1].trim());
                newAdmin.setEmailAddress(data[2].trim());
                newAdmin.setPhoneNumber(data[3].trim());
                newAdmin.setPassword(data[4].trim());
                newAdmin.setDateCreated(Instant.now());
                newAdmin.setUserRole(userRole);

                userRepository.save(newAdmin);
            }
        }
    }
}
