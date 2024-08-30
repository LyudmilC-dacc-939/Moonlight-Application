package com.moonlight.repository.user;

import com.moonlight.model.user.User;
import com.moonlight.model.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    private UserRole userRole;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        userRole = new UserRole();
        userRole.setUserRole("Admin");
        userRole = userRoleRepository.save(userRole);
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmailAddress("test@test.com");
        user.setPassword("password123");
        user.setPhoneNumber("123456789");
        user.setUserRole(userRole);
    }

    @Test
    void findByUserRole_Found() {
        userRepository.save(user);

        User existingUser = userRepository.findByUserRole(user.getUserRole()).get();

        assertEquals("John", existingUser.getFirstName());
        assertEquals("Doe", existingUser.getLastName());
    }

    @Test
    void findByUserRole_NotFound() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userRepository.findByUserRole(user.getUserRole()).get());
        assertEquals(exception.getMessage(), "No value present");
    }

    @Test
    void findByEmailAddress_Found() {
        userRepository.save(user);
        User existingUser = userRepository.findByEmailAddress(user.getEmailAddress()).get();

        assertEquals("John", existingUser.getFirstName());
        assertEquals("Doe", existingUser.getLastName());
    }

    @Test
    void findByEmailAddress_NotFound() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userRepository.findByEmailAddress(user.getEmailAddress()).get());
        assertEquals(exception.getMessage(), "No value present");
    }

    @Test
    void existsByEmailAddress_True() {
        userRepository.save(user);

        boolean existingUser = userRepository.existsByEmailAddress(user.getEmailAddress());

        assertTrue(existingUser);
    }

    @Test
    void existsByEmailAddress_False() {
        userRepository.save(user);

        boolean existingUser = userRepository.existsByEmailAddress("email123@abv.bg");

        assertFalse(existingUser);
    }
}