package com.ecse428.WaitForDinner.dto;

import java.util.Date;

public class CookedRecipeDto {
    private int recipeId;
    private String name;
    private Date cookedAt;

    public CookedRecipeDto() {}

    public CookedRecipeDto(int recipeId, String name, Date cookedAt) {
        this.recipeId = recipeId;
        this.name = name;
        this.cookedAt = cookedAt;
    }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getCookedAt() { return cookedAt; }
    public void setCookedAt(Date cookedAt) { this.cookedAt = cookedAt; }
}
