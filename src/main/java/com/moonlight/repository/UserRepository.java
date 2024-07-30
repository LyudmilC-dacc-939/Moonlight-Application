package com.moonlight.repository;

import com.moonlight.model.User;
import com.moonlight.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserRole(UserRole userRoleId);

    Optional<User> findByEmailAddress(String email);

    Boolean existsByEmailAddress(String email);

    Optional<User> findByUserRole(Optional<UserRole> userRoleId);
}
