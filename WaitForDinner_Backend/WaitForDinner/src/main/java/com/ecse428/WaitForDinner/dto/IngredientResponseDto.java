package com.ecse428.WaitForDinner.dto;

public class IngredientResponseDto {
    private String ingredientName;
    private int id;

    public IngredientResponseDto() {}

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
