package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class DietTypeRequestDto {
    private String dietName;
    private int id;
    private List<Integer> preferenceIds;

    public DietTypeRequestDto() {}

    public String getDietName() {
        return dietName;
    }

    public void setDietName(String dietName) {
        this.dietName = dietName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getPreferenceIds() {
        return preferenceIds;
    }

    public void setPreferenceIds(List<Integer> preferenceIds) {
        this.preferenceIds = preferenceIds;
    }
}
