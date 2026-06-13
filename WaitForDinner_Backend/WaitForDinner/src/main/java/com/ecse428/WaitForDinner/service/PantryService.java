package com.ecse428.WaitForDinner.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecse428.WaitForDinner.dto.PantryHistoryEventDto;
import com.ecse428.WaitForDinner.dto.PantryHistoryResponseDto;
import com.ecse428.WaitForDinner.dto.PantryIngredientAmountRequestDto;
import com.ecse428.WaitForDinner.dto.PantryIngredientRequestDto;
import com.ecse428.WaitForDinner.dto.PantryItemDto;
import com.ecse428.WaitForDinner.dto.PantryItemsResponseDto;
import com.ecse428.WaitForDinner.dto.PantryTypeDto;
import com.ecse428.WaitForDinner.dto.PantryTypeResponseDto;
import com.ecse428.WaitForDinner.model.History;
import com.ecse428.WaitForDinner.model.Ingredient;
import com.ecse428.WaitForDinner.model.Pantry;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.HistoryRepository;
import com.ecse428.WaitForDinner.repository.IngredientRepository;
import com.ecse428.WaitForDinner.repository.PantryRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PantryService {
    private final PantryRepository pantryRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    public PantryService(PantryRepository pantryRepository, IngredientRepository ingredientRepository, UserRepository userRepository, HistoryRepository historyRepository) {
        this.pantryRepository = pantryRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public PantryTypeResponseDto getPantryTypeById(int pantryId, int userId) {
        Pantry pantry = pantryRepository.findById(pantryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pantry not found"));

        boolean ownsPantry = pantry.getUsers().stream()
            .anyMatch(user -> user.getUserId() == userId);
        if (!ownsPantry) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pantry does not belong to user");
        }

        Map<Integer, String> ingredientsById = new LinkedHashMap<>();
        for (History history : pantry.getHistories()) {
            if (!history.getHas()) {
                continue;
            }
            if (history.getIngredients().isEmpty()) {
                continue;
            }
            Ingredient ingredient = history.getIngredients().get(0);
            ingredientsById.putIfAbsent(ingredient.getIngredientId(), ingredient.getName());
        }

        List<Integer> ingredientIds = new ArrayList<>(ingredientsById.keySet());
        List<String> ingredientNames = new ArrayList<>(ingredientsById.values());
        PantryTypeDto dto = new PantryTypeDto(pantry.getName(), ingredientIds, ingredientNames);
        return new PantryTypeResponseDto(List.of(dto));
    }

    @Transactional
    public PantryTypeResponseDto getAllPantries(int userId) {
        User user = resolveUser(userId);
        List<Pantry> pantries = pantryRepository.findAllByUsers(user);
        List<PantryTypeDto> pantryTypes = new ArrayList<>();

        for (Pantry pantry : pantries) {
            Map<Integer, String> ingredientsById = new LinkedHashMap<>();
            for (History history : pantry.getHistories()) {
                if (!history.getHas()) {
                    continue;
                }
                if (history.getIngredients().isEmpty()) {
                    continue;
                }
                Ingredient ingredient = history.getIngredients().get(0);
                ingredientsById.putIfAbsent(ingredient.getIngredientId(), ingredient.getName());
            }

            List<Integer> ingredientIds = new ArrayList<>(ingredientsById.keySet());
            List<String> ingredientNames = new ArrayList<>(ingredientsById.values());
            pantryTypes.add(new PantryTypeDto(pantry.getName(), ingredientIds, ingredientNames));
        }

        return new PantryTypeResponseDto(pantryTypes);
    }

    @Transactional
    public PantryTypeResponseDto createPantry(String name, int userId) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pantry name is required");
        }

        User user = resolveUser(userId);
        Pantry pantry = new Pantry(name, 0);
        Pantry savedPantry = pantryRepository.save(pantry);
        user.addPantry(savedPantry);
        userRepository.save(user);

        PantryTypeDto dto = new PantryTypeDto(savedPantry.getName(), new ArrayList<>(), new ArrayList<>());
        return new PantryTypeResponseDto(List.of(dto));
    }

    @Transactional
    public void deletePantry(int pantryId, int userId) {
        Pantry pantry = pantryRepository.findById(pantryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pantry not found"));

        boolean ownsPantry = pantry.getUsers().stream()
            .anyMatch(user -> user.getUserId() == userId);
        if (!ownsPantry) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pantry does not belong to user");
        }

        List<User> users = new ArrayList<>(pantry.getUsers());
        for (User user : users) {
            user.removePantry(pantry);
            userRepository.save(user);
        }

        pantryRepository.deleteById(pantryId);
    }

    @Transactional
    public PantryTypeResponseDto addIngredient(PantryIngredientAmountRequestDto req, int userId) {
        if (req == null || req.getPantryId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pantry id is required");
        }
        if (req.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }

        Pantry pantry = pantryRepository.findById(req.getPantryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pantry not found"));

        boolean ownsPantry = pantry.getUsers().stream()
            .anyMatch(user -> user.getUserId() == userId);
        if (!ownsPantry) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pantry does not belong to user");
        }

        Ingredient ingredient;
        if (req.getIngredientId() > 0) {
            ingredient = ingredientRepository.findById(req.getIngredientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient id is required");
        }

        Date dateAdded = new Date(System.currentTimeMillis());
        long expiryMillis = System.currentTimeMillis() + ingredient.getShelfLife() * 24L * 60L * 60L * 1000L;
        Date expiryDate = new Date(expiryMillis);
        History history = new History(true, dateAdded, expiryDate, req.getAmount(), pantry);
        historyRepository.save(history);
        ingredient.addHistory(history);
        ingredientRepository.save(ingredient);

        Map<Integer, String> ingredientsById = new LinkedHashMap<>();
        for (History existingHistoryItem : pantry.getHistories()) {
            if (!existingHistoryItem.getHas()) {
                continue;
            }
            if (existingHistoryItem.getIngredients().isEmpty()) {
                continue;
            }
            Ingredient existing = existingHistoryItem.getIngredients().get(0);
            ingredientsById.putIfAbsent(existing.getIngredientId(), existing.getName());
        }

        List<Integer> ingredientIds = new ArrayList<>(ingredientsById.keySet());
        List<String> ingredientNames = new ArrayList<>(ingredientsById.values());
        PantryTypeDto dto = new PantryTypeDto(pantry.getName(), ingredientIds, ingredientNames);
        return new PantryTypeResponseDto(List.of(dto));
    }

    @Transactional
    public PantryTypeResponseDto removeIngredient(PantryIngredientRequestDto req, int userId) {
        if (req == null || req.getPantryId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pantry id is required");
        }

        Pantry pantry = pantryRepository.findById(req.getPantryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pantry not found"));

        boolean ownsPantry = pantry.getUsers().stream()
            .anyMatch(user -> user.getUserId() == userId);
        if (!ownsPantry) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pantry does not belong to user");
        }

        Ingredient ingredient;
        if (req.getIngredientId() > 0) {
            ingredient = ingredientRepository.findById(req.getIngredientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        } else if (req.getIngredientName() != null && !req.getIngredientName().isBlank()) {
            ingredient = ingredientRepository.findByName(req.getIngredientName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient id or name is required");
        }

        List<History> histories = new ArrayList<>(pantry.getHistories());
        boolean removed = false;

        for (History history : histories) {
            if (history.getIngredients().isEmpty()) {
                continue;
            }
            Ingredient existing = history.getIngredients().get(0);
            boolean matches = existing.getIngredientId() == ingredient.getIngredientId();
            if (!matches) {
                continue;
            }

            if (history.getHas()) {
                history.setHas(false);
                history.setAmount(0);
            } else {
                history.removeIngredient(ingredient);
                if (history.numberOfIngredients() == 0) {
                    history.delete();
                }
            }
            removed = true;
            break;
        }

        if (removed) {
            pantryRepository.save(pantry);
        }

        Map<Integer, String> ingredientsById = new LinkedHashMap<>();
        for (History history : pantry.getHistories()) {
            if (!history.getHas()) {
                continue;
            }
            if (history.getIngredients().isEmpty()) {
                continue;
            }
            Ingredient existing = history.getIngredients().get(0);
            ingredientsById.putIfAbsent(existing.getIngredientId(), existing.getName());
        }

        List<Integer> ingredientIds = new ArrayList<>(ingredientsById.keySet());
        List<String> ingredientNames = new ArrayList<>(ingredientsById.values());
        PantryTypeDto dto = new PantryTypeDto(pantry.getName(), ingredientIds, ingredientNames);
        return new PantryTypeResponseDto(List.of(dto));
    }

    @Transactional
    public PantryTypeResponseDto removeIngredientAmount(PantryIngredientAmountRequestDto req, int userId) {
        if (req == null || req.getPantryId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pantry id is required");
        }
        if (req.getIngredientId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingredient id is required");
        }
        if (req.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }

        Pantry pantry = pantryRepository.findById(req.getPantryId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pantry not found"));

        boolean ownsPantry = pantry.getUsers().stream()
            .anyMatch(user -> user.getUserId() == userId);
        if (!ownsPantry) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pantry does not belong to user");
        }

        Ingredient ingredient = ingredientRepository.findById(req.getIngredientId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingredient not found"));

        Date now = new Date(System.currentTimeMillis());
        List<History> candidates = new ArrayList<>();
        int totalAvailable = 0;

        for (History history : pantry.getHistories()) {
            if (!history.getHas()) {
                continue;
            }
            if (history.getIngredients().isEmpty()) {
                continue;
            }
            Ingredient existing = history.getIngredients().get(0);
            if (existing.getIngredientId() != ingredient.getIngredientId()) {
                continue;
            }
            Date expiryDate = history.getExpiryDate();
            if (expiryDate != null && expiryDate.before(now)) {
                continue;
            }
            if (history.getAmount() <= 0) {
                continue;
            }
            candidates.add(history);
            totalAvailable += history.getAmount();
        }

        if (totalAvailable < req.getAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough ingredient amount");
        }

        candidates.sort((left, right) -> {
            Date leftExpiry = left.getExpiryDate();
            Date rightExpiry = right.getExpiryDate();
            if (leftExpiry == null && rightExpiry == null) {
                return 0;
            }
            if (leftExpiry == null) {
                return 1;
            }
            if (rightExpiry == null) {
                return -1;
            }
            return leftExpiry.compareTo(rightExpiry);
        });

        int remaining = req.getAmount();
        for (History history : candidates) {
            if (remaining <= 0) {
                break;
            }

            int available = history.getAmount();
            if (available > remaining) {
                history.setAmount(available - remaining);
                remaining = 0;
                if (history.getAmount() == 0) {
                    history.setHas(false);
                }
                break;
            }

            history.setAmount(0);
            history.setHas(false);
            remaining -= available;
        }

        pantryRepository.save(pantry);

        Map<Integer, String> ingredientsById = new LinkedHashMap<>();
        for (History history : pantry.getHistories()) {
            if (!history.getHas()) {
                continue;
            }
            if (history.getIngredients().isEmpty()) {
                continue;
            }
            Ingredient existing = history.getIngredients().get(0);
            ingredientsById.putIfAbsent(existing.getIngredientId(), existing.getName());
        }

        List<Integer> ingredientIds = new ArrayList<>(ingredientsById.keySet());
        List<String> ingredientNames = new ArrayList<>(ingredientsById.values());
        PantryTypeDto dto = new PantryTypeDto(pantry.getName(), ingredientIds, ingredientNames);
        return new PantryTypeResponseDto(List.of(dto));
    }

    @Transactional
    public PantryHistoryResponseDto getPantryHistory(int pantryId, int userId) {
        return getPantryHistory(pantryId, userId, null, null, null);
    }

    @Transactional
    public PantryHistoryResponseDto getPantryHistory(int pantryId, int userId, String itemName, String startDate, String endDate) {
        Pantry pantry = pantryRepository.findById(pantryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pantry not found"));

        boolean ownsPantry = pantry.getUsers().stream()
            .anyMatch(user -> user.getUserId() == userId);
        if (!ownsPantry) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pantry does not belong to user");
        }

        Date start = null;
        Date end = null;

        if (startDate != null && !startDate.isBlank()) {
            try {
                start = Date.valueOf(startDate);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid start date format");
            }
        }

        if (endDate != null && !endDate.isBlank()) {
            try {
                end = Date.valueOf(endDate);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid end date format");
            }
        }

        List<PantryHistoryEventDto> events = new ArrayList<>();
        for (History history : pantry.getHistories()) {
            if (history.getIngredients().isEmpty()) {
                continue;
            }

            Ingredient ingredient = history.getIngredients().get(0);
            if (itemName != null && !itemName.isBlank() && !ingredient.getName().equalsIgnoreCase(itemName)) {
                continue;
            }

            Date dateAdded = history.getDateAdded();
            if (dateAdded != null) {
                if (start != null && dateAdded.before(start)) {
                    continue;
                }
                if (end != null && dateAdded.after(end)) {
                    continue;
                }
            }

            String action = history.getHas() ? "added" : "removed";
            int quantity = history.getAmount();
            String unit = resolveUnit(ingredient.getName());
            String date = dateAdded != null ? dateAdded.toString() : null;

            PantryHistoryEventDto event = new PantryHistoryEventDto(
                ingredient.getName(),
                action,
                quantity,
                unit,
                date
            );
            events.add(event);
        }

        events.sort((a, b) -> {
            if (a.getDate() == null && b.getDate() == null) return 0;
            if (a.getDate() == null) return -1;
            if (b.getDate() == null) return 1;
            return a.getDate().compareTo(b.getDate());
        });

        return new PantryHistoryResponseDto(events);
    }

    @Transactional
    public PantryItemsResponseDto getPantryItems(int userId) {
        User user = resolveUser(userId);
        List<Pantry> pantries = pantryRepository.findAllByUsers(user);

        Map<String, Integer> quantitiesByName = new LinkedHashMap<>();
        Map<String, String> originalNamesByLowercase = new LinkedHashMap<>();

        for (Pantry pantry : pantries) {
            for (History history : pantry.getHistories()) {
                if (!history.getHas()) {
                    continue;
                }
                if (history.getIngredients().isEmpty()) {
                    continue;
                }
                if (history.getAmount() <= 0) {
                    continue;
                }

                Ingredient ingredient = history.getIngredients().get(0);
                String ingredientName = ingredient.getName();
                String key = ingredientName.toLowerCase();

                originalNamesByLowercase.putIfAbsent(key, ingredientName);
                quantitiesByName.put(key, quantitiesByName.getOrDefault(key, 0) + history.getAmount());
            }
        }

        List<PantryItemDto> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : quantitiesByName.entrySet()) {
            String key = entry.getKey();
            String actualName = originalNamesByLowercase.get(key);
            items.add(new PantryItemDto(actualName, entry.getValue(), resolveUnit(actualName)));
        }

        items.sort((left, right) -> left.getName().compareToIgnoreCase(right.getName()));
        return new PantryItemsResponseDto(items);
    }

    @Transactional
    public PantryItemDto getPantryItemByName(String name, int userId) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is required");
        }

        PantryItemsResponseDto response = getPantryItems(userId);
        for (PantryItemDto item : response.getItems()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
    }

    @Transactional
    public void deletePantryItemByName(String name, int userId) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is required");
        }

        User user = resolveUser(userId);
        List<Pantry> pantries = pantryRepository.findAllByUsers(user);

        boolean found = false;

        for (Pantry pantry : pantries) {
            boolean pantryChanged = false;

            for (History history : pantry.getHistories()) {
                if (!history.getHas()) {
                    continue;
                }
                if (history.getIngredients().isEmpty()) {
                    continue;
                }

                Ingredient ingredient = history.getIngredients().get(0);
                if (!ingredient.getName().equalsIgnoreCase(name)) {
                    continue;
                }

                history.setHas(false);
                history.setAmount(0);
                found = true;
                pantryChanged = true;
            }

            if (pantryChanged) {
                pantryRepository.save(pantry);
            }
        }

        if (!found) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found");
        }
    }

    @Transactional
    public PantryItemsResponseDto upsertPantryItem(int userId, String name, int quantity) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item name is required");
        }
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive");
        }

        User user = resolveUser(userId);

        // Get or create default pantry
        List<Pantry> pantries = pantryRepository.findAllByUsers(user);
        Pantry pantry;
        if (pantries.isEmpty()) {
            pantry = new Pantry("My Pantry", 0);
            pantry = pantryRepository.save(pantry);
            user.addPantry(pantry);
            userRepository.save(user);
        } else {
            pantry = pantries.get(0);
        }

        // Find ingredient — must already exist in DB
        Ingredient ingredient = ingredientRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ingredient \"" + name + "\" not found. Please use an ingredient name that exists in the database."));

        // Mark existing entries for this ingredient as removed
        boolean changed = false;
        for (History history : new ArrayList<>(pantry.getHistories())) {
            if (history.getIngredients().isEmpty()) continue;
            Ingredient existing = history.getIngredients().get(0);
            if (existing.getIngredientId() == ingredient.getIngredientId()) {
                history.setHas(false);
                history.setAmount(0);
                changed = true;
            }
        }
        if (changed) pantryRepository.save(pantry);

        // Add new entry
        java.sql.Date dateAdded = new java.sql.Date(System.currentTimeMillis());
        long shelfLifeMs = (long) ingredient.getShelfLife() * 24 * 60 * 60 * 1000;
        java.sql.Date expiryDate = new java.sql.Date(System.currentTimeMillis() + shelfLifeMs);
        History history = new History(true, dateAdded, expiryDate, quantity, pantry);
        historyRepository.save(history);
        ingredient.addHistory(history);
        ingredientRepository.save(ingredient);

        return getPantryItems(userId);
    }

    private User resolveUser(int userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private String resolveUnit(String ingredientName) {
        if (ingredientName == null) {
            return "unit";
        }

        return switch (ingredientName.trim().toLowerCase()) {
            case "eggs" -> "unit";
            case "milk" -> "litre";
            case "flour" -> "gram";
            case "butter" -> "gram";
            case "olive oil" -> "ml";
            default -> "unit";
        };
    }
}