package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class RecipeFilterResponseDto {
    private List<RecipeFilterDto> recipes;

    public RecipeFilterResponseDto(List<RecipeFilterDto> recipes) {
        this.recipes = recipes;
    }

    public List<RecipeFilterDto> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<RecipeFilterDto> recipes) {
        this.recipes = recipes;
    }
}
