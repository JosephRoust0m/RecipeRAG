package com.ecse428.WaitForDinner.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecse428.WaitForDinner.dto.DietTypeRequestDto;
import com.ecse428.WaitForDinner.dto.DietTypeResponseDto;
import com.ecse428.WaitForDinner.dto.DietTypesResponseDto;
import com.ecse428.WaitForDinner.dto.DietUserRequestDto;
import com.ecse428.WaitForDinner.dto.DietUserResponseDto;
import com.ecse428.WaitForDinner.model.DietType;
import com.ecse428.WaitForDinner.model.PreferenceType;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.DietRepository;
import com.ecse428.WaitForDinner.repository.PreferenceRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class DietService {
    private final DietRepository dietRepository;
    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public DietService(DietRepository dietRepository, PreferenceRepository preferenceRepository, UserRepository userRepository) {
        this.dietRepository = dietRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    public DietTypesResponseDto getAllDiets() {
        List<DietType> dietTypes = dietRepository.findAll();
        List<List<Integer>> dietPreferenceIdLists = new ArrayList<>();
        List<String> dietNames = new ArrayList<>();
        List<Integer> dietIds = new ArrayList<>();
        for (DietType dietType : dietTypes) {
            List<Integer> preferenceIds = dietType.getPreferenceTypes().stream().map(PreferenceType::getPreferenceTypeId).toList();
            dietPreferenceIdLists.add(preferenceIds);
            dietNames.add(dietType.getName());
            dietIds.add(dietType.getDieId());
        }

        DietTypesResponseDto res = new DietTypesResponseDto();
        res.setDietNames(dietNames);
        res.setDietIds(dietIds);
        res.setPreferenceIdLists(dietPreferenceIdLists);
        return res;
    }

    public DietTypeResponseDto getDietByName(String dietName) {
        DietType diet = dietRepository.findByName(dietName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet not found"));

        DietTypeResponseDto res = new DietTypeResponseDto();
        res.setDietName(diet.getName());
        res.setPreferenceIds(diet.getPreferenceTypes().stream().map(PreferenceType::getPreferenceTypeId).toList());
        return res;
    }

    public DietTypeResponseDto getDietById(int dietId) {
        DietType diet = dietRepository.findById(dietId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet not found"));

        DietTypeResponseDto res = new DietTypeResponseDto();
        res.setDietName(diet.getName());
        res.setPreferenceIds(diet.getPreferenceTypes().stream().map(PreferenceType::getPreferenceTypeId).toList());
        return res;
    }

    @Transactional
    public DietTypeResponseDto createDiet(DietTypeRequestDto req) {
        if (dietRepository.findByName(req.getDietName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Diet already exists");
        }

        DietType newDiet = new DietType(
            req.getDietName(),
            0
        );

        if (req.getPreferenceIds() != null) {
            for (int preferenceId : req.getPreferenceIds()) {
                PreferenceType preferenceType = preferenceRepository.findById(preferenceId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference with id " + preferenceId + " not found"));
                newDiet.addPreferenceType(preferenceType);
            }
        }

        DietType savedDiet = dietRepository.save(newDiet);

        DietTypeResponseDto res = new DietTypeResponseDto();
        res.setDietName(savedDiet.getName());
        res.setPreferenceIds(savedDiet.getPreferenceTypes().stream().map(PreferenceType::getPreferenceTypeId).toList());
        return res;
    }

    @Transactional
    public DietTypeResponseDto updateDiet(DietTypeRequestDto req) {
        DietType diet = dietRepository.findById(req.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet not found"));
        
        if (diet == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet not found");
        }

        if (req.getDietName() != null) {
            dietRepository.findByName(req.getDietName()).ifPresent(existing -> {
                if (existing.getDieId() != diet.getDieId()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "New diet name already exists");
                }
            });
            diet.setName(req.getDietName());
        }

        if (req.getPreferenceIds() != null) {
            Map<Integer, PreferenceType> existingPreferencesById = new HashMap<>();
            for (PreferenceType preferenceType : diet.getPreferenceTypes()) {
                existingPreferencesById.put(preferenceType.getPreferenceTypeId(), preferenceType);
            }

            Set<Integer> desiredPreferenceIds = new HashSet<>();
            for (int preferenceId : req.getPreferenceIds()) {
                PreferenceType preferenceType = preferenceRepository.findById(preferenceId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference with id " + preferenceId + " not found"));
                desiredPreferenceIds.add(preferenceId);
                if (!existingPreferencesById.containsKey(preferenceId)) {
                    diet.addPreferenceType(preferenceType);
                }
            }

            for (Map.Entry<Integer, PreferenceType> entry : existingPreferencesById.entrySet()) {
                if (!desiredPreferenceIds.contains(entry.getKey())) {
                    diet.removePreferenceType(entry.getValue());
                }
            }
        }

        dietRepository.save(diet);

        DietTypeResponseDto res = new DietTypeResponseDto();
        res.setDietName(diet.getName());
        res.setPreferenceIds(diet.getPreferenceTypes().stream().map(PreferenceType::getPreferenceTypeId).toList());
        return res;
    }

    @Transactional
    public void deleteDiet(int dietId) {
        dietRepository.findById(dietId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet not found"));

        dietRepository.deleteById(dietId);
    }

    @Transactional
    public DietUserResponseDto getCurrentDiets(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<DietType> dietTypes = user.getDietTypes();
        List<String> dietNames = dietTypes.stream().map(DietType::getName).toList();
        List<Integer> dietTypeIds = dietTypes.stream().map(DietType::getDieId).toList();

        DietUserResponseDto res = new DietUserResponseDto();
        res.setDietNames(dietNames);
        res.setDietTypeIds(dietTypeIds);
        return res;
    }

    @Transactional
    public DietUserResponseDto adoptDiet(DietUserRequestDto req, int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<Integer> existingDietIds = new HashSet<>();
        for (DietType dietType : user.getDietTypes()) {
            existingDietIds.add(dietType.getDieId());
        }

        if (req.getDietTypeIds() != null) {
            for (int dietId : req.getDietTypeIds()) {
                if (existingDietIds.contains(dietId)) {
                    continue;
                }
                DietType dietType = dietRepository.findById(dietId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet with id " + dietId + " not found"));
                user.addDietType(dietType);
                existingDietIds.add(dietId);
            }
        }
        userRepository.save(user);

        List<DietType> dietTypes = user.getDietTypes();
        List<String> dietNames = dietTypes.stream().map(DietType::getName).toList();
        List<Integer> dietTypeIds = dietTypes.stream().map(DietType::getDieId).toList();

        DietUserResponseDto res = new DietUserResponseDto();
        res.setDietNames(dietNames);
        res.setDietTypeIds(dietTypeIds);
        return res;
    }

    @Transactional
    public DietUserResponseDto quitDiet(DietUserRequestDto req, int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Map<Integer, DietType> existingDietsById = new HashMap<>();
        for (DietType dietType : user.getDietTypes()) {
            existingDietsById.put(dietType.getDieId(), dietType);
        }

        Set<Integer> removeIds = new HashSet<>();
        if (req.getDietTypeIds() != null) {
            for (int removeId : req.getDietTypeIds()) {
                if (!dietRepository.existsById(removeId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Diet with id " + removeId + " not found");
                }
                removeIds.add(removeId);
            }
        }

        for (int removeId : removeIds) {
            DietType existingDiet = existingDietsById.get(removeId);
            if (existingDiet != null) {
                user.removeDietType(existingDiet);
            }
        }

        userRepository.save(user);

        List<DietType> dietTypes = user.getDietTypes();
        List<String> dietNames = dietTypes.stream().map(DietType::getName).toList();
        List<Integer> dietTypeIds = dietTypes.stream().map(DietType::getDieId).toList();

        DietUserResponseDto res = new DietUserResponseDto();
        res.setDietNames(dietNames);
        res.setDietTypeIds(dietTypeIds);
        return res;
    }
}
