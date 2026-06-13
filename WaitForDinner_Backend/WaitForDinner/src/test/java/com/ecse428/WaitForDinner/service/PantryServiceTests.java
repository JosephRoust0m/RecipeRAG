package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.PantryHistoryResponseDto;
import com.ecse428.WaitForDinner.dto.PantryIngredientAmountRequestDto;
import com.ecse428.WaitForDinner.dto.PantryIngredientRequestDto;
import com.ecse428.WaitForDinner.dto.PantryItemDto;
import com.ecse428.WaitForDinner.dto.PantryItemsResponseDto;
import com.ecse428.WaitForDinner.dto.PantryTypeDto;
import com.ecse428.WaitForDinner.dto.PantryTypeResponseDto;
import com.ecse428.WaitForDinner.model.History;
import com.ecse428.WaitForDinner.model.Ingredient;
import com.ecse428.WaitForDinner.model.Pantry;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.HistoryRepository;
import com.ecse428.WaitForDinner.repository.IngredientRepository;
import com.ecse428.WaitForDinner.repository.PantryRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PantryServiceTests {

    @Mock
    private PantryRepository pantryRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private PantryService pantryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pantryService = new PantryService(pantryRepository, ingredientRepository, userRepository, historyRepository);
    }

    @Test
    void testGetPantryTypeByIdSuccess() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        Ingredient salt = buildIngredient(100, "Salt");
        Ingredient pepper = buildIngredient(101, "Pepper");
        Ingredient old = buildIngredient(102, "Old");

        History saltHistory = new History(true, new Date(System.currentTimeMillis()), null, 1, pantry);
        saltHistory.addIngredient(salt);

        History pepperHistory = new History(true, new Date(System.currentTimeMillis()), null, 1, pantry);
        pepperHistory.addIngredient(pepper);

        History oldHistory = new History(false, new Date(System.currentTimeMillis()), null, 0, pantry);
        oldHistory.addIngredient(old);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        PantryTypeResponseDto res = pantryService.getPantryTypeById(10, 1);

        assertNotNull(res);
        assertEquals(1, res.getPantries().size());
        PantryTypeDto dto = res.getPantries().get(0);
        assertEquals("Main", dto.getName());
        assertEquals(List.of(100, 101), dto.getIngredientIds());
        assertEquals(List.of("Salt", "Pepper"), dto.getIngredientNames());
    }

    @Test
    void testGetPantryTypeByIdNotFound() {
        when(pantryRepository.findById(10)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.getPantryTypeById(10, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetPantryTypeByIdForbidden() {
        Pantry pantry = buildPantry(10, "Main");
        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.getPantryTypeById(10, 99)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testGetAllPantriesSuccess() {
        User user = buildUser(1);
        Pantry pantry1 = buildPantry(10, "Main");
        Pantry pantry2 = buildPantry(11, "Backup");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry1, pantry2));

        PantryTypeResponseDto res = pantryService.getAllPantries(1);

        assertNotNull(res);
        assertEquals(2, res.getPantries().size());
    }

    @Test
    void testGetAllPantriesUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.getAllPantries(1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetAllPantriesIgnoresHasFalse() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        Ingredient current = buildIngredient(100, "Salt");
        Ingredient old = buildIngredient(101, "Pepper");

        History currentHistory = new History(true, new Date(System.currentTimeMillis()), null, 1, pantry);
        currentHistory.addIngredient(current);

        History oldHistory = new History(false, new Date(System.currentTimeMillis()), null, 0, pantry);
        oldHistory.addIngredient(old);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry));

        PantryTypeResponseDto res = pantryService.getAllPantries(1);

        assertEquals(1, res.getPantries().size());
        assertEquals(List.of(100), res.getPantries().get(0).getIngredientIds());
        assertEquals(List.of("Salt"), res.getPantries().get(0).getIngredientNames());
    }

    @Test
    void testCreatePantrySuccess() {
        User user = buildUser(1);
        Pantry saved = buildPantry(20, "New Pantry");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.save(any(Pantry.class))).thenReturn(saved);

        PantryTypeResponseDto res = pantryService.createPantry("New Pantry", 1);

        assertEquals(1, res.getPantries().size());
        assertEquals("New Pantry", res.getPantries().get(0).getName());
        assertEquals(1, user.numberOfPantries());
        verify(pantryRepository).save(any(Pantry.class));
        verify(userRepository).save(user);
    }

    @Test
    void testCreatePantryMissingName() {
        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.createPantry(" ", 1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(pantryRepository, never()).save(any(Pantry.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeletePantrySuccess() {
        User user1 = buildUser(1);
        User user2 = buildUser(2);
        Pantry pantry = buildPantry(10, "Main");
        user1.addPantry(pantry);
        user2.addPantry(pantry);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        pantryService.deletePantry(10, 1);

        assertTrue(user1.getPantries().isEmpty());
        assertTrue(user2.getPantries().isEmpty());
        assertTrue(pantry.getUsers().isEmpty());
        verify(userRepository).save(user1);
        verify(userRepository).save(user2);
        verify(pantryRepository).deleteById(10);
    }

    @Test
    void testDeletePantryNotFound() {
        when(pantryRepository.findById(10)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.deletePantry(10, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(pantryRepository, never()).deleteById(10);
    }

    @Test
    void testDeletePantryForbidden() {
        Pantry pantry = buildPantry(10, "Main");
        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.deletePantry(10, 123)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(pantryRepository, never()).deleteById(10);
    }

    @Test
    void testAddIngredientSuccess() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient ingredient = buildIngredient(100, "Salt");

        PantryIngredientAmountRequestDto req = new PantryIngredientAmountRequestDto(10, 100, 3);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(100)).thenReturn(Optional.of(ingredient));
        when(historyRepository.save(any(History.class))).thenAnswer(inv -> inv.getArgument(0));

        PantryTypeResponseDto res = pantryService.addIngredient(req, 1);

        assertEquals(1, res.getPantries().size());
        PantryTypeDto dto = res.getPantries().get(0);
        assertEquals(List.of(100), dto.getIngredientIds());
        assertEquals(List.of("Salt"), dto.getIngredientNames());
        assertEquals(1, pantry.getHistories().size());

        History history = pantry.getHistories().get(0);
        assertTrue(history.getHas());
        assertEquals(3, history.getAmount());
        assertNotNull(history.getDateAdded());
        assertNotNull(history.getExpiryDate());

        verify(historyRepository).save(any(History.class));
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    void testAddIngredientAlreadyPresent() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient ingredient = buildIngredient(100, "Salt");

        History history = new History(true, new Date(System.currentTimeMillis()), null, 2, pantry);
        history.addIngredient(ingredient);

        PantryIngredientAmountRequestDto req = new PantryIngredientAmountRequestDto(10, 100, 4);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(100)).thenReturn(Optional.of(ingredient));
        when(historyRepository.save(any(History.class))).thenAnswer(inv -> inv.getArgument(0));

        PantryTypeResponseDto res = pantryService.addIngredient(req, 1);

        assertEquals(List.of(100), res.getPantries().get(0).getIngredientIds());
        assertEquals(2, history.getAmount());
        assertTrue(history.getHas());
        assertEquals(2, pantry.getHistories().size());

        long matchingCount = pantry.getHistories().stream()
            .filter(item -> !item.getIngredients().isEmpty())
            .filter(item -> item.getIngredients().get(0).getIngredientId() == ingredient.getIngredientId())
            .count();
        assertEquals(2, matchingCount);

        verify(historyRepository).save(any(History.class));
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    void testAddIngredientHistoryWasFalse() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient ingredient = buildIngredient(100, "Salt");

        History history = new History(false, new Date(System.currentTimeMillis()), null, 0, pantry);
        history.addIngredient(ingredient);

        PantryIngredientAmountRequestDto req = new PantryIngredientAmountRequestDto(10, 100, 5);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(100)).thenReturn(Optional.of(ingredient));
        when(historyRepository.save(any(History.class))).thenAnswer(inv -> inv.getArgument(0));

        PantryTypeResponseDto res = pantryService.addIngredient(req, 1);

        assertEquals(List.of(100), res.getPantries().get(0).getIngredientIds());
        assertEquals(0, history.getAmount());
        assertTrue(!history.getHas());
        assertEquals(2, pantry.getHistories().size());

        long activeCount = pantry.getHistories().stream()
            .filter(History::getHas)
            .filter(item -> !item.getIngredients().isEmpty())
            .filter(item -> item.getIngredients().get(0).getIngredientId() == ingredient.getIngredientId())
            .count();
        assertEquals(1, activeCount);

        verify(historyRepository).save(any(History.class));
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    void testAddIngredientMissingIngredient() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        PantryIngredientAmountRequestDto req = new PantryIngredientAmountRequestDto(10, 0, 1);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.addIngredient(req, 1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testAddIngredientNotFound() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        PantryIngredientAmountRequestDto req = new PantryIngredientAmountRequestDto(10, 999, 1);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(999)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.addIngredient(req, 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testRemoveIngredientSuccess() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient ingredient = buildIngredient(100, "Salt");

        History history = new History(true, new Date(System.currentTimeMillis()), null, 1, pantry);
        history.addIngredient(ingredient);

        PantryIngredientRequestDto req = new PantryIngredientRequestDto(10, 100, null);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(100)).thenReturn(Optional.of(ingredient));

        PantryTypeResponseDto res = pantryService.removeIngredient(req, 1);

        assertTrue(res.getPantries().get(0).getIngredientIds().isEmpty());
        assertEquals(1, pantry.getHistories().size());
        assertTrue(history.getIngredients().contains(ingredient));
        assertEquals(0, history.getAmount());
        assertTrue(!history.getHas());

        verify(pantryRepository).save(pantry);
    }

    @Test
    void testRemoveIngredientNotPresent() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient existing = buildIngredient(100, "Salt");
        Ingredient other = buildIngredient(101, "Pepper");

        History history = new History(true, new Date(System.currentTimeMillis()), null, 1, pantry);
        history.addIngredient(existing);

        PantryIngredientRequestDto req = new PantryIngredientRequestDto(10, 101, null);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(101)).thenReturn(Optional.of(other));

        PantryTypeResponseDto res = pantryService.removeIngredient(req, 1);

        assertEquals(List.of(100), res.getPantries().get(0).getIngredientIds());
        verify(pantryRepository, never()).save(any(Pantry.class));
    }

    @Test
    void testRemoveIngredientMissingIngredient() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        PantryIngredientRequestDto req = new PantryIngredientRequestDto(10, 0, " ");

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.removeIngredient(req, 1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testRemoveIngredientAlreadyAbsentRemovesHistory() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient ingredient = buildIngredient(100, "Salt");

        History history = new History(false, new Date(System.currentTimeMillis()), null, 0, pantry);
        history.addIngredient(ingredient);

        PantryIngredientRequestDto req = new PantryIngredientRequestDto(10, 100, null);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(100)).thenReturn(Optional.of(ingredient));

        PantryTypeResponseDto res = pantryService.removeIngredient(req, 1);

        assertTrue(pantry.getHistories().isEmpty());
        assertTrue(res.getPantries().get(0).getIngredientIds().isEmpty());
        verify(pantryRepository).save(pantry);
    }

    @Test
    void testRemoveIngredientAmountUsesClosestExpiryFirst() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient ingredient = buildIngredient(100, "Salt");

        long now = System.currentTimeMillis();
        History early = new History(true, new Date(now), new Date(now + 1_000L), 1, pantry);
        early.addIngredient(ingredient);
        History later = new History(true, new Date(now), new Date(now + 10_000L), 3, pantry);
        later.addIngredient(ingredient);

        PantryIngredientAmountRequestDto req = new PantryIngredientAmountRequestDto(10, 100, 2);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(100)).thenReturn(Optional.of(ingredient));

        PantryTypeResponseDto res = pantryService.removeIngredientAmount(req, 1);

        assertEquals(0, early.getAmount());
        assertTrue(!early.getHas());
        assertEquals(2, later.getAmount());
        assertTrue(later.getHas());
        assertEquals(List.of(100), res.getPantries().get(0).getIngredientIds());

        verify(pantryRepository).save(pantry);
    }

    @Test
    void testRemoveIngredientAmountNotEnoughBecauseExpired() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        Ingredient ingredient = buildIngredient(100, "Salt");

        long now = System.currentTimeMillis();
        History expired = new History(true, new Date(now - 10_000L), new Date(now - 1_000L), 5, pantry);
        expired.addIngredient(ingredient);
        History valid = new History(true, new Date(now), new Date(now + 10_000L), 1, pantry);
        valid.addIngredient(ingredient);

        PantryIngredientAmountRequestDto req = new PantryIngredientAmountRequestDto(10, 100, 2);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));
        when(ingredientRepository.findById(100)).thenReturn(Optional.of(ingredient));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.removeIngredientAmount(req, 1)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals(5, expired.getAmount());
        assertEquals(1, valid.getAmount());
        verify(pantryRepository, never()).save(any(Pantry.class));
    }

    // ── Feature: View Pantry History ──
    // Background: pantry history contains Eggs added 12 unit 2025-01-01,
    //   Milk added 2 litre 2025-01-02, Eggs removed 6 unit 2025-01-03,
    //   Flour added 500 gram 2025-01-04, Milk removed 2 litre 2025-01-05

    private void setupBackgroundHistory(Pantry pantry) {
        Ingredient eggs = buildIngredient(100, "Eggs");
        Ingredient milk = buildIngredient(101, "Milk");
        Ingredient flour = buildIngredient(102, "Flour");

        History h1 = new History(true, Date.valueOf("2025-01-01"), null, 12, pantry);
        h1.addIngredient(eggs);
        History h2 = new History(true, Date.valueOf("2025-01-02"), null, 2, pantry);
        h2.addIngredient(milk);
        History h3 = new History(false, Date.valueOf("2025-01-03"), null, 6, pantry);
        h3.addIngredient(eggs);
        History h4 = new History(true, Date.valueOf("2025-01-04"), null, 500, pantry);
        h4.addIngredient(flour);
        History h5 = new History(false, Date.valueOf("2025-01-05"), null, 2, pantry);
        h5.addIngredient(milk);
    }

    @Test
    @DisplayName("Feature: View Pantry History | Scenario: User views their full pantry history")
    void testGetPantryHistoryFull() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        setupBackgroundHistory(pantry);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        PantryHistoryResponseDto res = pantryService.getPantryHistory(10, 1);

        assertEquals(5, res.getEvents().size());
        assertEquals("Eggs", res.getEvents().get(0).getItem());
        assertEquals("added", res.getEvents().get(0).getAction());
        assertEquals(12, res.getEvents().get(0).getQuantity());
        assertEquals("unit", res.getEvents().get(0).getUnit());
        assertEquals("2025-01-01", res.getEvents().get(0).getDate());
        assertEquals("unit", res.getEvents().get(0).getUnit());
    }

    @Test
    @DisplayName("Feature: View Pantry History | Scenario: User views pantry history filtered by item name")
    void testGetPantryHistoryFilteredByItemName() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        setupBackgroundHistory(pantry);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        PantryHistoryResponseDto res = pantryService.getPantryHistory(10, 1, "Eggs", null, null);

        assertEquals(2, res.getEvents().size());
        assertTrue(res.getEvents().stream().allMatch(e -> e.getItem().equals("Eggs")));
        assertTrue(res.getEvents().stream().noneMatch(e -> !e.getItem().equals("Eggs")));
    }

    @Test
    @DisplayName("Feature: View Pantry History | Scenario: User filters pantry history by date range 2025-01-01 to 2025-01-02")
    void testGetPantryHistoryFilteredByDateRange1() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);
        setupBackgroundHistory(pantry);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        PantryHistoryResponseDto res = pantryService.getPantryHistory(10, 1, null, "2025-01-01", "2025-01-02");

        assertEquals(2, res.getEvents().size());
        assertTrue(res.getEvents().stream().allMatch(e ->
            e.getDate().compareTo("2025-01-01") >= 0 && e.getDate().compareTo("2025-01-02") <= 0));
    }

    @Test
    @DisplayName("Feature: View Pantry History | Scenario: User with no pantry history receives an empty history")
    void testGetPantryHistoryEmpty() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        when(pantryRepository.findById(10)).thenReturn(Optional.of(pantry));

        PantryHistoryResponseDto res = pantryService.getPantryHistory(10, 1);
        assertTrue(res.getEvents().isEmpty());
    }

    @Test
    void testGetPantryItemsSuccess() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        Ingredient eggs = buildIngredient(100, "Eggs");
        Ingredient milk = buildIngredient(101, "Milk");
        Ingredient flour = buildIngredient(102, "Flour");

        History eggsHistory = new History(true, Date.valueOf("2025-01-01"), null, 12, pantry);
        eggsHistory.addIngredient(eggs);

        History milkHistory = new History(true, Date.valueOf("2025-01-02"), null, 2, pantry);
        milkHistory.addIngredient(milk);

        History flourHistory = new History(true, Date.valueOf("2025-01-03"), null, 500, pantry);
        flourHistory.addIngredient(flour);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry));

        PantryItemsResponseDto res = pantryService.getPantryItems(1);

        assertEquals(3, res.getItems().size());
        assertEquals("Eggs", res.getItems().get(0).getName());
        assertEquals(12, res.getItems().get(0).getQuantity());
        assertEquals("unit", res.getItems().get(0).getUnit());
    }

    @Test
    void testGetPantryItemsAlphabeticalOrder() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        Ingredient milk = buildIngredient(101, "Milk");
        Ingredient butter = buildIngredient(102, "Butter");
        Ingredient eggs = buildIngredient(100, "Eggs");

        History milkHistory = new History(true, Date.valueOf("2025-01-02"), null, 2, pantry);
        milkHistory.addIngredient(milk);

        History butterHistory = new History(true, Date.valueOf("2025-01-01"), null, 200, pantry);
        butterHistory.addIngredient(butter);

        History eggsHistory = new History(true, Date.valueOf("2025-01-03"), null, 12, pantry);
        eggsHistory.addIngredient(eggs);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry));

        PantryItemsResponseDto res = pantryService.getPantryItems(1);

        assertEquals("Butter", res.getItems().get(0).getName());
        assertEquals("Eggs", res.getItems().get(1).getName());
        assertEquals("Milk", res.getItems().get(2).getName());
    }

    @Test
    void testGetPantryItemByNameSuccess() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        Ingredient eggs = buildIngredient(100, "Eggs");
        History eggsHistory = new History(true, Date.valueOf("2025-01-01"), null, 12, pantry);
        eggsHistory.addIngredient(eggs);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry));

        PantryItemDto res = pantryService.getPantryItemByName("Eggs", 1);

        assertEquals("Eggs", res.getName());
        assertEquals(12, res.getQuantity());
        assertEquals("unit", res.getUnit());
    }

    @Test
    void testGetPantryItemByNameNotFound() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.getPantryItemByName("Olive Oil", 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testDeletePantryItemByNameSuccess() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        Ingredient eggs = buildIngredient(100, "Eggs");
        History eggsHistory = new History(true, Date.valueOf("2025-01-01"), null, 12, pantry);
        eggsHistory.addIngredient(eggs);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry));

        pantryService.deletePantryItemByName("Eggs", 1);

        assertTrue(!eggsHistory.getHas());
        assertEquals(0, eggsHistory.getAmount());
        verify(pantryRepository).save(pantry);
    }

    @Test
    void testDeletePantryItemByNameNotFound() {
        User user = buildUser(1);
        Pantry pantry = buildPantry(10, "Main");
        user.addPantry(pantry);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(pantryRepository.findAllByUsers(user)).thenReturn(List.of(pantry));

        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> pantryService.deletePantryItemByName("Olive Oil", 1)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(pantryRepository, never()).save(any(Pantry.class));
    }

    private User buildUser(int userId) {
        return new User(
            "User",
            "user" + userId + "@example.com",
            "pass",
            new java.util.Date(),
            null,
            userId
        );
    }

    private Pantry buildPantry(int pantryId, String name) {
        return new Pantry(name, pantryId);
    }

    private Ingredient buildIngredient(int ingredientId, String name) {
        return new Ingredient(name, 0, ingredientId);
    }
}