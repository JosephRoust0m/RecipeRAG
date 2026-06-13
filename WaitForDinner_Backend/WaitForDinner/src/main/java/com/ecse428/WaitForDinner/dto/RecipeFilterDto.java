package com.ecse428.WaitForDinner.dto;

public class RecipeFilterDto {
    private String name;
    private String dietaryTag;
    private int cookingTime;
    private String difficulty;

    public RecipeFilterDto(String name, String dietaryTag, int cookingTime, String difficulty) {
        this.name = name;
        this.dietaryTag = dietaryTag;
        this.cookingTime = cookingTime;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDietaryTag() {
        return dietaryTag;
    }

    public void setDietaryTag(String dietaryTag) {
        this.dietaryTag = dietaryTag;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
