package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class DietTypesResponseDto {
    private List<String> dietNames;
    private List<Integer> dietIds;
    private List<List<Integer>> preferenceIdLists;

    public DietTypesResponseDto() {}

    public List<String> getDietName() {
        return dietNames;
    }

    public void setDietNames(List<String> dietNames) {
        this.dietNames = dietNames;
    }

    public List<Integer> getDietIds() {
        return dietIds;
    }

    public void setDietIds(List<Integer> dietIds) {
        this.dietIds = dietIds;
    }

    public List<List<Integer>> getPreferenceIdLists() {
        return preferenceIdLists;
    }

    public void setPreferenceIdLists(List<List<Integer>> preferenceIdLists) {
        this.preferenceIdLists = preferenceIdLists;
    }
}