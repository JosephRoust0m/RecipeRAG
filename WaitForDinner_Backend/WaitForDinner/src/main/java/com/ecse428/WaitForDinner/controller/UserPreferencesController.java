package com.ecse428.WaitForDinner.controller;

import com.ecse428.WaitForDinner.dto.SavePreferencesRequestDto;
import com.ecse428.WaitForDinner.dto.UserPreferencesResponseDto;
import com.ecse428.WaitForDinner.model.DietType;
import com.ecse428.WaitForDinner.model.PreferenceType;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.DietTypeRepository;
import com.ecse428.WaitForDinner.repository.PreferenceTypeRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;
import com.ecse428.WaitForDinner.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserPreferencesController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PreferenceTypeRepository preferenceTypeRepository;
    private final DietTypeRepository dietTypeRepository;

    public UserPreferencesController(UserService userService, UserRepository userRepository,
                                      PreferenceTypeRepository preferenceTypeRepository,
                                      DietTypeRepository dietTypeRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.preferenceTypeRepository = preferenceTypeRepository;
        this.dietTypeRepository = dietTypeRepository;
    }

    @GetMapping("/{id}/preferences")
    @Transactional
    public ResponseEntity<UserPreferencesResponseDto> getPreferences(
            @PathVariable int id, HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<String> preferenceTypeNames = new ArrayList<>();
        for (PreferenceType pt : user.getPreferenceTypes()) {
            if (pt.getName() != null) preferenceTypeNames.add(pt.getName());
        }

        List<String> dietTypeNames = new ArrayList<>();
        for (DietType dt : user.getDietTypes()) {
            if (dt.getName() != null) dietTypeNames.add(dt.getName());
        }

        return ResponseEntity.ok(new UserPreferencesResponseDto(preferenceTypeNames, dietTypeNames));
    }

    @PutMapping("/{id}/preferences")
    @Transactional
    public ResponseEntity<Void> savePreferences(
            @PathVariable int id,
            @RequestBody SavePreferencesRequestDto req,
            HttpSession session) {
        int userId = userService.requireLoggedInUserId(session);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Clear and re-set preference types
        List<PreferenceType> currentPrefs = new ArrayList<>(user.getPreferenceTypes());
        for (PreferenceType pt : currentPrefs) {
            user.removePreferenceType(pt);
        }

        if (req.getPreferenceTypeNames() != null) {
            for (String name : req.getPreferenceTypeNames()) {
                preferenceTypeRepository.findByName(name).ifPresent(pt -> {
                    if (!user.getPreferenceTypes().contains(pt)) {
                        user.addPreferenceType(pt);
                    }
                });
            }
        }

        // Clear and re-set diet types
        List<DietType> currentDiets = new ArrayList<>(user.getDietTypes());
        for (DietType dt : currentDiets) {
            user.removeDietType(dt);
        }

        if (req.getDietTypeNames() != null) {
            for (String name : req.getDietTypeNames()) {
                dietTypeRepository.findByName(name).ifPresent(dt -> {
                    if (!user.getDietTypes().contains(dt)) {
                        user.addDietType(dt);
                    }
                });
            }
        }

        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
