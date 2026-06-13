package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class CookedRecipesResponseDto {
    private List<CookedRecipeDto> cookedRecipes;

    public CookedRecipesResponseDto() {}

    public CookedRecipesResponseDto(List<CookedRecipeDto> cookedRecipes) {
        this.cookedRecipes = cookedRecipes;
    }

    public List<CookedRecipeDto> getCookedRecipes() { return cookedRecipes; }
    public void setCookedRecipes(List<CookedRecipeDto> cookedRecipes) { this.cookedRecipes = cookedRecipes; }
}
