package com.ecse428.WaitForDinner.dto;

import java.util.List;

public class PantryHistoryResponseDto {
    private List<PantryHistoryEventDto> events;

    public PantryHistoryResponseDto(List<PantryHistoryEventDto> events) {
        this.events = events;
    }

    public List<PantryHistoryEventDto> getEvents() {
        return events;
    }

    public void setEvents(List<PantryHistoryEventDto> events) {
        this.events = events;
    }
}
