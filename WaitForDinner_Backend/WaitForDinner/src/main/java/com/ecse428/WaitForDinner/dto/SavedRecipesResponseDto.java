package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class SavedRecipesResponseDto {
    private List<SavedRecipeDto> savedRecipes;

    public SavedRecipesResponseDto() {}

    public SavedRecipesResponseDto(List<SavedRecipeDto> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }

    public List<SavedRecipeDto> getSavedRecipes() { return savedRecipes; }
    public void setSavedRecipes(List<SavedRecipeDto> savedRecipes) { this.savedRecipes = savedRecipes; }
}
