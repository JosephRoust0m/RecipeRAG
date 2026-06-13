package com.ecse428.WaitForDinner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ecse428.WaitForDinner.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    Optional<Recipe> findByNameIgnoreCase(String name);
}
