package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.IngredientRequestDto;
import com.ecse428.WaitForDinner.dto.IngredientResponseDto;
import com.ecse428.WaitForDinner.dto.IngredientUserRequestDto;
import com.ecse428.WaitForDinner.dto.IngredientUserResponseDto;
import com.ecse428.WaitForDinner.dto.IngredientsResponseDto;
import com.ecse428.WaitForDinner.model.Ingredient;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.model.UserIngredientPreference;
import com.ecse428.WaitForDinner.repository.IngredientRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IngredientServiceTests {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientService = new IngredientService(ingredientRepository, userRepository);
    }

    @Test
    void testGetAllIngredientsSuccess() {
        List<Ingredient> ingredients = List.of(
            new Ingredient("Salt", 0, 1),
            new Ingredient("Sugar", 0, 2)
        );
        when(ingredientRepository.findAll()).thenReturn(ingredients);

        IngredientsResponseDto res = ingredientService.getAllIngredients();

        assertEquals(List.of("Salt", "Sugar"), res.getIngredientNames());
        assertEquals(List.of(1, 2), res.getIds());
    }

    @Test
    void testGetIngredientByIdSuccess() {
        Ingredient ingredient = new Ingredient("Salt", 0, 1);
        when(ingredientRepository.findById(1)).thenReturn(Optional.of(ingredient));

        IngredientResponseDto res = ingredientService.getIngredientById(1);

        assertEquals(1, res.getId());
        assertEquals("Salt", res.getIngredientName());
    }

    @Test
    void testGetIngredientByIdNotFound() {
        when(ingredientRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.getIngredientById(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetIngredientByNameSuccess() {
        Ingredient ingredient = new Ingredient("Salt", 0, 1);
        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.of(ingredient));

        IngredientResponseDto res = ingredientService.getIngredientByName("Salt");

        assertEquals(1, res.getId());
        assertEquals("Salt", res.getIngredientName());
    }

    @Test
    void testGetIngredientByNameNotFound() {
        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.getIngredientByName("Salt")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCreateIngredientSuccess() {
        IngredientRequestDto req = new IngredientRequestDto();
        req.setIngredientName("Salt");

        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class)))
            .thenReturn(new Ingredient("Salt", 0, 2));

        IngredientResponseDto res = ingredientService.createIngredient(req);

        assertEquals(2, res.getId());
        assertEquals("Salt", res.getIngredientName());
        verify(ingredientRepository).save(any(Ingredient.class));
    }

    @Test
    void testCreateIngredientConflict() {
        IngredientRequestDto req = new IngredientRequestDto();
        req.setIngredientName("Salt");

        when(ingredientRepository.findByName("Salt"))
            .thenReturn(Optional.of(new Ingredient("Salt", 0, 1)));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.createIngredient(req)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testDeleteIngredientSuccess() {
        when(ingredientRepository.findById(1)).thenReturn(Optional.of(new Ingredient("Salt", 0, 1)));

        ingredientService.deleteIngredient(1);

        verify(ingredientRepository).deleteById(1);
    }

    @Test
    void testDeleteIngredientNotFound() {
        when(ingredientRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.deleteIngredient(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(ingredientRepository, never()).deleteById(1);
    }

    @Test
    void testGetIngredientPreferencesSuccess() {
        User user = buildUser(1);
        Ingredient ingredient = new Ingredient("Salt", 0, 10);
        user.addUserIngredientPreference(UserIngredientPreference.Status.Like, 0, ingredient);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        IngredientUserResponseDto res = ingredientService.getIngredientPreferences(1);

        assertEquals(List.of("Salt"), res.getIngredientNames());
        assertEquals(List.of(10), res.getIngredientIds());
        assertEquals(List.of("Like"), res.getStatuses());
    }

    @Test
    void testGetIngredientPreferencesUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.getIngredientPreferences(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testAddIngredientPreferenceSuccess() {
        User user = buildUser(1);
        Ingredient ingredient = new Ingredient("Salt", 0, 10);

        IngredientUserRequestDto req = new IngredientUserRequestDto();
        req.setIngredientId(10);
        req.setStatus(IngredientUserRequestDto.Status.Like);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10)).thenReturn(Optional.of(ingredient));

        IngredientUserResponseDto res = ingredientService.addIngredientPreference(req, 1);

        assertEquals(List.of("Salt"), res.getIngredientNames());
        assertEquals(List.of(10), res.getIngredientIds());
        assertEquals(List.of("Like"), res.getStatuses());
        verify(userRepository).save(user);
    }

    @Test
    void testAddIngredientPreferenceMissingIngredient() {
        User user = buildUser(1);

        IngredientUserRequestDto req = new IngredientUserRequestDto();
        req.setStatus(IngredientUserRequestDto.Status.Like);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.addIngredientPreference(req, 1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testAddIngredientPreferenceMissingStatus() {
        User user = buildUser(1);
        Ingredient ingredient = new Ingredient("Salt", 0, 10);

        IngredientUserRequestDto req = new IngredientUserRequestDto();
        req.setIngredientId(10);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10)).thenReturn(Optional.of(ingredient));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.addIngredientPreference(req, 1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testRemoveIngredientPreferenceSuccess() {
        User user = buildUser(1);
        Ingredient ingredient = new Ingredient("Salt", 0, 10);
        user.addUserIngredientPreference(UserIngredientPreference.Status.Like, 0, ingredient);

        IngredientUserRequestDto req = new IngredientUserRequestDto();
        req.setIngredientId(10);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(ingredientRepository.findById(10)).thenReturn(Optional.of(ingredient));

        IngredientUserResponseDto res = ingredientService.removeIngredientPreference(req, 1);

        assertTrue(res.getIngredientNames().isEmpty());
        assertTrue(res.getIngredientIds().isEmpty());
        assertTrue(res.getStatuses().isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveIngredientPreferenceUserNotFound() {
        IngredientUserRequestDto req = new IngredientUserRequestDto();
        req.setIngredientId(10);

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> ingredientService.removeIngredientPreference(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    private User buildUser(int userId) {
        return new User("User", "user" + userId + "@example.com", "pass", new Date(), null, userId);
    }
}
