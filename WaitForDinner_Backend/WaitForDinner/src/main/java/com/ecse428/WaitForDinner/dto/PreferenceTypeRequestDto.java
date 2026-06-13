package com.ecse428.WaitForDinner.dto;

public class PreferenceTypeRequestDto {
    private String name;
    private int id;

    public PreferenceTypeRequestDto() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
