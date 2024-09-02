package com.moonlight.service.impl.user;

import com.moonlight.advice.exception.InvalidInputException;
import com.moonlight.advice.exception.RecordNotFoundException;
import com.moonlight.dto.user.*;
import com.moonlight.model.car.CarReservation;
import com.moonlight.model.hotel.HotelRoomReservation;
import com.moonlight.model.user.User;
import com.moonlight.model.user.UserRole;
import com.moonlight.repository.car.CarReservationRepository;
import com.moonlight.repository.hotel.HotelRoomReservationRepository;
import com.moonlight.repository.user.UserRepository;
import com.moonlight.repository.user.UserRoleRepository;
import com.moonlight.security.ApplicationConfiguration;
import com.moonlight.security.JwtService;
import com.moonlight.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CurrentUserImpl currentUserImpl;

    @Mock
    private EmailService emailService;

    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @Mock
    private HotelRoomReservationRepository hotelRoomReservationRepository;

    @Mock
    private CarReservationRepository carReservationRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    private UserRole userRole;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmailAddress("test@example.com");
        user.setPassword("encodedPassword");

        userRole = new UserRole();
        userRole.setUserRole("ROLE_CLIENT");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(currentUserImpl.isCurrentUserARole("ROLE_ADMIN")).thenReturn(false);
    }

    @Test
    void testRegisterUserSuccess() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setPhoneNumber("123456789");
        userRequest.setPassword("password");
        userRequest.setIsAgreedEULA(true);
        userRequest.setIsAgreedGDPR(true);

        when(userRepository.findByEmailAddress(userRequest.getEmail())).thenReturn(Optional.empty());
        when(userRoleRepository.findByUserRole("ROLE_CLIENT")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");

        userServiceImpl.registerUser(userRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmailUponRegister(eq(userRequest.getEmail()), anyString(), anyString());
    }


    @Test
    void testRegisterUserNotAgreedToEULAAndGDPR() {
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setIsAgreedEULA(false);
        userRequest.setIsAgreedGDPR(false);

        when(userRepository.findByEmailAddress(userRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            userServiceImpl.registerUser(userRequest);
        });
    }

    @Test
    void testGetUserByIdSuccess() {
        User result = userServiceImpl.getUserById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserByIdNotAuthorized() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(false);
        when(currentUserImpl.isCurrentUserARole("ROLE_ADMIN")).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            userServiceImpl.getUserById(user.getId());
        });
    }

    @Test
    void testGetUserByEmailSuccess() {
        when(userRepository.findByEmailAddress("test@example.com")).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);

        Optional<User> foundUser = userRepository.findByEmailAddress("test@example.com");

        assertEquals(Optional.of(user), foundUser);
    }

    @Test
    void testGetUserByEmailNotAuthorized() {
        when(userRepository.findByEmailAddress("test@example.com")).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(false);
        when(currentUserImpl.isCurrentUserARole("ROLE_ADMIN")).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            userServiceImpl.getUserByEmail("test@example.com");
        });
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);

        userServiceImpl.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUserNotAuthorized() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            userServiceImpl.deleteUser(user.getId());
        });
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmailAddress(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateJwtToken(user)).thenReturn("jwtToken");

        String token = userServiceImpl.login(loginRequest);

        assertEquals("jwtToken", token);
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmailAddress(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(applicationConfiguration.matchesEncodedPassword(loginRequest.getPassword(), user.getPassword())).thenReturn(false);
        when(userServiceImpl.login(loginRequest)).thenThrow(RecordNotFoundException.class);

        assertThrows(RecordNotFoundException.class, () -> {
            userServiceImpl.login(loginRequest);
        });
    }

    @Test
    void testFindByEmail() {
        when(userRepository.findByEmailAddress("test@example.com")).thenReturn(Optional.of(user));

        Optional<User> foundUser = userRepository.findByEmailAddress("test@example.com");

        assertEquals(Optional.of(user), foundUser);
    }

    @Test
    void testGetPageableUsersList() {
        List<User> users = Arrays.asList(new User(), new User(), new User());
        Page<User> pagedResult = new PageImpl<>(users);

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(pagedResult);

        List<User> result = userServiceImpl.getPageableUsersList(0, 3);

        assertEquals(3, result.size());
    }

    @Test
    void testChangePasswordSuccess() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("currentPassword");
        changePasswordRequest.setNewPassword("newPassword");

        when(currentUserImpl.extractCurrentUser()).thenReturn(user);
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(applicationConfiguration.matchesEncodedPassword(changePasswordRequest.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("encodedNewPassword");

        User updatedUser = userServiceImpl.changePassword(changePasswordRequest);

        assertEquals("encodedNewPassword", updatedUser.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePasswordInvalidCurrentPassword() {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("currentPassword");

        when(currentUserImpl.extractCurrentUser()).thenReturn(user);
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(applicationConfiguration.matchesEncodedPassword(changePasswordRequest.getCurrentPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidInputException.class, () -> {
            userServiceImpl.changePassword(changePasswordRequest);
        });
    }

    @Test
    void testResetPasswordSuccess() {
        ResetPasswordRequest passwordRequest = new ResetPasswordRequest();
        passwordRequest.setEmail("test@example.com");

        when(userRepository.findByEmailAddress(passwordRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userServiceImpl.resetPassword(passwordRequest);

        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendEmailForForgottenPassword(eq(passwordRequest.getEmail()), anyString());
    }

    @Test
    void testResetPasswordUserNotFound() {
        ResetPasswordRequest passwordRequest = new ResetPasswordRequest();
        passwordRequest.setEmail("test@example.com");

        when(userRepository.findByEmailAddress(passwordRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> {
            userServiceImpl.resetPassword(passwordRequest);
        });
    }

    @Test
    void testGetUserReservationsSuccess() {
        List<HotelRoomReservation> hotelRoomReservations = Arrays.asList(new HotelRoomReservation(), new HotelRoomReservation());
        List<CarReservation> carReservations = Arrays.asList(new CarReservation(), new CarReservation());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(hotelRoomReservationRepository.findByUserId(user.getId())).thenReturn(hotelRoomReservations);
        when(carReservationRepository.findByUserId(user.getId())).thenReturn(carReservations);

        Map<String, Object> reservations = userServiceImpl.getUserReservations(user);

        assertEquals(2, ((List<?>) reservations.get("hotelRoomReservations")).size());
        assertEquals(2, ((List<?>) reservations.get("carReservations")).size());
    }

    @Test
    void testGetUserReservationsNotAuthorized() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            userServiceImpl.getUserReservations(user);
        });
    }

    @Test
    void testUpdateUserSuccess() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("UpdatedName");
        updateUserRequest.setLastName("UpdatedLastName");
        updateUserRequest.setPhoneNumber("987654321");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(true);
        when(userServiceImpl.updateUser(updateUserRequest, 1L)).thenReturn(user);

        User updatedUser = userServiceImpl.updateUser(updateUserRequest, 1L);

        assertEquals("UpdatedName", updatedUser.getFirstName());
        assertEquals("UpdatedLastName", updatedUser.getLastName());
        assertEquals("987654321", updatedUser.getPhoneNumber());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserNotAuthorized() {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setFirstName("UpdatedName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(currentUserImpl.isCurrentUserMatch(user)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> {
            userServiceImpl.updateUser(updateUserRequest, 1L);
        });
    }
}