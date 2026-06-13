package com.ecse428.WaitForDinner.dto;

public class PreferenceTypeDto {
    private int id;
    private String name;

    public PreferenceTypeDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}
