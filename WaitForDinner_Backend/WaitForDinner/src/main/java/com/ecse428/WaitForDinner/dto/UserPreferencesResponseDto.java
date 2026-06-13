package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class UserPreferencesResponseDto {
    private List<String> preferenceTypeNames;
    private List<String> dietTypeNames;

    public UserPreferencesResponseDto(List<String> preferenceTypeNames, List<String> dietTypeNames) {
        this.preferenceTypeNames = preferenceTypeNames;
        this.dietTypeNames = dietTypeNames;
    }

    public List<String> getPreferenceTypeNames() { return preferenceTypeNames; }
    public List<String> getDietTypeNames() { return dietTypeNames; }
}
