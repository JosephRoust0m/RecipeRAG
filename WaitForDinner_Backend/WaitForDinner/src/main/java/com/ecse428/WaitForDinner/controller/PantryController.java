package com.ecse428.WaitForDinner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecse428.WaitForDinner.dto.PantryHistoryResponseDto;
import com.ecse428.WaitForDinner.dto.PantryIngredientAmountRequestDto;
import com.ecse428.WaitForDinner.dto.PantryIngredientRequestDto;
import com.ecse428.WaitForDinner.dto.PantryItemDto;
import com.ecse428.WaitForDinner.dto.PantryItemsResponseDto;
import com.ecse428.WaitForDinner.dto.PantryTypeResponseDto;
import com.ecse428.WaitForDinner.dto.PantryUpsertItemRequestDto;
import com.ecse428.WaitForDinner.service.PantryService;
import com.ecse428.WaitForDinner.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/pantry")
public class PantryController {
    public final PantryService pantryService;
    public final UserService userService;

    public PantryController(PantryService pantryService, UserService userService) {
        this.pantryService = pantryService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PantryTypeResponseDto> getPantryTypeById(@PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.getPantryTypeById(id, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<PantryTypeResponseDto> getAllPantryTypes(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.getAllPantries(userId));
    }

    @PostMapping("/{name}")
    public ResponseEntity<PantryTypeResponseDto> createPantry(@PathVariable String name, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.createPantry(name, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePantry(@PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        this.pantryService.deletePantry(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/addIngredient")
    public ResponseEntity<PantryTypeResponseDto> addIngredient(@RequestBody PantryIngredientAmountRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.addIngredient(req, userId));
    }

    @PostMapping("/removeIngredient")
    public ResponseEntity<PantryTypeResponseDto> removeIngredient(@RequestBody PantryIngredientRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.removeIngredient(req, userId));
    }

    @PostMapping("/removeIngredientAmount")
    public ResponseEntity<PantryTypeResponseDto> removeIngredientAmount(@RequestBody PantryIngredientAmountRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.removeIngredientAmount(req, userId));
    }

    @GetMapping("/history")
    public ResponseEntity<PantryHistoryResponseDto> getPantryHistory(
        @RequestParam int pantryId,
        @RequestParam(required = false) String itemName,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        HttpSession session
    ) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.getPantryHistory(pantryId, userId, itemName, startDate, endDate));
    }

    @GetMapping("/items")
    public ResponseEntity<PantryItemsResponseDto> getPantryItems(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.getPantryItems(userId));
    }

    @GetMapping("/items/{name}")
    public ResponseEntity<PantryItemDto> getPantryItemByName(@PathVariable String name, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.getPantryItemByName(name, userId));
    }

    @DeleteMapping("/items/{name}")
    public ResponseEntity<Void> deletePantryItemByName(@PathVariable String name, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        this.pantryService.deletePantryItemByName(name, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/item")
    public ResponseEntity<PantryItemsResponseDto> upsertPantryItem(
            @RequestBody PantryUpsertItemRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.pantryService.upsertPantryItem(userId, req.getName(), req.getQuantity()));
    }
}