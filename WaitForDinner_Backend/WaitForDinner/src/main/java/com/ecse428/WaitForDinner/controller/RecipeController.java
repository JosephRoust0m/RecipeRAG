package com.ecse428.WaitForDinner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecse428.WaitForDinner.dto.CookedRecipesResponseDto;
import com.ecse428.WaitForDinner.dto.MissingIngredientsResponseDto;
import com.ecse428.WaitForDinner.dto.RecipeDetailDto;
import com.ecse428.WaitForDinner.dto.RecipeFilterResponseDto;
import com.ecse428.WaitForDinner.dto.RecipePublicDto;
import com.ecse428.WaitForDinner.dto.RecipeRankResponseDto;
import com.ecse428.WaitForDinner.dto.RecipeSearchResponseDto;
import com.ecse428.WaitForDinner.dto.SavedRecipesResponseDto;
import com.ecse428.WaitForDinner.service.RecipeService;
import com.ecse428.WaitForDinner.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final UserService userService;

    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    @GetMapping("/byName")
    public ResponseEntity<RecipePublicDto> getRecipeByName(@RequestParam String name) {
        return ResponseEntity.ok(recipeService.getRecipeByName(name));
    }

    @GetMapping("/ranked")
    public ResponseEntity<RecipeRankResponseDto> getRankedRecipes(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.rankRecipes(userId));
    }

    @GetMapping("/valid")
    public ResponseEntity<RecipeSearchResponseDto> getValidRecipes(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.getValidRecipes(userId));
    }

    @GetMapping("/filter")
    public ResponseEntity<RecipeFilterResponseDto> filterRecipes(
            @RequestParam(required = false) String dietaryTag,
            @RequestParam(required = false) Integer maxTime,
            @RequestParam(required = false) String difficulty,
            HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.filterRecipes(userId, dietaryTag, maxTime, difficulty));
    }

    @GetMapping("/search")
    public ResponseEntity<RecipeSearchResponseDto> searchRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String dietaryTag,
            HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.searchRecipes(userId, keyword, cuisine, dietaryTag));
    }

    @GetMapping("/saved")
    public ResponseEntity<SavedRecipesResponseDto> getSavedRecipes(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.getSavedRecipes(userId));
    }

    @GetMapping("/cooked")
    public ResponseEntity<CookedRecipesResponseDto> getCookedRecipes(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.getCookedRecipes(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailDto> getRecipeDetail(@PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.getRecipeDetail(id, userId));
    }

    @GetMapping("/{id}/missing-ingredients")
    public ResponseEntity<MissingIngredientsResponseDto> getMissingIngredients(
            @PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(recipeService.getMissingIngredients(id, userId));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> saveRecipe(@PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        recipeService.saveRecipe(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/save")
    public ResponseEntity<Void> unsaveRecipe(@PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        recipeService.unsaveRecipe(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cook")
    public ResponseEntity<Void> cookRecipe(@PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        recipeService.cookRecipe(id, userId);
        return ResponseEntity.ok().build();
    }
}
