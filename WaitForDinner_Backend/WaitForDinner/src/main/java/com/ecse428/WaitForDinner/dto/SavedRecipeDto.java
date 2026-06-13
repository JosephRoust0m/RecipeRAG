package com.ecse428.WaitForDinner.dto;

public class SavedRecipeDto {
    private int recipeId;
    private String name;
    private String dietaryTag;
    private String cuisine;

    public SavedRecipeDto() {}

    public SavedRecipeDto(int recipeId, String name, String dietaryTag, String cuisine) {
        this.recipeId = recipeId;
        this.name = name;
        this.dietaryTag = dietaryTag;
        this.cuisine = cuisine;
    }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDietaryTag() { return dietaryTag; }
    public void setDietaryTag(String dietaryTag) { this.dietaryTag = dietaryTag; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
}
