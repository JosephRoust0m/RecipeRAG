package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class DietTypeResponseDto {
    private String dietName;
    private List<Integer> preferenceIds;

    public DietTypeResponseDto() {}

    public String getDietName() {
        return dietName;
    }

    public void setDietName(String dietName) {
        this.dietName = dietName;
    }

    public List<Integer> getPreferenceIds() {
        return preferenceIds;
    }

    public void setPreferenceIds(List<Integer> preferenceIds) {
        this.preferenceIds = preferenceIds;
    }
}
