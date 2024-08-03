package com.moonlight.asset;

import com.moonlight.model.UserRole;
import com.moonlight.repository.UserRoleRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(1)
@Getter
@Setter
public class UserRoleAsset implements CommandLineRunner {

    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String CLIENT_ROLE = "ROLE_CLIENT";

    private UserRoleRepository userRoleRepository;

    public UserRoleAsset(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        //Find or create the admin role
        Optional<UserRole> adminUserRole = userRoleRepository.findByUserRole(ADMIN_ROLE);
        if (adminUserRole.isEmpty()) {
            UserRole adminRoleCreation = new UserRole();
            adminRoleCreation.setUserRole(ADMIN_ROLE);
            userRoleRepository.save(adminRoleCreation);
        }

        //Find or create the user role
        Optional<UserRole> userUserRole = userRoleRepository.findByUserRole(CLIENT_ROLE);
        if (userUserRole.isEmpty()) {
            UserRole userUserRoleCreation = new UserRole();
            userUserRoleCreation.setUserRole(CLIENT_ROLE);
            userRoleRepository.save(userUserRoleCreation);
        }
    }
}