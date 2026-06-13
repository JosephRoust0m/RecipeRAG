package com.ecse428.WaitForDinner.dto;

public class RecipeRankDto {
    private String name;
    private int matchCount;
    private boolean fullyCookable;
    private boolean dietaryMatch;

    public RecipeRankDto(String name, int matchCount, boolean fullyCookable, boolean dietaryMatch) {
        this.name = name;
        this.matchCount = matchCount;
        this.fullyCookable = fullyCookable;
        this.dietaryMatch = dietaryMatch;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    public boolean isFullyCookable() {
        return fullyCookable;
    }

    public void setFullyCookable(boolean fullyCookable) {
        this.fullyCookable = fullyCookable;
    }

    public boolean isDietaryMatch() {
        return dietaryMatch;
    }

    public void setDietaryMatch(boolean dietaryMatch) {
        this.dietaryMatch = dietaryMatch;
    }
}
