/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8123.5b4c718a3 modeling language!*/

package com.ecse428.WaitForDinner.model;

import jakarta.persistence.*;

// line 73 "../../../../model.ump"
// line 132 "../../../../model.ump"
@Entity
@Table (name = "user_ingredient_preferences")
public class UserIngredientPreference
{

    //------------------------
    // ENUMERATIONS
    //------------------------

    public enum Status { Like, Dislike, Allergic }

    //------------------------
    // MEMBER VARIABLES
    //------------------------

    //UserIngredientPreference Attributes
    private Status status;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userIngredientPreferenceId;

    //UserIngredientPreference Associations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    //Helper Variables
    private int cachedHashCode;
    private boolean canSetUser;
    private boolean canSetIngredient;

    //------------------------
    // CONSTRUCTOR
    //------------------------

    protected UserIngredientPreference() {}

    public UserIngredientPreference(Status aStatus, int aUserIngredientPreferenceId, User aUser, Ingredient aIngredient)
    {
        cachedHashCode = -1;
        canSetUser = true;
        canSetIngredient = true;
        status = aStatus;
        userIngredientPreferenceId = aUserIngredientPreferenceId;
        boolean didAddUser = setUser(aUser);
        if (!didAddUser)
        {
            throw new RuntimeException("Unable to create userIngredientPreference due to user. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
        }
        boolean didAddIngredient = setIngredient(aIngredient);
        if (!didAddIngredient)
        {
            throw new RuntimeException("Unable to create userIngredientPreference due to ingredient. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
        }
    }

    //------------------------
    // INTERFACE
    //------------------------

    public boolean setStatus(Status aStatus)
    {
        boolean wasSet = false;
        status = aStatus;
        wasSet = true;
        return wasSet;
    }

    public boolean setUserIngredientPreferenceId(int aUserIngredientPreferenceId)
    {
        boolean wasSet = false;
        userIngredientPreferenceId = aUserIngredientPreferenceId;
        wasSet = true;
        return wasSet;
    }

    public Status getStatus()
    {
        return status;
    }

    public int getUserIngredientPreferenceId()
    {
        return userIngredientPreferenceId;
    }
    /* Code from template association_GetOne */
    public User getUser()
    {
        return user;
    }
    /* Code from template association_GetOne */
    public Ingredient getIngredient()
    {
        return ingredient;
    }
    /* Code from template association_SetOneToManyAssociationClass */
    public boolean setUser(User aUser)
    {
        boolean wasSet = false;
        if (!canSetUser) { return false; }
        if (aUser == null)
        {
            return wasSet;
        }

        User existingUser = user;
        user = aUser;
        if (existingUser != null && !existingUser.equals(aUser))
        {
            existingUser.removeUserIngredientPreference(this);
        }
        if (!user.addUserIngredientPreference(this))
        {
            user = existingUser;
            wasSet = false;
        }
        else
        {
            wasSet = true;
        }
        return wasSet;
    }
    /* Code from template association_SetOneToManyAssociationClass */
    public boolean setIngredient(Ingredient aIngredient)
    {
        boolean wasSet = false;
        if (!canSetIngredient) { return false; }
        if (aIngredient == null)
        {
            return wasSet;
        }

        Ingredient existingIngredient = ingredient;
        ingredient = aIngredient;
        if (existingIngredient != null && !existingIngredient.equals(aIngredient))
        {
            existingIngredient.removeUserIngredientPreference(this);
        }
        if (!ingredient.addUserIngredientPreference(this))
        {
            ingredient = existingIngredient;
            wasSet = false;
        }
        else
        {
            wasSet = true;
        }
        return wasSet;
    }

    public boolean equals(Object obj)
    {
        if (obj == null) { return false; }
        if (!getClass().equals(obj.getClass())) { return false; }

        UserIngredientPreference compareTo = (UserIngredientPreference)obj;

        if (getUser() == null && compareTo.getUser() != null)
        {
            return false;
        }
        else if (getUser() != null && !getUser().equals(compareTo.getUser()))
        {
            return false;
        }

        if (getIngredient() == null && compareTo.getIngredient() != null)
        {
            return false;
        }
        else if (getIngredient() != null && !getIngredient().equals(compareTo.getIngredient()))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        if (cachedHashCode != -1)
        {
            return cachedHashCode;
        }
        cachedHashCode = 17;
        if (getUser() != null)
        {
            cachedHashCode = cachedHashCode * 23 + getUser().hashCode();
        }
        else
        {
            cachedHashCode = cachedHashCode * 23;
        }
        if (getIngredient() != null)
        {
            cachedHashCode = cachedHashCode * 23 + getIngredient().hashCode();
        }
        else
        {
            cachedHashCode = cachedHashCode * 23;
        }

        canSetUser = false;
        canSetIngredient = false;
        return cachedHashCode;
    }

    public void delete()
    {
        User placeholderUser = user;
        this.user = null;
        if(placeholderUser != null)
        {
            placeholderUser.removeUserIngredientPreference(this);
        }
        Ingredient placeholderIngredient = ingredient;
        this.ingredient = null;
        if(placeholderIngredient != null)
        {
            placeholderIngredient.removeUserIngredientPreference(this);
        }
    }


    public String toString()
    {
        return super.toString() + "["+
                "userIngredientPreferenceId" + ":" + getUserIngredientPreferenceId()+ "]" + System.getProperties().getProperty("line.separator") +
                "  " + "status" + "=" + (getStatus() != null ? !getStatus().equals(this)  ? getStatus().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
                "  " + "user = "+(getUser()!=null?Integer.toHexString(System.identityHashCode(getUser())):"null") + System.getProperties().getProperty("line.separator") +
                "  " + "ingredient = "+(getIngredient()!=null?Integer.toHexString(System.identityHashCode(getIngredient())):"null");
    }
}