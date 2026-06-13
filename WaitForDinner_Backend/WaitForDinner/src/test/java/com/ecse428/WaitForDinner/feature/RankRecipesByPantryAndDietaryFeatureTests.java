package com.ecse428.WaitForDinner.feature;

import com.ecse428.WaitForDinner.dto.RecipeRankDto;
import com.ecse428.WaitForDinner.dto.RecipeRankResponseDto;
import com.ecse428.WaitForDinner.service.RecipeService;
import com.ecse428.WaitForDinner.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Transactional
@WithMockUser
class RankRecipesByPantryAndDietaryFeatureTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(userService.requireLoggedInUserId(org.mockito.ArgumentMatchers.any())).thenReturn(1);
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Recipes are ranked with highest pantry ingredient coverage first")
    void testGetRankedRecipesHighestCoverageFirst() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Pancakes", 4, true, false),
                new RecipeRankDto("Crepes", 4, true, false),
                new RecipeRankDto("Omelette", 2, true, true),
                new RecipeRankDto("Buttered Eggs", 2, true, false),
                new RecipeRankDto("Beef Stew", 0, false, true));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes").isArray())
                .andExpect(jsonPath("$.recipes.length()").value(5))
                .andExpect(jsonPath("$.recipes[0].matchCount").value(4))
                .andExpect(jsonPath("$.recipes[4].name").value("Beef Stew"));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Among recipes with equal pantry coverage, recipes matching dietary restrictions rank higher")
    void testGetRankedRecipesDietaryTiebreaker() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Omelette", 2, true, true),
                new RecipeRankDto("Buttered Eggs", 2, true, false));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].name").value("Omelette"))
                .andExpect(jsonPath("$.recipes[0].dietaryMatch").value(true))
                .andExpect(jsonPath("$.recipes[1].name").value("Buttered Eggs"))
                .andExpect(jsonPath("$.recipes[1].dietaryMatch").value(false));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: A fully cookable recipe that matches dietary restrictions ranks above a fully cookable recipe that does not")
    void testGetRankedRecipesFullyCookableDietaryAboveNonDietary() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Omelette", 2, true, true),
                new RecipeRankDto("Buttered Eggs", 2, true, false));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].name").value("Omelette"))
                .andExpect(jsonPath("$.recipes[1].name").value("Buttered Eggs"));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: A fully cookable recipe ranks above a partially cookable recipe regardless of dietary tags")
    void testGetRankedRecipesFullyCookableAbovePartial() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Omelette", 2, true, true),
                new RecipeRankDto("Beef Stew", 0, false, true));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].name").value("Omelette"))
                .andExpect(jsonPath("$.recipes[0].fullyCookable").value(true))
                .andExpect(jsonPath("$.recipes[1].name").value("Beef Stew"))
                .andExpect(jsonPath("$.recipes[1].fullyCookable").value(false));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Rankings are updated when a new ingredient is added to the pantry")
    void testGetRankedRecipesAfterIngredientAdded() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Beef Stew", 3, true, true),
                new RecipeRankDto("Omelette", 2, true, true));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].name").value("Beef Stew"))
                .andExpect(jsonPath("$.recipes[1].name").value("Omelette"));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Rankings are updated when an ingredient is removed from the pantry")
    void testGetRankedRecipesAfterIngredientRemoved() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Pancakes", 3, false, false),
                new RecipeRankDto("Omelette", 1, false, true));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].matchCount").value(3))
                .andExpect(jsonPath("$.recipes[1].name").value("Omelette"));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario Outline: Recipes with more pantry ingredient matches rank above recipes with fewer matches | Pancakes(4) above Beef Stew(0)")
    void testGetRankedRecipesMoreMatchesAboveFewerPancakesBeefStew() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Pancakes", 4, true, false),
                new RecipeRankDto("Beef Stew", 0, false, true));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].name").value("Pancakes"))
                .andExpect(jsonPath("$.recipes[1].name").value("Beef Stew"));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario Outline: Recipes with more pantry ingredient matches rank above recipes with fewer matches | Omelette(2) above Beef Stew(0)")
    void testGetRankedRecipesMoreMatchesAboveFewerOmeletteBeefStew() throws Exception {
        List<RecipeRankDto> ranked = List.of(
                new RecipeRankDto("Omelette", 2, true, true),
                new RecipeRankDto("Beef Stew", 0, false, true));
        RecipeRankResponseDto response = new RecipeRankResponseDto(ranked);
        when(recipeService.rankRecipes(1)).thenReturn(response);

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].name").value("Omelette"))
                .andExpect(jsonPath("$.recipes[1].name").value("Beef Stew"));
    }

    @Test
    @DisplayName("Feature: Rank Recipes By Pantry and Dietary | Scenario: Unauthenticated user cannot retrieve ranked recipes")
    @WithAnonymousUser
    void testGetRankedRecipesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isForbidden());
    }
}
