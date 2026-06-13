/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8091.03bcab5b3 modeling language!*/

package com.ecse428.WaitForDinner.model;

import jakarta.persistence.*;

// line 65 "../../../../model.ump"
// line 124 "../../../../model.ump"
@Entity
@Table(name = "needs")
public class Need
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int needId;

  //Need Attributes
  private int amount;
  private String modifier;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ingredient_id", nullable = false)
  private Ingredient ingredient;


  //------------------------
  // CONSTRUCTOR
  //------------------------

  // Required by JPA/Hibernate for proxy instantiation
  protected Need() {}

  public Need(int aAmount, String aModifier, int aNeedId, Recipe aRecipe, Ingredient aIngredient)
  {
    amount = aAmount;
    modifier = aModifier;
    needId = aNeedId;
    boolean didAddRecipe = setRecipe(aRecipe);
    if (!didAddRecipe)
    {
      throw new RuntimeException("Unable to create need due to recipe. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddIngredient = setIngredient(aIngredient);
    if (!didAddIngredient)
    {
      throw new RuntimeException("Unable to create need due to ingredient. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setAmount(int aAmount)
  {
    boolean wasSet = false;
    amount = aAmount;
    wasSet = true;
    return wasSet;
  }

  public boolean setModifier(String aModifier)
  {
    boolean wasSet = false;
    modifier = aModifier;
    wasSet = true;
    return wasSet;
  }

  public boolean setNeedId(int aNeedId)
  {
    boolean wasSet = false;
    needId = aNeedId;
    wasSet = true;
    return wasSet;
  }

  public int getAmount()
  {
    return amount;
  }

  public String getModifier()
  {
    return modifier;
  }

  public int getNeedId()
  {
    return needId;
  }
  /* Code from template association_GetOne */
  public Recipe getRecipe()
  {
    return recipe;
  }
  /* Code from template association_GetOne */
  public Ingredient getIngredient()
  {
    return ingredient;
  }
  /* Code from template association_SetOneToManyAssociationClass */
  public boolean setRecipe(Recipe aRecipe)
  {
    boolean wasSet = false;

    if (aRecipe == null)
    {
      return wasSet;
    }

    Recipe existingRecipe = recipe;
    recipe = aRecipe;
    if (existingRecipe != null && !existingRecipe.equals(aRecipe))
    {
      existingRecipe.removeNeed(this);
    }
    if (!recipe.addNeed(this))
    {
      recipe = existingRecipe;
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

    if (aIngredient == null)
    {
      return wasSet;
    }

    Ingredient existingIngredient = ingredient;
    ingredient = aIngredient;
    if (existingIngredient != null && !existingIngredient.equals(aIngredient))
    {
      existingIngredient.removeNeed(this);
    }
    if (!ingredient.addNeed(this))
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

    Need compareTo = (Need)obj;
  
    if (getRecipe() == null && compareTo.getRecipe() != null)
    {
      return false;
    }
    else if (getRecipe() != null && !getRecipe().equals(compareTo.getRecipe()))
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


  public void delete()
  {
    Recipe placeholderRecipe = recipe;
    this.recipe = null;
    if(placeholderRecipe != null)
    {
      placeholderRecipe.removeNeed(this);
    }
    Ingredient placeholderIngredient = ingredient;
    this.ingredient = null;
    if(placeholderIngredient != null)
    {
      placeholderIngredient.removeNeed(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "amount" + ":" + getAmount()+ "," +
            "modifier" + ":" + getModifier()+ "," +
            "needId" + ":" + getNeedId()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "recipe = "+(getRecipe()!=null?Integer.toHexString(System.identityHashCode(getRecipe())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "ingredient = "+(getIngredient()!=null?Integer.toHexString(System.identityHashCode(getIngredient())):"null");
  }
}