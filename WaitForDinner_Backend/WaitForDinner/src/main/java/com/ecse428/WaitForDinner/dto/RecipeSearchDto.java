package com.ecse428.WaitForDinner.dto;

public class RecipeSearchDto {
    private int recipeId;
    private String name;
    private String dietaryTag;
    private String cuisine;
    private int time;
    private String difficulty;
    private int matchCount;
    private boolean fullyCookable;

    public RecipeSearchDto() {}

    public RecipeSearchDto(int recipeId, String name, String dietaryTag, String cuisine,
                           int time, String difficulty, int matchCount, boolean fullyCookable) {
        this.recipeId = recipeId;
        this.name = name;
        this.dietaryTag = dietaryTag;
        this.cuisine = cuisine;
        this.time = time;
        this.difficulty = difficulty;
        this.matchCount = matchCount;
        this.fullyCookable = fullyCookable;
    }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDietaryTag() { return dietaryTag; }
    public void setDietaryTag(String dietaryTag) { this.dietaryTag = dietaryTag; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }
    public boolean isFullyCookable() { return fullyCookable; }
    public void setFullyCookable(boolean fullyCookable) { this.fullyCookable = fullyCookable; }
}
