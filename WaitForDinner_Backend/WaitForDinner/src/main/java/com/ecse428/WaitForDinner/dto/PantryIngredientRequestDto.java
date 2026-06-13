package com.ecse428.WaitForDinner.dto;

public class PantryIngredientRequestDto {
    private int pantryId;
    private int ingredientId;
    private String ingredientName;

    public PantryIngredientRequestDto(int pantryId, int ingredientId, String ingredientName) {
        this.pantryId = pantryId;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
    }

    public int getPantryId() {
        return pantryId;
    }

    public void setPantryId(int pantryId) {
        this.pantryId = pantryId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}
