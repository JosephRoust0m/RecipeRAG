package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class PreferenceUserRequestDto {
    private List<Integer> preferenceIds;

    public PreferenceUserRequestDto() {}

    public List<Integer> getPreferenceIds() {
        return preferenceIds;
    }

    public void setPreferenceIds(List<Integer> preferenceIds) {
        this.preferenceIds = preferenceIds;
    }
}
