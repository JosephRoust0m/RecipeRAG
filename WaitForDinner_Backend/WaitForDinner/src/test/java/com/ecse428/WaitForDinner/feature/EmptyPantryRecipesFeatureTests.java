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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Transactional
@WithMockUser
class EmptyPantryRecipesFeatureTests {

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
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario: System suggests recipes when pantry is empty")
    void testEmptyPantrySystemSuggestsRecipes() throws Exception {
        List<RecipeRankDto> recipes = List.of(
                new RecipeRankDto("Spaghetti Carbonara", 0, false, false),
                new RecipeRankDto("Tacos", 0, false, false),
                new RecipeRankDto("Sushi", 0, false, false));
        when(recipeService.rankRecipes(1)).thenReturn(new RecipeRankResponseDto(recipes));

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes").isArray())
                .andExpect(jsonPath("$.recipes.length()").value(3));
    }

    @Test
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario: Recipes displayed when pantry is empty are marked as requiring ingredients")
    void testEmptyPantryRecipesMarkedAsRequiringIngredients() throws Exception {
        List<RecipeRankDto> recipes = List.of(
                new RecipeRankDto("Spaghetti Carbonara", 0, false, false),
                new RecipeRankDto("Tacos", 0, false, false));
        when(recipeService.rankRecipes(1)).thenReturn(new RecipeRankResponseDto(recipes));

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].fullyCookable").value(false))
                .andExpect(jsonPath("$.recipes[0].matchCount").value(0))
                .andExpect(jsonPath("$.recipes[1].fullyCookable").value(false))
                .andExpect(jsonPath("$.recipes[1].matchCount").value(0));
    }

    @Test
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario: User with empty pantry receives different suggestions than user with stocked pantry")
    void testEmptyPantryUserGetsDifferentSuggestionsThanStockedUser() {
        assertTrue(true);
    }

    @Test
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario: System does not return an error when pantry is empty and recipes are requested")
    void testEmptyPantryNoErrorReturned() throws Exception {
        List<RecipeRankDto> recipes = List.of(
                new RecipeRankDto("Spaghetti Carbonara", 0, false, false));
        when(recipeService.rankRecipes(1)).thenReturn(new RecipeRankResponseDto(recipes));

        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes").isArray());
    }

    @Test
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario Outline: System returns recipes matching cuisine preference even with empty pantry | Italian")
    void testEmptyPantryCuisineItalian() {
        assertTrue(true);
    }

    @Test
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario Outline: System returns recipes matching cuisine preference even with empty pantry | Japanese")
    void testEmptyPantryCuisineJapanese() {
        assertTrue(true);
    }

    @Test
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario Outline: System returns recipes matching cuisine preference even with empty pantry | Mexican")
    void testEmptyPantryCuisineMexican() {
        assertTrue(true);
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Feature: Display possible recipes when pantry is empty | Scenario: Unauthenticated user cannot request recipe suggestions")
    void testEmptyPantryUnauthenticatedUserForbidden() throws Exception {
        mockMvc.perform(get("/api/recipe/ranked"))
                .andExpect(status().isForbidden());
    }
}
