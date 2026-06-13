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

import com.ecse428.WaitForDinner.dto.PreferenceTypeRequestDto;
import com.ecse428.WaitForDinner.dto.PreferenceTypeResponseDto;
import com.ecse428.WaitForDinner.dto.PreferenceTypesResponseDto;
import com.ecse428.WaitForDinner.dto.PreferenceUserRequestDto;
import com.ecse428.WaitForDinner.dto.PreferenceUserResponseDto;
import com.ecse428.WaitForDinner.model.PreferenceType;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.PreferenceRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public PreferenceService(PreferenceRepository preferenceRepository, UserRepository userRepository) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    public PreferenceTypesResponseDto getAllPreferenceTypes() {
        List<PreferenceType> preferenceTypes = preferenceRepository.findAll();
        List<String> preferenceTypeNames = new ArrayList<>();
        List<Integer> preferenceTypeIds = new ArrayList<>();
        for (PreferenceType preferenceType : preferenceTypes) {
            preferenceTypeNames.add(preferenceType.getName());
            preferenceTypeIds.add(preferenceType.getPreferenceTypeId());
        }

        PreferenceTypesResponseDto res = new PreferenceTypesResponseDto();
        return res;
    }

    public PreferenceTypeResponseDto getPreferenceTypeById(int preferenceId) {
        PreferenceType preferenceType = preferenceRepository.findById(preferenceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found"));

        PreferenceTypeResponseDto res = new PreferenceTypeResponseDto();
        res.setId(preferenceType.getPreferenceTypeId());
        res.setName(preferenceType.getName());
        return res;
    }

    public PreferenceTypeResponseDto getPreferenceTypeByName(String name) {
        PreferenceType preferenceType = preferenceRepository.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found"));

        PreferenceTypeResponseDto res = new PreferenceTypeResponseDto();
        res.setId(preferenceType.getPreferenceTypeId());
        res.setName(preferenceType.getName());
        return res;
    }

    @Transactional
    public PreferenceTypeResponseDto createPreference(PreferenceTypeRequestDto req) {
        if (preferenceRepository.findByName(req.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Preference already exists");
        }

        PreferenceType newPreference = new PreferenceType(
            req.getName(),
            0
        );

        PreferenceType savedPreference = this.preferenceRepository.save(newPreference);
        PreferenceTypeResponseDto res = new PreferenceTypeResponseDto();
        res.setId(savedPreference.getPreferenceTypeId());
        res.setName(savedPreference.getName());
        return res;
    }

    @Transactional
    public PreferenceTypeResponseDto updatePreference(PreferenceTypeRequestDto req) {
        PreferenceType preference = preferenceRepository.findById(req.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found"));

        if (preferenceRepository.findByName(req.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "New preference name already exists");
        }

        preference.setName(req.getName());
        this.preferenceRepository.save(preference);
        PreferenceTypeResponseDto res = new PreferenceTypeResponseDto();
        res.setId(preference.getPreferenceTypeId());
        res.setName(preference.getName());
        return res;
    }

    @Transactional
    public void deletePreference(int preferenceId) {
        preferenceRepository.findById(preferenceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference not found"));

        this.preferenceRepository.deleteById(preferenceId);
        return;
    }

    @Transactional
    public PreferenceUserResponseDto getUserPreferenceTypes(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<PreferenceType> preferenceTyeps = preferenceRepository.findAllByUsers(user);
        List<String> preferenceTypeNames = preferenceTyeps.stream().map(PreferenceType::getName).toList();
        List<Integer> preferenceTypeIds = preferenceTyeps.stream().map(PreferenceType::getPreferenceTypeId).toList();

        PreferenceUserResponseDto res = new PreferenceUserResponseDto();
        res.setPreferenceNames(preferenceTypeNames);
        res.setPreferenceIds(preferenceTypeIds);
        return res;
    }

    @Transactional
    public PreferenceUserResponseDto addUserPreferenceType(PreferenceUserRequestDto req, int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<Integer> existingPreferenceIds = new HashSet<>();
        for (PreferenceType preferenceType : user.getPreferenceTypes()) {
            existingPreferenceIds.add(preferenceType.getPreferenceTypeId());
        }

        for (int preferenceId : req.getPreferenceIds()) {
            if (existingPreferenceIds.contains(preferenceId)) {
                continue;
            }
            PreferenceType preferenceType = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference with id " + preferenceId + " not found"));
            user.addPreferenceType(preferenceType);
            existingPreferenceIds.add(preferenceId);
        }
        userRepository.save(user);

        List<PreferenceType> preferenceTyeps = preferenceRepository.findAllByUsers(user);
        List<String> preferenceTypeNames = preferenceTyeps.stream().map(PreferenceType::getName).toList();
        List<Integer> preferenceTypeIds = preferenceTyeps.stream().map(PreferenceType::getPreferenceTypeId).toList();

        PreferenceUserResponseDto res = new PreferenceUserResponseDto();
        res.setPreferenceNames(preferenceTypeNames);
        res.setPreferenceIds(preferenceTypeIds);
        return res;
    }

    @Transactional
    public PreferenceUserResponseDto removeUserPreferenceType(PreferenceUserRequestDto req, int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        Map<Integer, PreferenceType> existingPreferencesById = new HashMap<>();
        for (PreferenceType preferenceType : user.getPreferenceTypes()) {
            existingPreferencesById.put(preferenceType.getPreferenceTypeId(), preferenceType);
        }

        Set<Integer> removeIds = new HashSet<>();
        for (int removeId : req.getPreferenceIds()) {
            if (!preferenceRepository.existsById(removeId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Preference with id " + removeId + " not found");
            }
            removeIds.add(removeId);
        }

        for (int removeId : removeIds) {
            PreferenceType existingPreference = existingPreferencesById.get(removeId);
            if (existingPreference != null) {
                user.removePreferenceType(existingPreference);
            }
        }

        userRepository.save(user);

        List<PreferenceType> preferenceTyeps = preferenceRepository.findAllByUsers(user);
        List<String> preferenceTypeNames = preferenceTyeps.stream().map(PreferenceType::getName).toList();
        List<Integer> preferenceTypeIds = preferenceTyeps.stream().map(PreferenceType::getPreferenceTypeId).toList();

        PreferenceUserResponseDto res = new PreferenceUserResponseDto();
        res.setPreferenceNames(preferenceTypeNames);
        res.setPreferenceIds(preferenceTypeIds);
        return res;
    }
}
