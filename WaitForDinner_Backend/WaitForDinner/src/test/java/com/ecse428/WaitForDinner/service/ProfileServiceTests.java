package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.ProfileUpdateRequestDto;
import com.ecse428.WaitForDinner.dto.ProfileUpdateResponseDto;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profileService = new ProfileService(userRepository, passwordEncoder);
    }

    @Test
    void testUpdateProfileSuccess() {
        User user = new User("Old Name", "old@example.com", "pass", new Date(), null, 1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        ProfileUpdateRequestDto req = new ProfileUpdateRequestDto();
        req.setEmail("new@example.com");
        req.setName("New Name");

        ProfileUpdateResponseDto res = profileService.UpdateProfile(req, 1);

        assertEquals("new@example.com", res.getEmail());
        assertEquals("New Name", res.getName());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("New Name", user.getName());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateProfileUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ProfileUpdateRequestDto req = new ProfileUpdateRequestDto();
        req.setEmail("new@example.com");
        req.setName("New Name");

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> profileService.UpdateProfile(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
