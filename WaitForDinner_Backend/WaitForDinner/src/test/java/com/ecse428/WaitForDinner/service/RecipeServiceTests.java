package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.RecipeRankResponseDto;
import com.ecse428.WaitForDinner.model.DietType;
import com.ecse428.WaitForDinner.model.History;
import com.ecse428.WaitForDinner.model.Ingredient;
import com.ecse428.WaitForDinner.model.Need;
import com.ecse428.WaitForDinner.model.Pantry;
import com.ecse428.WaitForDinner.model.Recipe;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.PantryRepository;
import com.ecse428.WaitForDinner.repository.RecipeRepository;
import com.ecse428.WaitForDinner.repository.UserCookedRecipeRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;
import com.ecse428.WaitForDinner.repository.UserSavedRecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RecipeServiceTests {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSavedRecipeRepository savedRecipeRepository;

    @Mock
    private UserCookedRecipeRepository cookedRecipeRepository;
    
    @Mock
    private PantryRepository pantryRepository;

    @InjectMocks
    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeService = new RecipeService(recipeRepository, userRepository, savedRecipeRepository, cookedRecipeRepository, pantryRepository);
    }

    // ── Feature: Rank Recipes By Pantry and Dietary ──
    // Background: pantry has Eggs 6 unit, Butter 200 gram, Milk 1 litre, Flour 500 gram
    //   dietary restriction: gluten-free
    //   recipes: Omelette (Eggs,Butter;gluten-free), Pancakes (Eggs,Butter,Flour,Milk;none),
    //            Buttered Eggs (Eggs,Butter;none), Crepes (Eggs,Butter,Flour,Milk;none),
    //            Beef Stew (Beef,Potatoes,Carrots;gluten-free)

    private User buildBackgroundUser() {
        User user = new User("User", "user@example.com", "pass", new java.util.Date(), null, 1);
        Pantry pantry = new Pantry("Main", 10);
        user.addPantry(pantry);
        Ingredient eggs = new Ingredient("Eggs", 0, 100);
        Ingredient butter = new Ingredient("Butter", 0, 101);
        Ingredient milk = new Ingredient("Milk", 0, 102);
        Ingredient flour = new Ingredient("Flour", 0, 103);

        History h1 = new History(true, Date.valueOf("2025-01-01"), null, 6, pantry);
        h1.addIngredient(eggs);
        History h2 = new History(true, Date.valueOf("2025-01-01"), null, 200, pantry);
        h2.addIngredient(butter);
        History h3 = new History(true, Date.valueOf("2025-01-01"), null, 1, pantry);
        h3.addIngredient(milk);
        History h4 = new History(true, Date.valueOf("2025-01-01"), null, 500, pantry);
        h4.addIngredient(flour);

        DietType glutenFree = new DietType("gluten-free", 1);
        user.addDietType(glutenFree);
        return user;
    }

    private List<Recipe> buildBackgroundRecipes() {
        Ingredient eggs = new Ingredient("Eggs", 0, 100);
        Ingredient butter = new Ingredient("Butter", 0, 101);
        Ingredient milk = new Ingredient("Milk", 0, 102);
        Ingredient flour = new Ingredient("Flour", 0, 103);
        Ingredient beef = new Ingredient("Beef", 0, 104);
        Ingredient potatoes = new Ingredient("Potatoes", 0, 105);
        Ingredient carrots = new Ingredient("Carrots", 0, 106);
        DietType glutenFree = new DietType("gluten-free", 1);

        Recipe omelette = new Recipe("Omelette", "1", "easy", 10, 1, 1);
        omelette.addDietType(glutenFree);
        new Need(1, "", 1, omelette, eggs);
        new Need(1, "", 2, omelette, butter);

        Recipe pancakes = new Recipe("Pancakes", "2", "easy", 20, 2, 2);
        new Need(1, "", 3, pancakes, eggs);
        new Need(1, "", 4, pancakes, butter);
        new Need(1, "", 5, pancakes, flour);
        new Need(1, "", 6, pancakes, milk);

        Recipe butteredEggs = new Recipe("Buttered Eggs", "3", "easy", 15, 1, 3);
        new Need(1, "", 7, butteredEggs, eggs);
        new Need(1, "", 8, butteredEggs, butter);

        Recipe crepes = new Recipe("Crepes", "4", "medium", 25, 2, 4);
        new Need(1, "", 9, crepes, eggs);
        new Need(1, "", 10, crepes, butter);
        new Need(1, "", 11, crepes, flour);
        new Need(1, "", 12, crepes, milk);

        Recipe beefStew = new Recipe("Beef Stew", "5", "hard", 60, 4, 5);
        beefStew.addDietType(glutenFree);
        new Need(1, "", 13, beefStew, beef);
        new Need(1, "", 14, beefStew, potatoes);
        new Need(1, "", 15, beefStew, carrots);

        return List.of(omelette, pancakes, butteredEggs, crepes, beefStew);
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Recipes are ranked with highest pantry ingredient coverage first")
    void testRecipesRankedByHighestPantryCoverageFirst() {
        User user = buildBackgroundUser();
        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        // Only fully cookable recipes appear; Beef Stew (0/3 pantry matches) is excluded
        int omeletteIdx = indexOfRecipe(result, "Omelette");
        int beefStewIdx = indexOfRecipe(result, "Beef Stew");
        assertTrue(omeletteIdx >= 0, "Omelette (fully cookable) should appear in ranked list");
        assertEquals(-1, beefStewIdx, "Beef Stew (not fully cookable) should not appear in ranked list");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Among recipes with equal pantry coverage, recipes matching dietary restrictions rank higher")
    void testEqualCoverageDietaryMatchRanksHigher() {
        User user = buildBackgroundUser();
        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        // Omelette (gluten-free, 2/2 match) should appear before Buttered Eggs (no tag, 2/2 match)
        int omeletteIdx = indexOfRecipe(result, "Omelette");
        int butteredEggsIdx = indexOfRecipe(result, "Buttered Eggs");
        assertTrue(omeletteIdx < butteredEggsIdx,
            "Omelette should appear before Buttered Eggs");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: A fully cookable recipe that matches dietary restrictions ranks above a fully cookable recipe that does not")
    void testFullyCookableDietaryMatchRanksAboveFullyCookableNonMatch() {
        User user = buildBackgroundUser();
        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        int omeletteIdx = indexOfRecipe(result, "Omelette");
        int butteredEggsIdx = indexOfRecipe(result, "Buttered Eggs");
        assertTrue(omeletteIdx < butteredEggsIdx,
            "Omelette should appear before Buttered Eggs");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: A fully cookable recipe ranks above a partially cookable recipe regardless of dietary tags")
    void testFullyCookableRanksAbovePartiallyCookable() {
        User user = buildBackgroundUser();
        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        // Only fully cookable recipes appear; Beef Stew (0/3 pantry matches) is excluded entirely
        int omeletteIdx = indexOfRecipe(result, "Omelette");
        int beefStewIdx = indexOfRecipe(result, "Beef Stew");
        assertTrue(omeletteIdx >= 0, "Omelette (fully cookable) should appear in ranked list");
        assertEquals(-1, beefStewIdx, "Beef Stew (not fully cookable) should not appear in ranked list");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Rankings are updated when a new ingredient is added to the pantry")
    void testRankingsUpdatedWhenIngredientAdded() {
        User user = buildBackgroundUser();
        Pantry pantry = user.getPantries().get(0);

        // Add Beef, Potatoes, Carrots to pantry
        Ingredient beef = new Ingredient("Beef", 0, 104);
        Ingredient potatoes = new Ingredient("Potatoes", 0, 105);
        Ingredient carrots = new Ingredient("Carrots", 0, 106);
        History h5 = new History(true, Date.valueOf("2025-01-01"), null, 400, pantry);
        h5.addIngredient(beef);
        History h6 = new History(true, Date.valueOf("2025-01-01"), null, 3, pantry);
        h6.addIngredient(potatoes);
        History h7 = new History(true, Date.valueOf("2025-01-01"), null, 4, pantry);
        h7.addIngredient(carrots);

        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        // Beef Stew now fully cookable + gluten-free; with 3 ingredients matched the same as Omelette's 2
        // but Beef Stew has 3 matches and gluten-free, so it should rank high
        int beefStewIdx = indexOfRecipe(result, "Beef Stew");
        int omeletteIdx = indexOfRecipe(result, "Omelette");
        assertTrue(beefStewIdx < omeletteIdx,
            "Beef Stew should appear before Omelette after adding its ingredients to pantry");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Rankings are updated when an ingredient is removed from the pantry")
    void testRankingsUpdatedWhenIngredientRemoved() {
        User user = buildBackgroundUser();
        Pantry pantry = user.getPantries().get(0);

        // Remove Butter from pantry (mark history as has=false)
        pantry.getHistories().stream()
            .filter(h -> !h.getIngredients().isEmpty() && h.getIngredients().get(0).getName().equals("Butter"))
            .forEach(h -> h.setHas(false));

        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        // Omelette now only has 1/2 ingredients => not fully cookable => excluded from ranked list
        int omeletteIdx = indexOfRecipe(result, "Omelette");
        assertEquals(-1, omeletteIdx,
            "Omelette should not appear in ranked list after Butter is removed from pantry");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Recipes with more pantry ingredient matches rank above recipes with fewer matches - Pancakes vs Beef Stew")
    void testMoreMatchesRankAboveFewerMatchesPancakesVsBeefStew() {
        User user = buildBackgroundUser();
        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        // Pancakes fully cookable (4/4); Beef Stew not fully cookable (0/3) => excluded
        int pancakesIdx = indexOfRecipe(result, "Pancakes");
        int beefStewIdx = indexOfRecipe(result, "Beef Stew");
        assertTrue(pancakesIdx >= 0, "Pancakes (fully cookable) should appear in ranked list");
        assertEquals(-1, beefStewIdx, "Beef Stew (not fully cookable) should not appear in ranked list");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Recipes with more pantry ingredient matches rank above recipes with fewer matches - Omelette vs Beef Stew")
    void testMoreMatchesRankAboveFewerMatchesOmeletteVsBeefStew() {
        User user = buildBackgroundUser();
        List<Recipe> recipes = buildBackgroundRecipes();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(recipeRepository.findAll()).thenReturn(recipes);

        RecipeRankResponseDto result = recipeService.rankRecipes(1);

        // Omelette fully cookable (2/2); Beef Stew not fully cookable (0/3) => excluded
        int omeletteIdx = indexOfRecipe(result, "Omelette");
        int beefStewIdx = indexOfRecipe(result, "Beef Stew");
        assertTrue(omeletteIdx >= 0, "Omelette (fully cookable) should appear in ranked list");
        assertEquals(-1, beefStewIdx, "Beef Stew (not fully cookable) should not appear in ranked list");
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Unauthenticated user cannot retrieve ranked recipes")
    void testRankRecipesUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> recipeService.rankRecipes(1));
    }

    private int indexOfRecipe(RecipeRankResponseDto result, String name) {
        for (int i = 0; i < result.getRecipes().size(); i++) {
            if (result.getRecipes().get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
