package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class PreferenceUserResponseDto {
    private List<String> preferenceNames;
    private List<Integer> preferenceIds;

    public PreferenceUserResponseDto() {}

    public List<String> getPreferenceNames() {
        return preferenceNames;
    }

    public void setPreferenceNames(List<String> preferenceNames) {
        this.preferenceNames = preferenceNames;
    }

    public List<Integer> getPreferenceIds() {
        return preferenceIds;
    }

    public void setPreferenceIds(List<Integer> preferenceIds) {
        this.preferenceIds = preferenceIds;
    }
}
