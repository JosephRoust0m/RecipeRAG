package com.ecse428.WaitForDinner.dto;

public class PantryIngredientAmountRequestDto {
    private int pantryId;
    private int ingredientId;
    private String ingredientName;
    private int amount;

    public PantryIngredientAmountRequestDto() {}

    public PantryIngredientAmountRequestDto(int pantryId, int ingredientId, int amount) {
        this.pantryId = pantryId;
        this.ingredientId = ingredientId;
        this.amount = amount;
    }

    public PantryIngredientAmountRequestDto(int pantryId, int ingredientId, String ingredientName, int amount) {
        this.pantryId = pantryId;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
