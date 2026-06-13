package com.ecse428.WaitForDinner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecse428.WaitForDinner.dto.IngredientRequestDto;
import com.ecse428.WaitForDinner.dto.IngredientResponseDto;
import com.ecse428.WaitForDinner.dto.IngredientUserRequestDto;
import com.ecse428.WaitForDinner.dto.IngredientUserResponseDto;
import com.ecse428.WaitForDinner.dto.IngredientsResponseDto;
import com.ecse428.WaitForDinner.model.Ingredient;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.model.UserIngredientPreference;
import com.ecse428.WaitForDinner.repository.IngredientRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    public IngredientService(IngredientRepository ingredientRepository, UserRepository userRepository) {
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
    }

    public IngredientsResponseDto getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        List<String> ingredientNames = ingredients.stream()
            .map(Ingredient::getName)
            .toList();
        List<Integer> ingredientIds = ingredients.stream()
            .map(Ingredient::getIngredientId)
            .toList();

        IngredientsResponseDto res = new IngredientsResponseDto();
        res.setIngredientNames(ingredientNames);
        res.setId(ingredientIds);
        return res;
    }

    public IngredientResponseDto getIngredientById(int ingredientId) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

        IngredientResponseDto res = new IngredientResponseDto();
        res.setId(ingredient.getIngredientId());
        res.setIngredientName(ingredient.getName());
        return res;
    }

    public IngredientResponseDto getIngredientByName(String ingredientName) {
        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

        IngredientResponseDto res = new IngredientResponseDto();
        res.setId(ingredient.getIngredientId());
        res.setIngredientName(ingredient.getName());
        return res;
    }

    @Transactional
    public IngredientResponseDto createIngredient(IngredientRequestDto req) {
        if (ingredientRepository.findByName(req.getIngredientName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ingredient with name " + req.getIngredientName() + " already exists");
        }
        Ingredient ingredient = new Ingredient(
            req.getIngredientName(),
            0,
            0
        );

        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        IngredientResponseDto res = new IngredientResponseDto();
        res.setId(savedIngredient.getIngredientId());
        res.setIngredientName(savedIngredient.getName());
        return res;
    }

    @Transactional
    public void deleteIngredient(int ingredientId) {
        ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        ingredientRepository.deleteById(ingredientId);
    }

    @Transactional
    public IngredientUserResponseDto getIngredientPreferences(int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<UserIngredientPreference> preferences = user.getUserIngredientPreferences();
        List<String> ingredientNames = preferences.stream()
            .map(preference -> preference.getIngredient().getName())
            .toList();
        List<Integer> ingredientIds = preferences.stream()
            .map(preference -> preference.getIngredient().getIngredientId())
            .toList();
        List<String> statuses = preferences.stream()
            .map(preference -> preference.getStatus().name())
            .toList();

        IngredientUserResponseDto res = new IngredientUserResponseDto();
        res.setIngredientNames(ingredientNames);
        res.setIngredientIds(ingredientIds);
        res.setStatuses(statuses);
        return res;
    }

    @Transactional
    public IngredientUserResponseDto addIngredientPreference(IngredientUserRequestDto req, int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Ingredient ingredient = resolveIngredient(req);
        UserIngredientPreference.Status status = resolveStatus(req);

        Optional<UserIngredientPreference> existingPreference = user.getUserIngredientPreferences().stream()
            .filter(preference -> preference.getIngredient().getIngredientId() == ingredient.getIngredientId())
            .findFirst();

        if (existingPreference.isPresent()) {
            existingPreference.get().setStatus(status);
        } else {
            user.addUserIngredientPreference(status, 0, ingredient);
        }

        userRepository.save(user);
        return getIngredientPreferences(userId);
    }

    @Transactional
    public IngredientUserResponseDto removeIngredientPreference(IngredientUserRequestDto req, int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Ingredient ingredient = resolveIngredient(req);

        Optional<UserIngredientPreference> existingPreference = user.getUserIngredientPreferences().stream()
            .filter(preference -> preference.getIngredient().getIngredientId() == ingredient.getIngredientId())
            .findFirst();

        existingPreference.ifPresent(UserIngredientPreference::delete);

        userRepository.save(user);
        return getIngredientPreferences(userId);
    }

    private Ingredient resolveIngredient(IngredientUserRequestDto req) {
        if (req.getIngredientId() > 0) {
            return ingredientRepository.findById(req.getIngredientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        }
        if (req.getIngredientName() != null && !req.getIngredientName().isBlank()) {
            return ingredientRepository.findByName(req.getIngredientName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient id or name is required");
    }

    private UserIngredientPreference.Status resolveStatus(IngredientUserRequestDto req) {
        if (req.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient preference status is required");
        }
        return UserIngredientPreference.Status.valueOf(req.getStatus().name());
    }
}
