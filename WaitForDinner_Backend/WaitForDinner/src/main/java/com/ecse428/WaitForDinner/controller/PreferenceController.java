package com.ecse428.WaitForDinner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecse428.WaitForDinner.dto.PreferenceTypeResponseDto;
import com.ecse428.WaitForDinner.dto.PreferenceTypesResponseDto;
import com.ecse428.WaitForDinner.dto.PreferenceUserRequestDto;
import com.ecse428.WaitForDinner.dto.PreferenceUserResponseDto;
import com.ecse428.WaitForDinner.service.PreferenceService;
import com.ecse428.WaitForDinner.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/preference")
public class PreferenceController {
    private final UserService userService;
    private final PreferenceService preferenceService;

    public PreferenceController(UserService userService, PreferenceService preferenceService) {
        this.userService = userService;
        this.preferenceService = preferenceService;
    }

    @GetMapping("")
    public ResponseEntity<PreferenceTypesResponseDto> getAllPreferenceTypes() {
        return ResponseEntity.ok(this.preferenceService.getAllPreferenceTypes());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PreferenceTypeResponseDto> getPreferenceById(@PathVariable int id) {
        return ResponseEntity.ok(this.preferenceService.getPreferenceTypeById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PreferenceTypeResponseDto> getPreferenceByName(@PathVariable String name) {
        return ResponseEntity.ok(this.preferenceService.getPreferenceTypeByName(name));
    }

    @GetMapping("/user/current")
    public ResponseEntity<PreferenceUserResponseDto> getCurrentPreferences(HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.preferenceService.getUserPreferenceTypes(userId));
    }

    @PostMapping("/user/add")
    public ResponseEntity<PreferenceUserResponseDto> addPreference(@RequestBody PreferenceUserRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.preferenceService.addUserPreferenceType(req, userId));
    }

    @PostMapping("/user/remove")
    public ResponseEntity<PreferenceUserResponseDto> removePreference(@RequestBody PreferenceUserRequestDto req, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);
        return ResponseEntity.ok(this.preferenceService.removeUserPreferenceType(req, userId));
    }
}
