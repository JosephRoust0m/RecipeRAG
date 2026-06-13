package com.ecse428.WaitForDinner.dto;

public class PantryUpsertItemRequestDto {
    private String name;
    private int quantity;
    private String unit;

    public PantryUpsertItemRequestDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
