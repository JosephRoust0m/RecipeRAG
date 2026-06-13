package com.ecse428.WaitForDinner.dto;

public class PantryHistoryEventDto {
    private String item;
    private String action;
    private int quantity;
    private String unit;
    private String date;

    public PantryHistoryEventDto(String item, String action, int quantity, String unit, String date) {
        this.item = item;
        this.action = action;
        this.quantity = quantity;
        this.unit = unit;
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
