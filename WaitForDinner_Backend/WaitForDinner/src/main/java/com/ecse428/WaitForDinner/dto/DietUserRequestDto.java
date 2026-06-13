package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class DietUserRequestDto {
    private List<Integer> dietTypeIds;

    public DietUserRequestDto() {}

    public List<Integer> getDietTypeIds() {
        return dietTypeIds;
    }

    public void setDietTypeIds(List<Integer> dietTypeIds) {
        this.dietTypeIds = dietTypeIds;
    }
}
