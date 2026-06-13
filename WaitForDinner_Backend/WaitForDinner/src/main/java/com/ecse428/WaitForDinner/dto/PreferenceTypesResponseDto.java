package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class PreferenceTypesResponseDto {
    private List<String> names;
    private List<Integer> ids;

    public PreferenceTypesResponseDto() {}

    public List<String> getName() {
        return names;
    }

    public void setName(List<String> names) {
        this.names = names;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setId(List<Integer> ids) {
        this.ids = ids;
    }
}