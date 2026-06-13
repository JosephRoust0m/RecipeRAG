package com.ecse428.WaitForDinner.dto;

public class IngredientUserRequestDto {
    public String ingredientName;
    public int ingredientId;
    public enum Status { Like, Dislike, Allergic }
    public Status status;

    public IngredientUserRequestDto() {}

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
