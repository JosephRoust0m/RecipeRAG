package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class MissingIngredientsResponseDto {
    private List<String> missingIngredients;

    public MissingIngredientsResponseDto() {}

    public MissingIngredientsResponseDto(List<String> missingIngredients) {
        this.missingIngredients = missingIngredients;
    }

    public List<String> getMissingIngredients() { return missingIngredients; }
    public void setMissingIngredients(List<String> missingIngredients) { this.missingIngredients = missingIngredients; }
}
