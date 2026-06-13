package com.ecse428.WaitForDinner.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_saved_recipes")
public class UserSavedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private int recipeId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date savedAt;

    public UserSavedRecipe() {}

    public UserSavedRecipe(int userId, int recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.savedAt = new Date();
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public Date getSavedAt() { return savedAt; }
    public void setSavedAt(Date savedAt) { this.savedAt = savedAt; }
}
