package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.LoginRequestDto;
import com.ecse428.WaitForDinner.dto.LoginResponseDto;
import com.ecse428.WaitForDinner.dto.SignupRequestDto;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpSession;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private HttpSession session;

    @InjectMocks
    private UserService userService;

    private static final String SIGNUP_EMAIL = "signup.test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, mailSender);
    }
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void testSignupSuccess() {
        SignupRequestDto request = new SignupRequestDto(
                "New User",
                SIGNUP_EMAIL,
                "StrongPass123!"
        );

        LoginResponseDto response = userService.signup(request, session);

        assertNotNull(response);
        assertEquals("New User", response.getName());
        assertEquals(SIGNUP_EMAIL, response.getEmail());

    }

    @Test
    void testSignupFailsWhenEmailAlreadyExists() {
        User existing = new User(
                "Existing User",
                SIGNUP_EMAIL,
                passwordEncoder.encode("Password1!"),
                new Date(),
                null,
                0
        );
        userRepository.save(existing);

        SignupRequestDto request = new SignupRequestDto(
                "Another User",
                SIGNUP_EMAIL,
                "StrongPass123!"
        );

        when(userRepository.findByEmail(SIGNUP_EMAIL)).thenReturn(Optional.of(existing));
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.signup(request, session)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testSignupFailsWhenEmailMissing() {
        SignupRequestDto request = new SignupRequestDto(
                "New User",
                "",
                "StrongPass123!"
        );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.signup(request, session)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testSignupFailsWhenPasswordMissing() {
        SignupRequestDto request = new SignupRequestDto(
                "New User",
                SIGNUP_EMAIL,
                ""
        );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.signup(request, session)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testSignupFailsWhenNameMissing() {
        SignupRequestDto request = new SignupRequestDto(
                "",
                SIGNUP_EMAIL,
                "StrongPass123!"
        );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.signup(request, session)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testSignupFailsWhenEmailFormatIsWrong() {
        SignupRequestDto request = new SignupRequestDto(
                "",
                "hello_",
                "StrongPass123!"
        );

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.signup(request, session)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Email format is invalid", ex.getReason());
    }


    @Test
    void testLoginSuccess() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "password@123");

        User user = new User();
        user.setUserId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password@123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password@123", "password@123")).thenReturn(true);

        LoginResponseDto response = userService.login(request, session);

        assertEquals("test@example.com", response.getEmail());
    }


    @Test
    void testLoginInvalidEmail() {
        LoginRequestDto request = new LoginRequestDto(null, "password");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.login(request, session));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.login(request, session));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "password@123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.login(request, session));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testLoginWrongPassword() {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "password@123");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("hashed@123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "hashed")).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.login(request, session));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testLoginWithEmptyFields() {

        LoginRequestDto emptyEmail = new LoginRequestDto("", "password");
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class, () -> userService.login(emptyEmail, session));
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatusCode());


        LoginRequestDto emptyPassword = new LoginRequestDto("test@example.com", "");
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class, () -> userService.login(emptyPassword, session));
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatusCode());
    }

    @Test
    void testLogoutSuccess() {
        when(session.getAttribute(UserService.SESSION_USER_ID)).thenReturn(1L);
        doNothing().when(session).invalidate();
        userService.logout(session);
        verify(session).invalidate();
    }

    @Test
    void testLogoutNotLoggedIn() {
        when(session.getAttribute(UserService.SESSION_USER_ID)).thenReturn(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.logout(session));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testForgotPasswordSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        userService.forgotPassword("test@example.com");
        verify(mailSender).send(captor.capture());
    }

    @Test
    void testForgotPasswordUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.forgotPassword("notfound@example.com"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testResetPasswordSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("oldHashed@123");
        Date token = new Date(System.currentTimeMillis());
        user.setToken(token);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("newPassword@123", "oldHashed@123")).thenReturn(false);
        userService.resetPassword("test@example.com", "newPassword@123");
        verify(userRepository).save(user);
    }

    @Test
    void testResetPasswordUserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.resetPassword("notfound@example.com", "newPassword@123"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testResetPasswordTokenMissing() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setToken(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.resetPassword("test@example.com", "newPassword@123"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testResetPasswordTokenExpired() {
        User user = new User();
        user.setEmail("test@example.com");
        Date token = Date.from(LocalDateTime.now().minusMinutes(20).atZone(ZoneId.systemDefault()).toInstant());
        user.setToken(token);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.resetPassword("test@example.com", "newPassword@123"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void testResetPasswordSamePasswordFailure() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("oldHashed@123");
        Date token = new Date(System.currentTimeMillis());
        user.setToken(token);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword@123", "oldHashed@123")).thenReturn(true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.resetPassword("test@example.com", "oldPassword@123"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

        @Test
    void testDeleteAccountSuccess() {
        int userId = 123;
        User user = new User("Test User", "test@delete.com", "pass", new Date(), null, userId);
        when(session.getAttribute(UserService.SESSION_USER_ID)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);
        doNothing().when(session).invalidate();

        assertDoesNotThrow(() -> userService.deleteAccount(session));
        verify(userRepository).delete(user);
        verify(session).invalidate();
    }

    @Test
    void testDeleteAccountUnauthenticated() {
        when(session.getAttribute(UserService.SESSION_USER_ID)).thenReturn(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.deleteAccount(session));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

}
