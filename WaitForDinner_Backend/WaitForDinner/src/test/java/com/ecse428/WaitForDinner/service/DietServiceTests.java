package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.DietTypeRequestDto;
import com.ecse428.WaitForDinner.dto.DietTypeResponseDto;
import com.ecse428.WaitForDinner.dto.DietTypesResponseDto;
import com.ecse428.WaitForDinner.dto.DietUserRequestDto;
import com.ecse428.WaitForDinner.dto.DietUserResponseDto;
import com.ecse428.WaitForDinner.model.DietType;
import com.ecse428.WaitForDinner.model.PreferenceType;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.DietRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DietServiceTests {

    @Mock
    private DietRepository dietRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DietService dietService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dietService = new DietService(dietRepository, preferenceRepository, userRepository);
    }

    @Test
    void testGetAllDietsSuccess() {
        PreferenceType pref1 = new PreferenceType("Vegan", 1);
        PreferenceType pref2 = new PreferenceType("Gluten Free", 2);
        DietType diet1 = buildDiet(1, "Diet1", pref1);
        DietType diet2 = buildDiet(2, "Diet2", pref2);

        when(dietRepository.findAll()).thenReturn(List.of(diet1, diet2));

        DietTypesResponseDto res = dietService.getAllDiets();

        assertEquals(List.of("Diet1", "Diet2"), res.getDietName());
        assertEquals(List.of(List.of(1), List.of(2)), res.getPreferenceIdLists());
    }

    @Test
    void testGetDietByNameSuccess() {
        PreferenceType pref1 = new PreferenceType("Vegan", 1);
        DietType diet = buildDiet(1, "Keto", pref1);

        when(dietRepository.findByName("Keto")).thenReturn(Optional.of(diet));

        DietTypeResponseDto res = dietService.getDietByName("Keto");

        assertEquals("Keto", res.getDietName());
        assertEquals(List.of(1), res.getPreferenceIds());
    }

    @Test
    void testGetDietByNameNotFound() {
        when(dietRepository.findByName("Keto")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.getDietByName("Keto")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetDietByIdSuccess() {
        PreferenceType pref1 = new PreferenceType("Vegan", 1);
        DietType diet = buildDiet(1, "Keto", pref1);

        when(dietRepository.findById(1)).thenReturn(Optional.of(diet));

        DietTypeResponseDto res = dietService.getDietById(1);

        assertEquals("Keto", res.getDietName());
        assertEquals(List.of(1), res.getPreferenceIds());
    }

    @Test
    void testGetDietByIdNotFound() {
        when(dietRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.getDietById(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateDietSuccess() {
        DietTypeRequestDto req = new DietTypeRequestDto();
        req.setDietName("Keto");
        req.setPreferenceIds(List.of(1, 2));

        PreferenceType pref1 = new PreferenceType("Vegan", 1);
        PreferenceType pref2 = new PreferenceType("Gluten Free", 2);

        when(dietRepository.findByName("Keto")).thenReturn(Optional.empty());
        when(preferenceRepository.findById(1)).thenReturn(Optional.of(pref1));
        when(preferenceRepository.findById(2)).thenReturn(Optional.of(pref2));
        when(dietRepository.save(any(DietType.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        DietTypeResponseDto res = dietService.createDiet(req);

        assertEquals("Keto", res.getDietName());
        assertEquals(List.of(1, 2), res.getPreferenceIds());
    }

    @Test
    void testCreateDietConflict() {
        DietTypeRequestDto req = new DietTypeRequestDto();
        req.setDietName("Keto");

        when(dietRepository.findByName("Keto")).thenReturn(Optional.of(new DietType("Keto", 1)));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.createDiet(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testUpdateDietSuccess() {
        PreferenceType pref1 = new PreferenceType("Vegan", 1);
        PreferenceType pref2 = new PreferenceType("Gluten Free", 2);
        DietType diet = buildDiet(1, "Keto", pref1);

        DietTypeRequestDto req = new DietTypeRequestDto();
        req.setId(1);
        req.setDietName("New Keto");
        req.setPreferenceIds(List.of(2));

        when(dietRepository.findById(1)).thenReturn(Optional.of(diet));
        when(dietRepository.findByName("New Keto")).thenReturn(Optional.empty());
        when(preferenceRepository.findById(2)).thenReturn(Optional.of(pref2));

        DietTypeResponseDto res = dietService.updateDiet(req);

        assertEquals("New Keto", res.getDietName());
        assertEquals(List.of(2), res.getPreferenceIds());
        verify(dietRepository).save(diet);
    }

    @Test
    void testUpdateDietNotFound() {
        DietTypeRequestDto req = new DietTypeRequestDto();
        req.setId(1);
        req.setDietName("New Keto");

        when(dietRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.updateDiet(req)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testUpdateDietConflict() {
        DietTypeRequestDto req = new DietTypeRequestDto();
        req.setId(1);
        req.setDietName("New Keto");

        DietType existing = new DietType("Keto", 1);
        DietType conflict = new DietType("New Keto", 2);

        when(dietRepository.findById(1)).thenReturn(Optional.of(existing));
        when(dietRepository.findByName("New Keto")).thenReturn(Optional.of(conflict));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.updateDiet(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(dietRepository, never()).save(existing);
    }

    @Test
    void testDeleteDietSuccess() {
        when(dietRepository.findById(1)).thenReturn(Optional.of(new DietType("Keto", 1)));

        dietService.deleteDiet(1);

        verify(dietRepository).deleteById(1);
    }

    @Test
    void testDeleteDietNotFound() {
        when(dietRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.deleteDiet(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(dietRepository, never()).deleteById(1);
    }

    @Test
    void testGetCurrentDietsSuccess() {
        User user = buildUser(1);
        DietType diet1 = new DietType("Keto", 1);
        DietType diet2 = new DietType("Vegan", 2);
        user.addDietType(diet1);
        user.addDietType(diet2);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        DietUserResponseDto res = dietService.getCurrentDiets(1);

        assertEquals(List.of("Keto", "Vegan"), res.getDietNames());
        assertEquals(List.of(1, 2), res.getDietTypeIds());
    }

    @Test
    void testGetCurrentDietsUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.getCurrentDiets(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testAdoptDietSuccess() {
        User user = buildUser(1);
        DietType diet1 = new DietType("Keto", 1);
        DietType diet2 = new DietType("Vegan", 2);
        user.addDietType(diet1);

        DietUserRequestDto req = new DietUserRequestDto();
        req.setDietTypeIds(List.of(1, 2));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(dietRepository.findById(2)).thenReturn(Optional.of(diet2));

        DietUserResponseDto res = dietService.adoptDiet(req, 1);

        assertEquals(List.of("Keto", "Vegan"), res.getDietNames());
        assertEquals(List.of(1, 2), res.getDietTypeIds());
        verify(userRepository).save(user);
    }

    @Test
    void testAdoptDietUserNotFound() {
        DietUserRequestDto req = new DietUserRequestDto();
        req.setDietTypeIds(List.of(1));

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.adoptDiet(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testAdoptDietNotFound() {
        User user = buildUser(1);

        DietUserRequestDto req = new DietUserRequestDto();
        req.setDietTypeIds(List.of(99));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(dietRepository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.adoptDiet(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testQuitDietSuccess() {
        User user = buildUser(1);
        DietType diet1 = new DietType("Keto", 1);
        DietType diet2 = new DietType("Vegan", 2);
        user.addDietType(diet1);
        user.addDietType(diet2);

        DietUserRequestDto req = new DietUserRequestDto();
        req.setDietTypeIds(List.of(1));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(dietRepository.existsById(1)).thenReturn(true);

        DietUserResponseDto res = dietService.quitDiet(req, 1);

        assertEquals(List.of("Vegan"), res.getDietNames());
        assertEquals(List.of(2), res.getDietTypeIds());
        verify(userRepository).save(user);
    }

    @Test
    void testQuitDietUserNotFound() {
        DietUserRequestDto req = new DietUserRequestDto();
        req.setDietTypeIds(List.of(1));

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.quitDiet(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testQuitDietNotFound() {
        User user = buildUser(1);

        DietUserRequestDto req = new DietUserRequestDto();
        req.setDietTypeIds(List.of(99));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(dietRepository.existsById(99)).thenReturn(false);

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> dietService.quitDiet(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    private DietType buildDiet(int id, String name, PreferenceType... preferences) {
        DietType diet = new DietType(name, id);
        for (PreferenceType preference : preferences) {
            diet.addPreferenceType(preference);
        }
        return diet;
    }

    private User buildUser(int userId) {
        return new User("User", "user" + userId + "@example.com", "pass", new Date(), null, userId);
    }
}
