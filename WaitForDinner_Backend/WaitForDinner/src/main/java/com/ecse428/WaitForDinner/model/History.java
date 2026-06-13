/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8091.03bcab5b3 modeling language!*/

package com.ecse428.WaitForDinner.model;
import java.sql.Date;
import java.util.*;

import jakarta.persistence.*;

// line 73 "../../../../../model.ump"
// line 114 "../../../../../model.ump"
@Entity
@Table(name = "histories")
public class History
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int historyId;

  //History Attributes
  private boolean has;
  private Date dateAdded;
  private Date expiryDate;
  private int amount;

  @ManyToMany(mappedBy = "histories", fetch = FetchType.LAZY)
  private List<Ingredient> ingredients;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pantry_id", nullable = false)
  private Pantry pantry;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  protected History() {}

  public History(boolean aHas, Date aDateAdded, Date aExpiryDate, int aAmount, Pantry aPantry)
  {
    has = aHas;
    dateAdded = aDateAdded;
    expiryDate = aExpiryDate;
    amount = aAmount;
    ingredients = new ArrayList<Ingredient>();
    boolean didAddPantry = setPantry(aPantry);
    if (!didAddPantry)
    {
      throw new RuntimeException("Unable to create history due to pantry. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public int getHistoryId()
  {
    return historyId;
  }

  public boolean setHistoryId(int aHistoryId)
  {
    boolean wasSet = false;
    historyId = aHistoryId;
    wasSet = true;
    return wasSet;
  }

  public boolean setHas(boolean aHas)
  {
    boolean wasSet = false;
    has = aHas;
    wasSet = true;
    return wasSet;
  }

  public boolean setDateAdded(Date aDateAdded)
  {
    boolean wasSet = false;
    dateAdded = aDateAdded;
    wasSet = true;
    return wasSet;
  }

  public boolean setExpiryDate(Date aExpiryDate)
  {
    boolean wasSet = false;
    expiryDate = aExpiryDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setAmount(int aAmount)
  {
    boolean wasSet = false;
    amount = aAmount;
    wasSet = true;
    return wasSet;
  }

  public boolean getHas()
  {
    return has;
  }

  public Date getDateAdded()
  {
    return dateAdded;
  }

  public Date getExpiryDate()
  {
    return expiryDate;
  }

  public int getAmount()
  {
    return amount;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isHas()
  {
    return has;
  }
  /* Code from template association_GetMany */
  public Ingredient getIngredient(int index)
  {
    Ingredient aIngredient = ingredients.get(index);
    return aIngredient;
  }

  public List<Ingredient> getIngredients()
  {
    List<Ingredient> newIngredients = Collections.unmodifiableList(ingredients);
    return newIngredients;
  }

  public int numberOfIngredients()
  {
    int number = ingredients.size();
    return number;
  }

  public boolean hasIngredients()
  {
    boolean has = ingredients.size() > 0;
    return has;
  }

  public int indexOfIngredient(Ingredient aIngredient)
  {
    int index = ingredients.indexOf(aIngredient);
    return index;
  }
  /* Code from template association_GetOne */
  public Pantry getPantry()
  {
    return pantry;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfIngredients()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addIngredient(Ingredient aIngredient)
  {
    boolean wasAdded = false;
    if (ingredients.contains(aIngredient)) { return false; }
    ingredients.add(aIngredient);
    if (aIngredient.indexOfHistory(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aIngredient.addHistory(this);
      if (!wasAdded)
      {
        ingredients.remove(aIngredient);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removeIngredient(Ingredient aIngredient)
  {
    boolean wasRemoved = false;
    if (!ingredients.contains(aIngredient))
    {
      return wasRemoved;
    }

    int oldIndex = ingredients.indexOf(aIngredient);
    ingredients.remove(oldIndex);
    if (aIngredient.indexOfHistory(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aIngredient.removeHistory(this);
      if (!wasRemoved)
      {
        ingredients.add(oldIndex,aIngredient);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addIngredientAt(Ingredient aIngredient, int index)
  {  
    boolean wasAdded = false;
    if(addIngredient(aIngredient))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfIngredients()) { index = numberOfIngredients() - 1; }
      ingredients.remove(aIngredient);
      ingredients.add(index, aIngredient);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveIngredientAt(Ingredient aIngredient, int index)
  {
    boolean wasAdded = false;
    if(ingredients.contains(aIngredient))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfIngredients()) { index = numberOfIngredients() - 1; }
      ingredients.remove(aIngredient);
      ingredients.add(index, aIngredient);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addIngredientAt(aIngredient, index);
    }
    return wasAdded;
  }
  /* Code from template association_SetOneToMany */
  public boolean setPantry(Pantry aPantry)
  {
    boolean wasSet = false;
    if (aPantry == null)
    {
      return wasSet;
    }

    Pantry existingPantry = pantry;
    pantry = aPantry;
    if (existingPantry != null && !existingPantry.equals(aPantry))
    {
      existingPantry.removeHistory(this);
    }
    pantry.addHistory(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    ArrayList<Ingredient> copyOfIngredients = new ArrayList<Ingredient>(ingredients);
    ingredients.clear();
    for(Ingredient aIngredient : copyOfIngredients)
    {
      aIngredient.removeHistory(this);
    }
    Pantry placeholderPantry = pantry;
    this.pantry = null;
    if(placeholderPantry != null)
    {
      placeholderPantry.removeHistory(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "has" + ":" + getHas()+ "," +
            "amount" + ":" + getAmount()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "dateAdded" + "=" + (getDateAdded() != null ? !getDateAdded().equals(this)  ? getDateAdded().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "expiryDate" + "=" + (getExpiryDate() != null ? !getExpiryDate().equals(this)  ? getExpiryDate().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "pantry = "+(getPantry()!=null?Integer.toHexString(System.identityHashCode(getPantry())):"null");
  }
}