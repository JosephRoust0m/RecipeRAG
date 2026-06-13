package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class DietUserResponseDto {
    private List<String> dietNames;
    private List<Integer> dietTypeIds;

    public DietUserResponseDto() {}

    public List<String> getDietNames() {
        return dietNames;
    }

    public void setDietNames(List<String> dietNames) {
        this.dietNames = dietNames;
    }

    public List<Integer> getDietTypeIds() {
        return dietTypeIds;
    }

    public void setDietTypeIds(List<Integer> dietTypeIds) {
        this.dietTypeIds = dietTypeIds;
    }
}