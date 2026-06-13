package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.UserSavedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSavedRecipeRepository extends JpaRepository<UserSavedRecipe, Integer> {
    List<UserSavedRecipe> findByUserId(int userId);
    Optional<UserSavedRecipe> findByUserIdAndRecipeId(int userId, int recipeId);
    void deleteByUserIdAndRecipeId(int userId, int recipeId);
}
