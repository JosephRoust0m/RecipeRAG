package com.ecse428.WaitForDinner.feature;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Transactional
@WithMockUser
class DeletePantryItemsFeatureTests {

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
    @DisplayName("Feature: Delete Pantry Items | Scenario: Successfully delete a single pantry item")
    void testDeletePantryItemSuccess() throws Exception {
        mockMvc.perform(delete("/api/pantry/items/{name}", "Eggs").session(session))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Feature: Delete Pantry Items | Scenario: Delete an item that does not exist in the pantry")
    void testDeletePantryItemNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"))
            .when(pantryService).deletePantryItemByName("Butter", 1);

        mockMvc.perform(delete("/api/pantry/items/{name}", "Butter").session(session))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Feature: Delete Pantry Items | Scenario Outline: Delete multiple different pantry items | Eggs")
    void testDeletePantryItemEggs() throws Exception {
        mockMvc.perform(delete("/api/pantry/items/{name}", "Eggs").session(session))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Feature: Delete Pantry Items | Scenario Outline: Delete multiple different pantry items | Milk")
    void testDeletePantryItemMilk() throws Exception {
        mockMvc.perform(delete("/api/pantry/items/{name}", "Milk").session(session))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Feature: Delete Pantry Items | Scenario Outline: Delete multiple different pantry items | Flour")
    void testDeletePantryItemFlour() throws Exception {
        mockMvc.perform(delete("/api/pantry/items/{name}", "Flour").session(session))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Feature: Delete Pantry Items | Scenario: Delete all pantry items one by one")
    void testDeleteAllPantryItemsOneByOne() throws Exception {
        mockMvc.perform(delete("/api/pantry/items/{name}", "Eggs").session(session))
            .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/pantry/items/{name}", "Milk").session(session))
            .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/pantry/items/{name}", "Flour").session(session))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Feature: Delete Pantry Items | Scenario: Unauthenticated user cannot delete a pantry item")
    void testDeletePantryItemUnauthorized() throws Exception {
        when(userService.requireLoggedInUserId(any(HttpSession.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in"));

        mockMvc.perform(delete("/api/pantry/items/{name}", "Eggs").session(new MockHttpSession()))
            .andExpect(status().isUnauthorized());
    }
}
