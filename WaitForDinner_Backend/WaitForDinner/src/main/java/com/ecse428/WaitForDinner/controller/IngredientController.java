package com.ecse428.WaitForDinner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecse428.WaitForDinner.dto.IngredientResponseDto;
import com.ecse428.WaitForDinner.dto.IngredientUserRequestDto;
import com.ecse428.WaitForDinner.dto.IngredientUserResponseDto;
import com.ecse428.WaitForDinner.dto.IngredientsResponseDto;
import com.ecse428.WaitForDinner.service.IngredientService;
import com.ecse428.WaitForDinner.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientController {
    private final UserService userService;
    private final IngredientService ingredientService;
    
    public IngredientController(UserService userService, IngredientService ingredientService) {
        this.userService = userService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("")
    public ResponseEntity<IngredientsResponseDto> getAllIngredients() {
        return ResponseEntity.ok(this.ingredientService.getAllIngredients());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<IngredientResponseDto> getIngredientById(@PathVariable int id) {
        return ResponseEntity.ok(this.ingredientService.getIngredientById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<IngredientResponseDto> getIngredientByName(@PathVariable String name) {
        return ResponseEntity.ok(this.ingredientService.getIngredientByName(name));
    }

    @GetMapping("/user/current")
    public ResponseEntity<IngredientUserResponseDto> getCurrentUserIngredients(HttpSession session) {
        int userID = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.ingredientService.getIngredientPreferences(userID));
    }

    @PostMapping("/user/add")
    public ResponseEntity<IngredientUserResponseDto> addUserIngredient(@RequestBody IngredientUserRequestDto req, HttpSession session) {
        int userID = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.ingredientService.addIngredientPreference(req, userID));
    }

    @PostMapping("/user/remove")
    public ResponseEntity<IngredientUserResponseDto> removeUserIngredient(@RequestBody IngredientUserRequestDto req, HttpSession session) {
        int userID = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.ingredientService.removeIngredientPreference(req, userID));
    }
}
