package com.ecse428.WaitForDinner.feature;

import com.ecse428.WaitForDinner.dto.RecipeFilterDto;
import com.ecse428.WaitForDinner.dto.RecipeFilterResponseDto;
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
class FilterRecipesByPreferenceTimeDifficultyFeatureTests {

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
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: User filters recipes by a single dietary preference")
    void testFilterRecipesByDietaryPreference() throws Exception {
        List<RecipeFilterDto> filtered = List.of(
                new RecipeFilterDto("Tofu Stir Fry", "vegan", 20, "easy"),
                new RecipeFilterDto("Mushroom Risotto", "vegan", 40, "medium"));
        when(recipeService.filterRecipes(1, "vegan", null, null))
                .thenReturn(new RecipeFilterResponseDto(filtered));

        mockMvc.perform(get("/api/recipe/filter").param("dietaryTag", "vegan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes.length()").value(2))
                .andExpect(jsonPath("$.recipes[0].dietaryTag").value("vegan"))
                .andExpect(jsonPath("$.recipes[1].dietaryTag").value("vegan"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: User filters recipes by maximum cooking time")
    void testFilterRecipesByMaxCookingTime() throws Exception {
        List<RecipeFilterDto> filtered = List.of(
                new RecipeFilterDto("Omelette", "vegetarian", 10, "easy"),
                new RecipeFilterDto("Pancakes", "vegetarian", 15, "easy"),
                new RecipeFilterDto("Tofu Stir Fry", "vegan", 20, "easy"));
        when(recipeService.filterRecipes(1, null, 20, null))
                .thenReturn(new RecipeFilterResponseDto(filtered));

        mockMvc.perform(get("/api/recipe/filter").param("maxTime", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes.length()").value(3))
                .andExpect(jsonPath("$.recipes[0].cookingTime").value(10))
                .andExpect(jsonPath("$.recipes[1].cookingTime").value(15))
                .andExpect(jsonPath("$.recipes[2].cookingTime").value(20));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: User filters recipes by difficulty level")
    void testFilterRecipesByDifficultyLevel() throws Exception {
        List<RecipeFilterDto> filtered = List.of(
                new RecipeFilterDto("Omelette", "vegetarian", 10, "easy"),
                new RecipeFilterDto("Pancakes", "vegetarian", 15, "easy"),
                new RecipeFilterDto("Tofu Stir Fry", "vegan", 20, "easy"));
        when(recipeService.filterRecipes(1, null, null, "easy"))
                .thenReturn(new RecipeFilterResponseDto(filtered));

        mockMvc.perform(get("/api/recipe/filter").param("difficulty", "easy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes.length()").value(3))
                .andExpect(jsonPath("$.recipes[0].difficulty").value("easy"))
                .andExpect(jsonPath("$.recipes[1].difficulty").value("easy"))
                .andExpect(jsonPath("$.recipes[2].difficulty").value("easy"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: User filters recipes by dietary preference and difficulty level combined")
    void testFilterRecipesByDietaryAndDifficulty() throws Exception {
        List<RecipeFilterDto> filtered = List.of(
                new RecipeFilterDto("Omelette", "vegetarian", 10, "easy"),
                new RecipeFilterDto("Pancakes", "vegetarian", 15, "easy"));
        when(recipeService.filterRecipes(1, "vegetarian", null, "easy"))
                .thenReturn(new RecipeFilterResponseDto(filtered));

        mockMvc.perform(get("/api/recipe/filter")
                .param("dietaryTag", "vegetarian")
                .param("difficulty", "easy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes.length()").value(2))
                .andExpect(jsonPath("$.recipes[0].dietaryTag").value("vegetarian"))
                .andExpect(jsonPath("$.recipes[0].difficulty").value("easy"))
                .andExpect(jsonPath("$.recipes[1].dietaryTag").value("vegetarian"))
                .andExpect(jsonPath("$.recipes[1].difficulty").value("easy"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: User filters recipes by dietary preference and maximum cooking time combined")
    void testFilterRecipesByDietaryAndMaxTime() throws Exception {
        List<RecipeFilterDto> filtered = List.of(
                new RecipeFilterDto("Chicken Tikka Masala", "gluten-free", 45, "medium"));
        when(recipeService.filterRecipes(1, "gluten-free", 50, null))
                .thenReturn(new RecipeFilterResponseDto(filtered));

        mockMvc.perform(get("/api/recipe/filter")
                .param("dietaryTag", "gluten-free")
                .param("maxTime", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes.length()").value(1))
                .andExpect(jsonPath("$.recipes[0].name").value("Chicken Tikka Masala"))
                .andExpect(jsonPath("$.recipes[0].dietaryTag").value("gluten-free"))
                .andExpect(jsonPath("$.recipes[0].cookingTime").value(45));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: User applies all three filters simultaneously")
    void testFilterRecipesByAllThreeFilters() throws Exception {
        List<RecipeFilterDto> filtered = List.of(
                new RecipeFilterDto("Tofu Stir Fry", "vegan", 20, "easy"));
        when(recipeService.filterRecipes(1, "vegan", 25, "easy"))
                .thenReturn(new RecipeFilterResponseDto(filtered));

        mockMvc.perform(get("/api/recipe/filter")
                .param("dietaryTag", "vegan")
                .param("maxTime", "25")
                .param("difficulty", "easy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes.length()").value(1))
                .andExpect(jsonPath("$.recipes[0].name").value("Tofu Stir Fry"))
                .andExpect(jsonPath("$.recipes[0].dietaryTag").value("vegan"))
                .andExpect(jsonPath("$.recipes[0].cookingTime").value(20))
                .andExpect(jsonPath("$.recipes[0].difficulty").value("easy"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: No recipes match the applied filters")
    void testFilterRecipesNoMatchReturnsEmptyList() throws Exception {
        when(recipeService.filterRecipes(1, "vegan", null, "hard"))
                .thenReturn(new RecipeFilterResponseDto(List.of()));

        mockMvc.perform(get("/api/recipe/filter")
                .param("dietaryTag", "vegan")
                .param("difficulty", "hard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes").isArray())
                .andExpect(jsonPath("$.recipes.length()").value(0));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario Outline: User filters recipes by each supported difficulty level | easy")
    void testFilterRecipesByDifficultyEasy() throws Exception {
        when(recipeService.filterRecipes(1, null, null, "easy"))
                .thenReturn(new RecipeFilterResponseDto(List.of(
                        new RecipeFilterDto("Omelette", "vegetarian", 10, "easy"),
                        new RecipeFilterDto("Pancakes", "vegetarian", 15, "easy"),
                        new RecipeFilterDto("Tofu Stir Fry", "vegan", 20, "easy"))));

        mockMvc.perform(get("/api/recipe/filter").param("difficulty", "easy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].difficulty").value("easy"))
                .andExpect(jsonPath("$.recipes[1].difficulty").value("easy"))
                .andExpect(jsonPath("$.recipes[2].difficulty").value("easy"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario Outline: User filters recipes by each supported difficulty level | medium")
    void testFilterRecipesByDifficultyMedium() throws Exception {
        when(recipeService.filterRecipes(1, null, null, "medium"))
                .thenReturn(new RecipeFilterResponseDto(List.of(
                        new RecipeFilterDto("Chicken Tikka Masala", "gluten-free", 45, "medium"),
                        new RecipeFilterDto("Mushroom Risotto", "vegan", 40, "medium"))));

        mockMvc.perform(get("/api/recipe/filter").param("difficulty", "medium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].difficulty").value("medium"))
                .andExpect(jsonPath("$.recipes[1].difficulty").value("medium"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario Outline: User filters recipes by each supported difficulty level | hard")
    void testFilterRecipesByDifficultyHard() throws Exception {
        when(recipeService.filterRecipes(1, null, null, "hard"))
                .thenReturn(new RecipeFilterResponseDto(List.of(
                        new RecipeFilterDto("Beef Wellington", "none", 120, "hard"),
                        new RecipeFilterDto("Lamb Tagine", "gluten-free", 90, "hard"))));

        mockMvc.perform(get("/api/recipe/filter").param("difficulty", "hard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].difficulty").value("hard"))
                .andExpect(jsonPath("$.recipes[1].difficulty").value("hard"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario Outline: User filters recipes by each supported dietary preference | vegan")
    void testFilterRecipesByDietaryTagVegan() throws Exception {
        when(recipeService.filterRecipes(1, "vegan", null, null))
                .thenReturn(new RecipeFilterResponseDto(List.of(
                        new RecipeFilterDto("Tofu Stir Fry", "vegan", 20, "easy"),
                        new RecipeFilterDto("Mushroom Risotto", "vegan", 40, "medium"))));

        mockMvc.perform(get("/api/recipe/filter").param("dietaryTag", "vegan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].dietaryTag").value("vegan"))
                .andExpect(jsonPath("$.recipes[1].dietaryTag").value("vegan"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario Outline: User filters recipes by each supported dietary preference | vegetarian")
    void testFilterRecipesByDietaryTagVegetarian() throws Exception {
        when(recipeService.filterRecipes(1, "vegetarian", null, null))
                .thenReturn(new RecipeFilterResponseDto(List.of(
                        new RecipeFilterDto("Omelette", "vegetarian", 10, "easy"),
                        new RecipeFilterDto("Pancakes", "vegetarian", 15, "easy"))));

        mockMvc.perform(get("/api/recipe/filter").param("dietaryTag", "vegetarian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].dietaryTag").value("vegetarian"))
                .andExpect(jsonPath("$.recipes[1].dietaryTag").value("vegetarian"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario Outline: User filters recipes by each supported dietary preference | gluten-free")
    void testFilterRecipesByDietaryTagGlutenFree() throws Exception {
        when(recipeService.filterRecipes(1, "gluten-free", null, null))
                .thenReturn(new RecipeFilterResponseDto(List.of(
                        new RecipeFilterDto("Chicken Tikka Masala", "gluten-free", 45, "medium"),
                        new RecipeFilterDto("Lamb Tagine", "gluten-free", 90, "hard"))));

        mockMvc.perform(get("/api/recipe/filter").param("dietaryTag", "gluten-free"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].dietaryTag").value("gluten-free"))
                .andExpect(jsonPath("$.recipes[1].dietaryTag").value("gluten-free"));
    }

    @Test
    @DisplayName("Feature: Filter Recipes By Preference, Time, Difficulty | Scenario: Unauthenticated user cannot filter recipes")
    @WithAnonymousUser
    void testFilterRecipesUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/recipe/filter").param("dietaryTag", "vegan"))
                .andExpect(status().isForbidden());
    }
}
