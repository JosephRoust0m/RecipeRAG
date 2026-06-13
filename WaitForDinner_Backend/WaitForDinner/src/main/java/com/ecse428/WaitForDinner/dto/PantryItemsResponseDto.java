package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class PantryItemsResponseDto {
  private List<PantryItemDto> items;

  public PantryItemsResponseDto() {
  }

  public PantryItemsResponseDto(List<PantryItemDto> items) {
    this.items = items;
  }

  public List<PantryItemDto> getItems() {
    return items;
  }

  public void setItems(List<PantryItemDto> items) {
    this.items = items;
  }
}