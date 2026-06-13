package com.ecse428.WaitForDinner.feature;

import com.ecse428.WaitForDinner.dto.PantryItemDto;
import com.ecse428.WaitForDinner.dto.PantryItemsResponseDto;
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
class ViewPantryItemsFeatureTests {

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
    @DisplayName("Feature: User can view pantry items | Scenario: User views all items in their pantry")
    void testGetPantryItems() throws Exception {
        PantryItemsResponseDto res = new PantryItemsResponseDto(List.of(
            new PantryItemDto("Butter", 200, "gram"),
            new PantryItemDto("Eggs", 12, "unit"),
            new PantryItemDto("Flour", 500, "gram"),
            new PantryItemDto("Milk", 2, "litre")
        ));

        when(pantryService.getPantryItems(1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(4))
            .andExpect(jsonPath("$.items[0].name").exists())
            .andExpect(jsonPath("$.items[0].quantity").exists())
            .andExpect(jsonPath("$.items[0].unit").exists());
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario: Pantry items are returned in alphabetical order by name")
    void testGetPantryItemsAlphabeticalOrder() throws Exception {
        PantryItemsResponseDto res = new PantryItemsResponseDto(List.of(
            new PantryItemDto("Butter", 200, "gram"),
            new PantryItemDto("Eggs", 12, "unit"),
            new PantryItemDto("Flour", 500, "gram"),
            new PantryItemDto("Milk", 2, "litre")
        ));

        when(pantryService.getPantryItems(1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].name").value("Butter"))
            .andExpect(jsonPath("$.items[3].name").value("Milk"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario: User views a specific pantry item by name")
    void testGetPantryItemByName() throws Exception {
        PantryItemDto res = new PantryItemDto("Eggs", 12, "unit");

        when(pantryService.getPantryItemByName("Eggs", 1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items/{name}", "Eggs").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Eggs"))
            .andExpect(jsonPath("$.quantity").value(12))
            .andExpect(jsonPath("$.unit").value("unit"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario: User requests a pantry item that does not exist")
    void testGetPantryItemByNameNotFound() throws Exception {
        when(pantryService.getPantryItemByName("Olive Oil", 1))
            .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        mockMvc.perform(get("/api/pantry/items/{name}", "Olive Oil").session(session))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario: User with an empty pantry receives an empty list")
    void testGetPantryItemsEmpty() throws Exception {
        PantryItemsResponseDto res = new PantryItemsResponseDto(List.of());

        when(pantryService.getPantryItems(1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items").isArray())
            .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario: Pantry list reflects items added since last viewed")
    void testGetPantryItemsReflectsAddedItem() throws Exception {
        PantryItemsResponseDto res = new PantryItemsResponseDto(List.of(
            new PantryItemDto("Butter", 200, "gram"),
            new PantryItemDto("Eggs", 12, "unit"),
            new PantryItemDto("Flour", 500, "gram"),
            new PantryItemDto("Milk", 2, "litre"),
            new PantryItemDto("Olive Oil", 250, "ml")
        ));

        when(pantryService.getPantryItems(1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(5))
            .andExpect(jsonPath("$.items[4].name").value("Olive Oil"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario: Pantry list reflects items removed since last viewed")
    void testGetPantryItemsReflectsRemovedItem() throws Exception {
        PantryItemsResponseDto res = new PantryItemsResponseDto(List.of(
            new PantryItemDto("Butter", 200, "gram"),
            new PantryItemDto("Eggs", 12, "unit"),
            new PantryItemDto("Flour", 500, "gram")
        ));

        when(pantryService.getPantryItems(1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items.length()").value(3))
            .andExpect(jsonPath("$.items[0].name").value("Butter"))
            .andExpect(jsonPath("$.items[1].name").value("Eggs"))
            .andExpect(jsonPath("$.items[2].name").value("Flour"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario Outline: User views details of each pantry item individually | Eggs")
    void testGetPantryItemEggs() throws Exception {
        PantryItemDto res = new PantryItemDto("Eggs", 12, "unit");

        when(pantryService.getPantryItemByName("Eggs", 1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items/{name}", "Eggs").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Eggs"))
            .andExpect(jsonPath("$.quantity").value(12))
            .andExpect(jsonPath("$.unit").value("unit"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario Outline: User views details of each pantry item individually | Milk")
    void testGetPantryItemMilk() throws Exception {
        PantryItemDto res = new PantryItemDto("Milk", 2, "litre");

        when(pantryService.getPantryItemByName("Milk", 1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items/{name}", "Milk").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Milk"))
            .andExpect(jsonPath("$.quantity").value(2))
            .andExpect(jsonPath("$.unit").value("litre"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario Outline: User views details of each pantry item individually | Flour")
    void testGetPantryItemFlour() throws Exception {
        PantryItemDto res = new PantryItemDto("Flour", 500, "gram");

        when(pantryService.getPantryItemByName("Flour", 1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items/{name}", "Flour").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Flour"))
            .andExpect(jsonPath("$.quantity").value(500))
            .andExpect(jsonPath("$.unit").value("gram"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario Outline: User views details of each pantry item individually | Butter")
    void testGetPantryItemButter() throws Exception {
        PantryItemDto res = new PantryItemDto("Butter", 200, "gram");

        when(pantryService.getPantryItemByName("Butter", 1)).thenReturn(res);

        mockMvc.perform(get("/api/pantry/items/{name}", "Butter").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Butter"))
            .andExpect(jsonPath("$.quantity").value(200))
            .andExpect(jsonPath("$.unit").value("gram"));
    }

    @Test
    @DisplayName("Feature: User can view pantry items | Scenario: Unauthenticated user cannot view pantry items")
    void testGetPantryItemsUnauthorized() throws Exception {
        when(userService.requireLoggedInUserId(any(HttpSession.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in"));

        mockMvc.perform(get("/api/pantry/items").session(new MockHttpSession()))
            .andExpect(status().isUnauthorized());
    }
}
