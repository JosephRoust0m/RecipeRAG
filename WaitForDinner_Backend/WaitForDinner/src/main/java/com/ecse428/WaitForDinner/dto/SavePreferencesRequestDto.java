package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class SavePreferencesRequestDto {
    private List<String> preferenceTypeNames;
    private List<String> dietTypeNames;

    public List<String> getPreferenceTypeNames() { return preferenceTypeNames; }
    public void setPreferenceTypeNames(List<String> preferenceTypeNames) { this.preferenceTypeNames = preferenceTypeNames; }

    public List<String> getDietTypeNames() { return dietTypeNames; }
    public void setDietTypeNames(List<String> dietTypeNames) { this.dietTypeNames = dietTypeNames; }
}
