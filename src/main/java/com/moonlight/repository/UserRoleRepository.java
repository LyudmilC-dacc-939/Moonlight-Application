package com.moonlight.repository;

import com.moonlight.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {

    UserRole findByUserRole(String userRole);
}
