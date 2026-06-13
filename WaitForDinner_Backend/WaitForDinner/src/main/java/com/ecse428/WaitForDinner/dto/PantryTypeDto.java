package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class PantryTypeDto {
    private String name;
    private List<Integer> ingredientIds;
    private List<String> ingredientNames;

    public PantryTypeDto(String name, List<Integer> ingredientIds, List<String> ingredientNames) {
        this.name = name;
        this.ingredientIds = ingredientIds;
        this.ingredientNames = ingredientNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(List<Integer> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public List<String> getIngredientNames() {
        return ingredientNames;
    }

    public void setIngredientNames(List<String> ingredientNames) {
        this.ingredientNames = ingredientNames;
    }
}

