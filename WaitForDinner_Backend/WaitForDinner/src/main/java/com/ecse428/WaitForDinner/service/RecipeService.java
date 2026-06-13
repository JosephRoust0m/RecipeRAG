package com.ecse428.WaitForDinner.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ecse428.WaitForDinner.dto.CookedRecipeDto;
import com.ecse428.WaitForDinner.dto.RecipePublicDto;
import com.ecse428.WaitForDinner.dto.CookedRecipesResponseDto;
import com.ecse428.WaitForDinner.dto.MissingIngredientsResponseDto;
import com.ecse428.WaitForDinner.dto.RecipeDetailDto;
import com.ecse428.WaitForDinner.dto.RecipeFilterDto;
import com.ecse428.WaitForDinner.dto.RecipeFilterResponseDto;
import com.ecse428.WaitForDinner.dto.RecipeRankDto;
import com.ecse428.WaitForDinner.dto.RecipeRankResponseDto;
import com.ecse428.WaitForDinner.dto.RecipeSearchDto;
import com.ecse428.WaitForDinner.dto.RecipeSearchResponseDto;
import com.ecse428.WaitForDinner.dto.SavedRecipeDto;
import com.ecse428.WaitForDinner.dto.SavedRecipesResponseDto;
import com.ecse428.WaitForDinner.model.DietType;
import com.ecse428.WaitForDinner.model.History;
import com.ecse428.WaitForDinner.model.Ingredient;
import com.ecse428.WaitForDinner.model.Need;
import com.ecse428.WaitForDinner.model.Pantry;
import com.ecse428.WaitForDinner.model.PreferenceType;
import com.ecse428.WaitForDinner.model.Recipe;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.model.UserCookedRecipe;
import com.ecse428.WaitForDinner.model.UserSavedRecipe;
import com.ecse428.WaitForDinner.repository.PantryRepository;
import com.ecse428.WaitForDinner.repository.RecipeRepository;
import com.ecse428.WaitForDinner.repository.UserCookedRecipeRepository;
import com.ecse428.WaitForDinner.repository.UserRepository;
import com.ecse428.WaitForDinner.repository.UserSavedRecipeRepository;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserSavedRecipeRepository savedRecipeRepository;
    private final UserCookedRecipeRepository cookedRecipeRepository;
    private final PantryRepository pantryRepository;

    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository,
                         UserSavedRecipeRepository savedRecipeRepository,
                         UserCookedRecipeRepository cookedRecipeRepository,
                         PantryRepository pantryRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.savedRecipeRepository = savedRecipeRepository;
        this.cookedRecipeRepository = cookedRecipeRepository;
        this.pantryRepository = pantryRepository;
    }

    private Set<String> getPantryIngredients(User user) {
        Set<String> pantryIngredients = new HashSet<>();
        for (Pantry pantry : user.getPantries()) {
            for (History history : pantry.getHistories()) {
                if (!history.getHas() || history.getAmount() <= 0) continue;
                if (history.getIngredients().isEmpty()) continue;
                Ingredient ingredient = history.getIngredients().get(0);
                pantryIngredients.add(ingredient.getName().toLowerCase());
            }
        }
        return pantryIngredients;
    }

    public RecipeRankResponseDto rankRecipes(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<String> pantryIngredients = getPantryIngredients(user);

        Set<String> userDietRestrictions = new HashSet<>();
        for (DietType dietType : user.getDietTypes()) {
            if (dietType.getName() != null) {
                userDietRestrictions.add(dietType.getName().toLowerCase());
            }
        }

        List<RecipeRankDto> ranked = new ArrayList<>();

        for (Recipe recipe : recipeRepository.findAll()) {
            Set<String> requiredIngredientNames = new HashSet<>();
            for (Need need : recipe.getNeeds()) {
                Ingredient ingredient = need.getIngredient();
                if (ingredient != null && ingredient.getName() != null) {
                    requiredIngredientNames.add(ingredient.getName().toLowerCase());
                }
            }

            int matchCount = 0;
            for (String required : requiredIngredientNames) {
                if (pantryIngredients.contains(required)) matchCount++;
            }

            boolean fullCookable = !requiredIngredientNames.isEmpty() && matchCount == requiredIngredientNames.size();

            Set<String> recipeDietNames = new HashSet<>();
            for (DietType dietType : recipe.getDietTypes()) {
                if (dietType.getName() != null) recipeDietNames.add(dietType.getName().toLowerCase());
            }

            if (!fullCookable) continue;

            boolean dietaryMatch = userDietRestrictions.isEmpty() || recipeDietNames.containsAll(userDietRestrictions);

            String recipeName = recipe.getName() != null ? recipe.getName() : "Recipe " + recipe.getRecipeId();
            ranked.add(new RecipeRankDto(recipeName, matchCount, fullCookable, dietaryMatch));
        }

        ranked.sort((a, b) -> {
            if (b.getMatchCount() != a.getMatchCount()) return Integer.compare(b.getMatchCount(), a.getMatchCount());
            if (Boolean.compare(b.isFullyCookable(), a.isFullyCookable()) != 0) return Boolean.compare(b.isFullyCookable(), a.isFullyCookable());
            if (Boolean.compare(b.isDietaryMatch(), a.isDietaryMatch()) != 0) return Boolean.compare(b.isDietaryMatch(), a.isDietaryMatch());
            return a.getName().compareToIgnoreCase(b.getName());
        });

        return new RecipeRankResponseDto(ranked);
    }
    @Transactional
    public RecipeSearchResponseDto getValidRecipes(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<String> pantryIngredients = getPantryIngredients(user);

        Set<String> userDietNames = new HashSet<>();
        for (DietType dt : user.getDietTypes()) {
            if (dt.getName() != null) userDietNames.add(dt.getName().toLowerCase());
        }
        Set<String> userPrefNames = new HashSet<>();
        for (PreferenceType pt : user.getPreferenceTypes()) {
            if (pt.getName() != null) userPrefNames.add(pt.getName().toLowerCase());
        }

        List<RecipeSearchDto> results = new ArrayList<>();
        Map<Integer, int[]> recipeScores = new HashMap<>(); // recipeId -> [dietScore, prefScore]

        for (Recipe recipe : recipeRepository.findAll()) {

            // Filter: only include recipes where user has all required ingredients
            Set<String> requiredIngredients = new HashSet<>();
            for (Need need : recipe.getNeeds()) {
                if (need.getIngredient() != null && need.getIngredient().getName() != null) {
                    requiredIngredients.add(need.getIngredient().getName().toLowerCase());
                }
            }
            boolean fullyCookable = !requiredIngredients.isEmpty() && pantryIngredients.containsAll(requiredIngredients);

            if (!fullyCookable) continue;

            String dietTag = recipe.getDietTypes().isEmpty() ? "none" : recipe.getDietTypes().get(0).getName();
            String cuisineName = recipe.getPreferenceTypes().isEmpty() ? "International" : recipe.getPreferenceTypes().get(0).getName();

            int dietScore = 0;
            for (DietType dt : recipe.getDietTypes()) {
                if (dt.getName() != null && userDietNames.contains(dt.getName().toLowerCase())) dietScore++;
            }
            int prefScore = 0;
            for (PreferenceType pt : recipe.getPreferenceTypes()) {
                if (pt.getName() != null && userPrefNames.contains(pt.getName().toLowerCase())) prefScore++;
            }

            results.add(new RecipeSearchDto(
                    recipe.getRecipeId(),
                    recipe.getName(),
                    dietTag,
                    cuisineName,
                    recipe.getTime(),
                    recipe.getDifficulty(),
                    requiredIngredients.size(),
                    true
            ));
            recipeScores.put(recipe.getRecipeId(), new int[]{dietScore, prefScore});
        }

        // Rank by diet type matches desc, then preference type matches desc, then name asc
        results.sort((a, b) -> {
            int[] scoresA = recipeScores.get(a.getRecipeId());
            int[] scoresB = recipeScores.get(b.getRecipeId());
            int cmp = Integer.compare(scoresB[0], scoresA[0]);
            if (cmp != 0) return cmp;
            cmp = Integer.compare(scoresB[1], scoresA[1]);
            if (cmp != 0) return cmp;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        return new RecipeSearchResponseDto(results);
    }

    public RecipeFilterResponseDto filterRecipes(int userId, String dietaryTag, Integer maxTime, String difficulty) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<RecipeFilterDto> results = new ArrayList<>();

        for (Recipe recipe : recipeRepository.findAll()) {
            if (dietaryTag != null) {
                boolean hasTag = recipe.getDietTypes().stream()
                        .anyMatch(d -> dietaryTag.equalsIgnoreCase(d.getName()));
                if (!hasTag) continue;
            }

            if (maxTime != null && recipe.getTime() > maxTime) continue;

            if (difficulty != null && !difficulty.equalsIgnoreCase(recipe.getDifficulty())) continue;

            String tag = recipe.getDietTypes().isEmpty() ? "none" : recipe.getDietTypes().get(0).getName();
            results.add(new RecipeFilterDto(recipe.getName(), tag, recipe.getTime(), recipe.getDifficulty()));
        }

        return new RecipeFilterResponseDto(results);
    }

    @Transactional
    public RecipeSearchResponseDto searchRecipes(int userId, String keyword, String cuisine, String dietaryTag) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<String> pantryIngredients = getPantryIngredients(user);

        List<RecipeSearchDto> results = new ArrayList<>();

        for (Recipe recipe : recipeRepository.findAll()) {
            // Filter by keyword (name or ingredients)
            if (keyword != null && !keyword.isBlank()) {
                boolean nameMatch = recipe.getName() != null && recipe.getName().toLowerCase().contains(keyword.toLowerCase());
                boolean ingredientMatch = recipe.getNeeds().stream()
                        .anyMatch(n -> n.getIngredient() != null &&
                                n.getIngredient().getName() != null &&
                                n.getIngredient().getName().toLowerCase().contains(keyword.toLowerCase()));
                if (!nameMatch && !ingredientMatch) continue;
            }

            // Filter by cuisine
            if (cuisine != null && !cuisine.isBlank()) {
                boolean hasCuisine = recipe.getPreferenceTypes().stream()
                        .anyMatch(p -> cuisine.equalsIgnoreCase(p.getName()));
                if (!hasCuisine) continue;
            }

            // Filter by dietary tag
            if (dietaryTag != null && !dietaryTag.isBlank()) {
                boolean hasTag = recipe.getDietTypes().stream()
                        .anyMatch(d -> dietaryTag.equalsIgnoreCase(d.getName()));
                if (!hasTag) continue;
            }

            // Calculate match count
            Set<String> requiredIngredients = new HashSet<>();
            for (Need need : recipe.getNeeds()) {
                if (need.getIngredient() != null && need.getIngredient().getName() != null) {
                    requiredIngredients.add(need.getIngredient().getName().toLowerCase());
                }
            }
            int matchCount = 0;
            for (String req : requiredIngredients) {
                if (pantryIngredients.contains(req)) matchCount++;
            }
            boolean fullyCookable = !requiredIngredients.isEmpty() && matchCount == requiredIngredients.size();

            String dietTag = recipe.getDietTypes().isEmpty() ? "none" : recipe.getDietTypes().get(0).getName();
            String cuisineName = recipe.getPreferenceTypes().isEmpty() ? "International" : recipe.getPreferenceTypes().get(0).getName();

            results.add(new RecipeSearchDto(
                    recipe.getRecipeId(),
                    recipe.getName(),
                    dietTag,
                    cuisineName,
                    recipe.getTime(),
                    recipe.getDifficulty(),
                    matchCount,
                    fullyCookable
            ));
        }

        // Sort by match count descending
        results.sort((a, b) -> Integer.compare(b.getMatchCount(), a.getMatchCount()));

        return new RecipeSearchResponseDto(results);
    }

    @Transactional
    public RecipePublicDto getRecipeByName(String name) {
        Recipe recipe = recipeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found: " + name));

        List<String> ingredients = new ArrayList<>();
        for (Need need : recipe.getNeeds()) {
            if (need.getIngredient() != null && need.getIngredient().getName() != null) {
                ingredients.add(need.getIngredient().getName());
            }
        }

        List<String> dietaryTags = new ArrayList<>();
        for (DietType dt : recipe.getDietTypes()) {
            if (dt.getName() != null) dietaryTags.add(dt.getName());
        }

        List<String> cuisines = new ArrayList<>();
        for (PreferenceType pt : recipe.getPreferenceTypes()) {
            if (pt.getName() != null) cuisines.add(pt.getName());
        }

        List<String> steps = new ArrayList<>();
        if (recipe.getSteps() != null && !recipe.getSteps().isBlank()) {
            for (String line : recipe.getSteps().split("\\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) steps.add(trimmed);
            }
        }

        return new RecipePublicDto(
                recipe.getRecipeId(),
                recipe.getName(),
                recipe.getDifficulty(),
                recipe.getTime(),
                recipe.getPortions(),
                ingredients,
                dietaryTags,
                cuisines,
                steps
        );
    }

    @Transactional
    public RecipeDetailDto getRecipeDetail(int recipeId, int userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        List<String> ingredients = new ArrayList<>();
        for (Need need : recipe.getNeeds()) {
            if (need.getIngredient() != null && need.getIngredient().getName() != null) {
                ingredients.add(need.getIngredient().getName());
            }
        }

        List<String> dietaryTags = new ArrayList<>();
        for (DietType dt : recipe.getDietTypes()) {
            if (dt.getName() != null) dietaryTags.add(dt.getName());
        }

        List<String> cuisines = new ArrayList<>();
        for (PreferenceType pt : recipe.getPreferenceTypes()) {
            if (pt.getName() != null) cuisines.add(pt.getName());
        }

        boolean saved = savedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId).isPresent();

        return new RecipeDetailDto(
                recipe.getRecipeId(),
                recipe.getName(),
                recipe.getSteps(),
                recipe.getDifficulty(),
                recipe.getTime(),
                recipe.getPortions(),
                ingredients,
                dietaryTags,
                cuisines,
                saved
        );
    }

    @Transactional
    public MissingIngredientsResponseDto getMissingIngredients(int recipeId, int userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<String> pantryIngredients = getPantryIngredients(user);

        List<String> missing = new ArrayList<>();
        for (Need need : recipe.getNeeds()) {
            if (need.getIngredient() != null && need.getIngredient().getName() != null) {
                String ingredientName = need.getIngredient().getName();
                if (!pantryIngredients.contains(ingredientName.toLowerCase())) {
                    missing.add(ingredientName);
                }
            }
        }

        return new MissingIngredientsResponseDto(missing);
    }

    @Transactional
    public void saveRecipe(int recipeId, int userId) {
        recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        if (savedRecipeRepository.findByUserIdAndRecipeId(userId, recipeId).isEmpty()) {
            savedRecipeRepository.save(new UserSavedRecipe(userId, recipeId));
        }
    }

    @Transactional
    public void unsaveRecipe(int recipeId, int userId) {
        savedRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }

    @Transactional
    public SavedRecipesResponseDto getSavedRecipes(int userId) {
        List<UserSavedRecipe> saved = savedRecipeRepository.findByUserId(userId);
        List<SavedRecipeDto> dtos = new ArrayList<>();

        for (UserSavedRecipe s : saved) {
            recipeRepository.findById(s.getRecipeId()).ifPresent(recipe -> {
                String dietTag = recipe.getDietTypes().isEmpty() ? "none" : recipe.getDietTypes().get(0).getName();
                String cuisine = recipe.getPreferenceTypes().isEmpty() ? "International" : recipe.getPreferenceTypes().get(0).getName();
                dtos.add(new SavedRecipeDto(recipe.getRecipeId(), recipe.getName(), dietTag, cuisine));
            });
        }

        return new SavedRecipesResponseDto(dtos);
    }

    @Transactional
    public void cookRecipe(int recipeId, int userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Set<String> pantryIngredients = getPantryIngredients(user);
        Set<String> requiredIngredients = new HashSet<>();
        for (Need need : recipe.getNeeds()) {
            if (need.getIngredient() != null && need.getIngredient().getName() != null) {
                requiredIngredients.add(need.getIngredient().getName().toLowerCase());
            }
        }

        if (!pantryIngredients.containsAll(requiredIngredients)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have all required ingredients to cook this recipe");
        }

        // Subtract each ingredient's required amount from the user's pantry (FIFO by expiry)
        List<Pantry> pantries = pantryRepository.findAllByUsers(user);
        java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

        for (Need need : recipe.getNeeds()) {
            if (need.getIngredient() == null) continue;
            int ingredientId = need.getIngredient().getIngredientId();
            int toSubtract = need.getAmount();

            // Collect all valid history entries for this ingredient across all pantries
            List<History> candidates = new ArrayList<>();
            for (Pantry pantry : pantries) {
                for (History history : pantry.getHistories()) {
                    if (!history.getHas() || history.getAmount() <= 0) continue;
                    if (history.getIngredients().isEmpty()) continue;
                    if (history.getIngredients().get(0).getIngredientId() != ingredientId) continue;
                    java.sql.Date expiry = history.getExpiryDate();
                    if (expiry != null && expiry.before(now)) continue;
                    candidates.add(history);
                }
            }

            // Consume soonest-to-expire first
            candidates.sort((a, b) -> {
                if (a.getExpiryDate() == null) return 1;
                if (b.getExpiryDate() == null) return -1;
                return a.getExpiryDate().compareTo(b.getExpiryDate());
            });

            int remaining = toSubtract;
            for (History history : candidates) {
                if (remaining <= 0) break;
                int available = history.getAmount();
                if (available <= remaining) {
                    remaining -= available;
                    history.setAmount(0);
                    history.setHas(false);
                } else {
                    history.setAmount(available - remaining);
                    remaining = 0;
                }
            }
        }

        // Persist all pantry changes
        for (Pantry pantry : pantries) {
            pantryRepository.save(pantry);
        }

        cookedRecipeRepository.save(new UserCookedRecipe(userId, recipeId));
    }

    @Transactional
    public CookedRecipesResponseDto getCookedRecipes(int userId) {
        List<UserCookedRecipe> cooked = cookedRecipeRepository.findByUserIdOrderByCookedAtDesc(userId);
        List<CookedRecipeDto> dtos = new ArrayList<>();

        for (UserCookedRecipe c : cooked) {
            recipeRepository.findById(c.getRecipeId()).ifPresent(recipe -> {
                dtos.add(new CookedRecipeDto(recipe.getRecipeId(), recipe.getName(), c.getCookedAt()));
            });
        }

        return new CookedRecipesResponseDto(dtos);
    }
}
