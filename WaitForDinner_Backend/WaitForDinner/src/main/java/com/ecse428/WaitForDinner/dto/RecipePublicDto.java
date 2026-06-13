package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class RecipePublicDto {
    private int recipeId;
    private String name;
    private String difficulty;
    private int time;
    private int portions;
    private List<String> ingredients;
    private List<String> dietaryTags;
    private List<String> cuisines;
    private List<String> steps;

    public RecipePublicDto() {}

    public RecipePublicDto(int recipeId, String name, String difficulty, int time, int portions,
                           List<String> ingredients, List<String> dietaryTags,
                           List<String> cuisines, List<String> steps) {
        this.recipeId = recipeId;
        this.name = name;
        this.difficulty = difficulty;
        this.time = time;
        this.portions = portions;
        this.ingredients = ingredients;
        this.dietaryTags = dietaryTags;
        this.cuisines = cuisines;
        this.steps = steps;
    }

    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }
    public int getPortions() { return portions; }
    public void setPortions(int portions) { this.portions = portions; }
    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    public List<String> getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(List<String> dietaryTags) { this.dietaryTags = dietaryTags; }
    public List<String> getCuisines() { return cuisines; }
    public void setCuisines(List<String> cuisines) { this.cuisines = cuisines; }
    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }
}
