package com.moonlight.repository.user;

import com.moonlight.model.user.User;
import com.moonlight.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserRole(UserRole userRoleId);

    Optional<User> findByEmailAddress(String email);

    Boolean existsByEmailAddress(String email);
}
