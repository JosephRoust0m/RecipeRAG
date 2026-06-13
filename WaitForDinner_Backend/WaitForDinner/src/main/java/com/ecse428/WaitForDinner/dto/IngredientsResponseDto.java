package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class IngredientsResponseDto {
    private List<String> ingredientNames;
    private List<Integer> ids;

    public IngredientsResponseDto() {}

    public List<String> getIngredientNames() {
        return ingredientNames;
    }

    public void setIngredientNames(List<String> ingredientNames) {
        this.ingredientNames = ingredientNames;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setId(List<Integer> ids) {
        this.ids = ids;
    }
}
