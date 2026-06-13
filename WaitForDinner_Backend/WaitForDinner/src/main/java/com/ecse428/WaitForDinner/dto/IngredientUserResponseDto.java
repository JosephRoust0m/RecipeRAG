package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class IngredientUserResponseDto {
    public List<String> ingredientNames;
    public List<Integer> ingredientIds;
    public List<String> statuses;

    public IngredientUserResponseDto() {}

    public List<String> getIngredientNames() {
        return ingredientNames;
    }

    public void setIngredientNames(List<String> ingredientNames) {
        this.ingredientNames = ingredientNames;
    }

    public List<Integer> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(List<Integer> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }
}
