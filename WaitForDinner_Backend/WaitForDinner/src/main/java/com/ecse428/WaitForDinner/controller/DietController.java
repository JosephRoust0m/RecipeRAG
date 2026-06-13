package com.ecse428.WaitForDinner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecse428.WaitForDinner.dto.DietTypeResponseDto;
import com.ecse428.WaitForDinner.dto.DietTypesResponseDto;
import com.ecse428.WaitForDinner.dto.DietUserRequestDto;
import com.ecse428.WaitForDinner.dto.DietUserResponseDto;
import com.ecse428.WaitForDinner.service.DietService;
import com.ecse428.WaitForDinner.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/diet")
public class DietController {
    private final UserService userService;
    private final DietService dietService;

    public DietController(UserService userService, DietService dietService) {
        this.userService = userService;
        this.dietService = dietService;
    }

    @GetMapping("")
    public ResponseEntity<DietTypesResponseDto> getAllDiets() {
        return ResponseEntity.ok(this.dietService.getAllDiets());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DietTypeResponseDto> getDietById(@PathVariable int id) {
        return ResponseEntity.ok(this.dietService.getDietById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DietTypeResponseDto> getDietByName(@PathVariable String name) {
        return ResponseEntity.ok(this.dietService.getDietByName(name));
    }

    @GetMapping("/current")
    public ResponseEntity<DietUserResponseDto> getCurrentDiets(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.dietService.getCurrentDiets(userId));
    }

    @PostMapping("/adopt")
    public ResponseEntity<DietUserResponseDto> adoptDiet(@RequestBody DietUserRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.dietService.adoptDiet(req, userId));
    }

    @PostMapping("/quit")
    public ResponseEntity<DietUserResponseDto> quitDiet(@RequestBody DietUserRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.dietService.quitDiet(req, userId));
    }
}
