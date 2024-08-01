package com.moonlight.asset;

import com.moonlight.model.UserRole;
import com.moonlight.repository.UserRoleRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Getter
@Setter
public class UserRoleAsset implements CommandLineRunner {

    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String USER_ROLE = "ROLE_USER";

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) throws Exception {

        Optional<UserRole> adminUserRole = userRoleRepository.findByUserRole(ADMIN_ROLE);
        if (adminUserRole.isEmpty()) {
            UserRole adminRoleCreation = new UserRole();
            adminRoleCreation.setUserRole(ADMIN_ROLE);
            userRoleRepository.save(adminRoleCreation);
        }

        //Find or create the user role
        Optional<UserRole> userUserRole = userRoleRepository.findByUserRole(USER_ROLE);
        if (userUserRole.isEmpty()) {
            UserRole userUserRoleCreation = new UserRole();
            userUserRoleCreation.setUserRole(USER_ROLE);
            userRoleRepository.save(userUserRoleCreation);
        }
    }
}