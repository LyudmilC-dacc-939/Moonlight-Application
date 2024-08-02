package com.moonlight.repository;

import com.moonlight.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    Optional<UserRole> findByUserRole(String userRole);

    UserRole getByUserRoleIs(String userRole);
}
