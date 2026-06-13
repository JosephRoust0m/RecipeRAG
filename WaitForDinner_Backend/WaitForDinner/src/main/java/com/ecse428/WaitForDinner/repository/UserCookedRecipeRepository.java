package com.ecse428.WaitForDinner.repository;

import com.ecse428.WaitForDinner.model.UserCookedRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCookedRecipeRepository extends JpaRepository<UserCookedRecipe, Integer> {
    List<UserCookedRecipe> findByUserId(int userId);
    List<UserCookedRecipe> findByUserIdOrderByCookedAtDesc(int userId);
}
