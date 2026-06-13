package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class RecipeSearchResponseDto {
    private List<RecipeSearchDto> results;

    public RecipeSearchResponseDto() {}

    public RecipeSearchResponseDto(List<RecipeSearchDto> results) {
        this.results = results;
    }

    public List<RecipeSearchDto> getResults() { return results; }
    public void setResults(List<RecipeSearchDto> results) { this.results = results; }
}
