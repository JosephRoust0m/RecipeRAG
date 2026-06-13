package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.PreferenceTypeRequestDto;
import com.ecse428.WaitForDinner.dto.PreferenceTypeResponseDto;
import com.ecse428.WaitForDinner.dto.PreferenceTypesResponseDto;
import com.ecse428.WaitForDinner.dto.PreferenceUserRequestDto;
import com.ecse428.WaitForDinner.dto.PreferenceUserResponseDto;
import com.ecse428.WaitForDinner.model.PreferenceType;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.PreferenceRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreferenceServiceTests {

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PreferenceService preferenceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        preferenceService = new PreferenceService(preferenceRepository, userRepository);
    }

    @Test
    void testGetAllPreferenceTypes() {
        when(preferenceRepository.findAll()).thenReturn(List.of(
            new PreferenceType("Vegan", 1),
            new PreferenceType("Gluten Free", 2)
        ));

        PreferenceTypesResponseDto res = preferenceService.getAllPreferenceTypes();

        assertNotNull(res);
        verify(preferenceRepository).findAll();
    }

    @Test
    void testGetPreferenceTypeByIdSuccess() {
        PreferenceType preference = new PreferenceType("Vegan", 1);
        when(preferenceRepository.findById(1)).thenReturn(Optional.of(preference));

        PreferenceTypeResponseDto res = preferenceService.getPreferenceTypeById(1);

        assertEquals(1, res.getId());
        assertEquals("Vegan", res.getName());
    }

    @Test
    void testGetPreferenceTypeByIdNotFound() {
        when(preferenceRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.getPreferenceTypeById(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetPreferenceTypeByNameSuccess() {
        PreferenceType preference = new PreferenceType("Vegan", 1);
        when(preferenceRepository.findByName("Vegan")).thenReturn(Optional.of(preference));

        PreferenceTypeResponseDto res = preferenceService.getPreferenceTypeByName("Vegan");

        assertEquals(1, res.getId());
        assertEquals("Vegan", res.getName());
    }

    @Test
    void testGetPreferenceTypeByNameNotFound() {
        when(preferenceRepository.findByName("Vegan")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.getPreferenceTypeByName("Vegan")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreatePreferenceSuccess() {
        PreferenceTypeRequestDto req = new PreferenceTypeRequestDto();
        req.setName("Vegan");

        when(preferenceRepository.findByName("Vegan")).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(PreferenceType.class)))
            .thenReturn(new PreferenceType("Vegan", 1));

        PreferenceTypeResponseDto res = preferenceService.createPreference(req);

        assertEquals(1, res.getId());
        assertEquals("Vegan", res.getName());
        verify(preferenceRepository).save(any(PreferenceType.class));
    }

    @Test
    void testCreatePreferenceConflict() {
        PreferenceTypeRequestDto req = new PreferenceTypeRequestDto();
        req.setName("Vegan");

        when(preferenceRepository.findByName("Vegan"))
            .thenReturn(Optional.of(new PreferenceType("Vegan", 1)));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.createPreference(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testUpdatePreferenceSuccess() {
        PreferenceTypeRequestDto req = new PreferenceTypeRequestDto();
        req.setId(1);
        req.setName("Vegetarian");

        PreferenceType existing = new PreferenceType("Vegan", 1);
        when(preferenceRepository.findById(1)).thenReturn(Optional.of(existing));
        when(preferenceRepository.findByName("Vegetarian")).thenReturn(Optional.empty());

        PreferenceTypeResponseDto res = preferenceService.updatePreference(req);

        assertEquals(1, res.getId());
        assertEquals("Vegetarian", res.getName());
        verify(preferenceRepository).save(existing);
    }

    @Test
    void testUpdatePreferenceNotFound() {
        PreferenceTypeRequestDto req = new PreferenceTypeRequestDto();
        req.setId(1);
        req.setName("Vegetarian");

        when(preferenceRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.updatePreference(req)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testUpdatePreferenceConflict() {
        PreferenceTypeRequestDto req = new PreferenceTypeRequestDto();
        req.setId(1);
        req.setName("Vegetarian");

        when(preferenceRepository.findById(1)).thenReturn(Optional.of(new PreferenceType("Vegan", 1)));
        when(preferenceRepository.findByName("Vegetarian"))
            .thenReturn(Optional.of(new PreferenceType("Vegetarian", 2)));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.updatePreference(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(preferenceRepository, never()).save(any(PreferenceType.class));
    }

    @Test
    void testDeletePreferenceSuccess() {
        when(preferenceRepository.findById(1)).thenReturn(Optional.of(new PreferenceType("Vegan", 1)));

        preferenceService.deletePreference(1);

        verify(preferenceRepository).deleteById(1);
    }

    @Test
    void testDeletePreferenceNotFound() {
        when(preferenceRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.deletePreference(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(preferenceRepository, never()).deleteById(1);
    }

    @Test
    void testGetUserPreferenceTypesSuccess() {
        User user = buildUser(1);
        PreferenceType preference = new PreferenceType("Vegan", 1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(preferenceRepository.findAllByUsers(user)).thenReturn(List.of(preference));

        PreferenceUserResponseDto res = preferenceService.getUserPreferenceTypes(1);

        assertEquals(List.of("Vegan"), res.getPreferenceNames());
        assertEquals(List.of(1), res.getPreferenceIds());
    }

    @Test
    void testGetUserPreferenceTypesUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.getUserPreferenceTypes(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testAddUserPreferenceTypeSuccess() {
        User user = buildUser(1);
        PreferenceType preference = new PreferenceType("Vegan", 1);

        PreferenceUserRequestDto req = new PreferenceUserRequestDto();
        req.setPreferenceIds(List.of(1));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(preferenceRepository.findById(1)).thenReturn(Optional.of(preference));
        when(preferenceRepository.findAllByUsers(user)).thenReturn(List.of(preference));

        PreferenceUserResponseDto res = preferenceService.addUserPreferenceType(req, 1);

        assertEquals(List.of("Vegan"), res.getPreferenceNames());
        assertEquals(List.of(1), res.getPreferenceIds());
        verify(userRepository).save(user);
    }

    @Test
    void testAddUserPreferenceTypePreferenceNotFound() {
        User user = buildUser(1);

        PreferenceUserRequestDto req = new PreferenceUserRequestDto();
        req.setPreferenceIds(List.of(1));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(preferenceRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.addUserPreferenceType(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testRemoveUserPreferenceTypeSuccess() {
        User user = buildUser(1);
        PreferenceType preference = new PreferenceType("Vegan", 1);
        user.addPreferenceType(preference);

        PreferenceUserRequestDto req = new PreferenceUserRequestDto();
        req.setPreferenceIds(List.of(1));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(preferenceRepository.existsById(1)).thenReturn(true);
        when(preferenceRepository.findAllByUsers(user)).thenReturn(List.of());

        PreferenceUserResponseDto res = preferenceService.removeUserPreferenceType(req, 1);

        assertEquals(List.of(), res.getPreferenceNames());
        assertEquals(List.of(), res.getPreferenceIds());
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveUserPreferenceTypePreferenceNotFound() {
        User user = buildUser(1);

        PreferenceUserRequestDto req = new PreferenceUserRequestDto();
        req.setPreferenceIds(List.of(1));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(preferenceRepository.existsById(1)).thenReturn(false);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> preferenceService.removeUserPreferenceType(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    private User buildUser(int userId) {
        return new User("User", "user" + userId + "@example.com", "pass", new Date(), null, userId);
    }
}
