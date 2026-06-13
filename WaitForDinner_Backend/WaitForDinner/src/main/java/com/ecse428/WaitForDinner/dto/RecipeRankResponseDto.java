package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class RecipeRankResponseDto {
    private List<RecipeRankDto> recipes;

    public RecipeRankResponseDto(List<RecipeRankDto> recipes) {
        this.recipes = recipes;
    }

    public List<RecipeRankDto> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeRankDto> recipes) {
        this.recipes = recipes;
    }
}
