package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class PantryTypeResponseDto {
    private List<PantryTypeDto> pantries;

    public PantryTypeResponseDto(List<PantryTypeDto> pantries) {
        this.pantries = pantries;
    }

    public List<PantryTypeDto> getPantries() {
        return pantries;
    }

    public void setPantries(List<PantryTypeDto> pantries) {
        this.pantries = pantries;
    }
}
