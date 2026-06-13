package com.ecse428.WaitForDinner.feature;

import com.ecse428.WaitForDinner.dto.PantryHistoryEventDto;
import com.ecse428.WaitForDinner.dto.PantryHistoryResponseDto;
import com.ecse428.WaitForDinner.service.PantryService;
import com.ecse428.WaitForDinner.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Transactional
@WithMockUser
class ViewPantryHistoryFeatureTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PantryService pantryService;

    @MockBean
    private UserService userService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        when(userService.requireLoggedInUserId(any(HttpSession.class))).thenReturn(1);
    }

    @Test
    @DisplayName("Feature: User can view pantry history | Scenario: User views their full pantry history")
    void testGetPantryHistory() throws Exception {
        List<PantryHistoryEventDto> events = List.of(
            new PantryHistoryEventDto("Eggs", "added", 12, "unit", "2025-01-01"),
            new PantryHistoryEventDto("Milk", "added", 2, "litre", "2025-01-02"),
            new PantryHistoryEventDto("Eggs", "removed", 6, "unit", "2025-01-03"),
            new PantryHistoryEventDto("Flour", "added", 500, "gram", "2025-01-04"),
            new PantryHistoryEventDto("Milk", "removed", 2, "litre", "2025-01-05")
        );
        PantryHistoryResponseDto res = new PantryHistoryResponseDto(events);

        when(pantryService.getPantryHistory(10, 1, null, null, null)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/history")
                .param("pantryId", "10")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events").isArray())
            .andExpect(jsonPath("$.events.length()").value(5))
            .andExpect(jsonPath("$.events[0].item").value("Eggs"))
            .andExpect(jsonPath("$.events[0].action").value("added"))
            .andExpect(jsonPath("$.events[0].quantity").value(12))
            .andExpect(jsonPath("$.events[0].unit").value("unit"))
            .andExpect(jsonPath("$.events[0].date").value("2025-01-01"));
    }

    @Test
    @DisplayName("Feature: User can view pantry history | Scenario: User views pantry history filtered by item name")
    void testGetPantryHistoryFilteredByItemName() throws Exception {
        List<PantryHistoryEventDto> events = List.of(
            new PantryHistoryEventDto("Eggs", "added", 12, "unit", "2025-01-01"),
            new PantryHistoryEventDto("Eggs", "removed", 6, "unit", "2025-01-03")
        );
        PantryHistoryResponseDto res = new PantryHistoryResponseDto(events);

        when(pantryService.getPantryHistory(10, 1, "Eggs", null, null)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/history")
                .param("pantryId", "10")
                .param("itemName", "Eggs")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events").isArray())
            .andExpect(jsonPath("$.events.length()").value(2))
            .andExpect(jsonPath("$.events[0].item").value("Eggs"))
            .andExpect(jsonPath("$.events[1].item").value("Eggs"));
    }

    @Test
    @DisplayName("Feature: User can view pantry history | Scenario Outline: User filters pantry history by date range | 2025-01-01 to 2025-01-02")
    void testGetPantryHistoryFilteredByDateRange1() throws Exception {
        List<PantryHistoryEventDto> events = List.of(
            new PantryHistoryEventDto("Eggs", "added", 12, "unit", "2025-01-01"),
            new PantryHistoryEventDto("Milk", "added", 2, "litre", "2025-01-02")
        );
        PantryHistoryResponseDto res = new PantryHistoryResponseDto(events);

        when(pantryService.getPantryHistory(10, 1, null, "2025-01-01", "2025-01-02")).thenReturn(res);

        mockMvc.perform(get("/api/pantry/history")
                .param("pantryId", "10")
                .param("startDate", "2025-01-01")
                .param("endDate", "2025-01-02")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events").isArray())
            .andExpect(jsonPath("$.events.length()").value(2));
    }

    @Test
    @DisplayName("Feature: User can view pantry history | Scenario Outline: User filters pantry history by date range | 2025-01-03 to 2025-01-05")
    void testGetPantryHistoryFilteredByDateRange2() throws Exception {
        List<PantryHistoryEventDto> events = List.of(
            new PantryHistoryEventDto("Eggs", "removed", 6, "unit", "2025-01-03"),
            new PantryHistoryEventDto("Flour", "added", 500, "gram", "2025-01-04"),
            new PantryHistoryEventDto("Milk", "removed", 2, "litre", "2025-01-05")
        );
        PantryHistoryResponseDto res = new PantryHistoryResponseDto(events);

        when(pantryService.getPantryHistory(10, 1, null, "2025-01-03", "2025-01-05")).thenReturn(res);

        mockMvc.perform(get("/api/pantry/history")
                .param("pantryId", "10")
                .param("startDate", "2025-01-03")
                .param("endDate", "2025-01-05")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events").isArray())
            .andExpect(jsonPath("$.events.length()").value(3));
    }

    @Test
    @DisplayName("Feature: User can view pantry history | Scenario Outline: User filters pantry history by date range | 2025-01-02 to 2025-01-04")
    void testGetPantryHistoryFilteredByDateRange3() throws Exception {
        List<PantryHistoryEventDto> events = List.of(
            new PantryHistoryEventDto("Milk", "added", 2, "litre", "2025-01-02"),
            new PantryHistoryEventDto("Eggs", "removed", 6, "unit", "2025-01-03"),
            new PantryHistoryEventDto("Flour", "added", 500, "gram", "2025-01-04")
        );
        PantryHistoryResponseDto res = new PantryHistoryResponseDto(events);

        when(pantryService.getPantryHistory(10, 1, null, "2025-01-02", "2025-01-04")).thenReturn(res);

        mockMvc.perform(get("/api/pantry/history")
                .param("pantryId", "10")
                .param("startDate", "2025-01-02")
                .param("endDate", "2025-01-04")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events").isArray())
            .andExpect(jsonPath("$.events.length()").value(3));
    }

    @Test
    @DisplayName("Feature: User can view pantry history | Scenario: User with no pantry history receives an empty history")
    void testGetPantryHistoryEmpty() throws Exception {
        PantryHistoryResponseDto res = new PantryHistoryResponseDto(List.of());

        when(pantryService.getPantryHistory(10, 1, null, null, null)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/history")
                .param("pantryId", "10")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.events").isArray())
            .andExpect(jsonPath("$.events.length()").value(0));
    }

    @Test
    @DisplayName("Feature: User can view pantry history | Scenario: Unauthenticated user cannot view pantry history")
    @WithAnonymousUser
    void testGetPantryHistoryUnauthorized() throws Exception {
        mockMvc.perform(get("/api/pantry/history").param("pantryId", "10"))
            .andExpect(status().isForbidden());
    }
}
