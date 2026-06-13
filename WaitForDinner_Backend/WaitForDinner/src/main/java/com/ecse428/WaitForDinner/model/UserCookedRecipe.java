package com.ecse428.WaitForDinner.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_cooked_recipes")
public class UserCookedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private int recipeId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date cookedAt;

    public UserCookedRecipe() {}

    public UserCookedRecipe(int userId, int recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.cookedAt = new Date();
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public Date getCookedAt() { return cookedAt; }
    public void setCookedAt(Date cookedAt) { this.cookedAt = cookedAt; }
}
