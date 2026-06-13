/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8091.03bcab5b3 modeling language!*/

package com.ecse428.WaitForDinner.model;
import java.util.*;

import jakarta.persistence.*;

// line 33 "../../../../../model.ump"
// line 119 "../../../../../model.ump"
@Entity
@Table(name = "diet_types")
public class DietType
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //DietType Attributes
  private String name;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int dieId;

  @ManyToMany(mappedBy = "dietTypes", fetch = FetchType.LAZY)
  private List<PreferenceType> preferenceTypes;

  @ManyToMany(mappedBy = "dietTypes", fetch = FetchType.LAZY)
  private List<User> users;

  @ManyToMany(mappedBy = "dietTypes", fetch = FetchType.LAZY)
  private List<Recipe> recipes;

  //----------------------c--
  // CONSTRUCTOR
  //------------------------

  protected DietType() {}

  public DietType(String aName, int aDieId)
  {
    name = aName;
    dieId = aDieId;
    preferenceTypes = new ArrayList<PreferenceType>();
    users = new ArrayList<User>();
    recipes = new ArrayList<Recipe>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setDieId(int aDieId)
  {
    boolean wasSet = false;
    dieId = aDieId;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public int getDieId()
  {
    return dieId;
  }
  /* Code from template association_GetMany */
  public PreferenceType getPreferenceType(int index)
  {
    PreferenceType aPreferenceType = preferenceTypes.get(index);
    return aPreferenceType;
  }

  public List<PreferenceType> getPreferenceTypes()
  {
    List<PreferenceType> newPreferenceTypes = Collections.unmodifiableList(preferenceTypes);
    return newPreferenceTypes;
  }

  public int numberOfPreferenceTypes()
  {
    int number = preferenceTypes.size();
    return number;
  }

  public boolean hasPreferenceTypes()
  {
    boolean has = preferenceTypes.size() > 0;
    return has;
  }

  public int indexOfPreferenceType(PreferenceType aPreferenceType)
  {
    int index = preferenceTypes.indexOf(aPreferenceType);
    return index;
  }
  /* Code from template association_GetMany */
  public User getUser(int index)
  {
    User aUser = users.get(index);
    return aUser;
  }

  public List<User> getUsers()
  {
    List<User> newUsers = Collections.unmodifiableList(users);
    return newUsers;
  }

  public int numberOfUsers()
  {
    int number = users.size();
    return number;
  }

  public boolean hasUsers()
  {
    boolean has = users.size() > 0;
    return has;
  }

  public int indexOfUser(User aUser)
  {
    int index = users.indexOf(aUser);
    return index;
  }
  /* Code from template association_GetMany */
  public Recipe getRecipe(int index)
  {
    Recipe aRecipe = recipes.get(index);
    return aRecipe;
  }

  public List<Recipe> getRecipes()
  {
    List<Recipe> newRecipes = Collections.unmodifiableList(recipes);
    return newRecipes;
  }

  public int numberOfRecipes()
  {
    int number = recipes.size();
    return number;
  }

  public boolean hasRecipes()
  {
    boolean has = recipes.size() > 0;
    return has;
  }

  public int indexOfRecipe(Recipe aRecipe)
  {
    int index = recipes.indexOf(aRecipe);
    return index;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfPreferenceTypes()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addPreferenceType(PreferenceType aPreferenceType)
  {
    boolean wasAdded = false;
    if (preferenceTypes.contains(aPreferenceType)) { return false; }
    preferenceTypes.add(aPreferenceType);
    if (aPreferenceType.indexOfDietType(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aPreferenceType.addDietType(this);
      if (!wasAdded)
      {
        preferenceTypes.remove(aPreferenceType);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removePreferenceType(PreferenceType aPreferenceType)
  {
    boolean wasRemoved = false;
    if (!preferenceTypes.contains(aPreferenceType))
    {
      return wasRemoved;
    }

    int oldIndex = preferenceTypes.indexOf(aPreferenceType);
    preferenceTypes.remove(oldIndex);
    if (aPreferenceType.indexOfDietType(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aPreferenceType.removeDietType(this);
      if (!wasRemoved)
      {
        preferenceTypes.add(oldIndex,aPreferenceType);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addPreferenceTypeAt(PreferenceType aPreferenceType, int index)
  {  
    boolean wasAdded = false;
    if(addPreferenceType(aPreferenceType))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfPreferenceTypes()) { index = numberOfPreferenceTypes() - 1; }
      preferenceTypes.remove(aPreferenceType);
      preferenceTypes.add(index, aPreferenceType);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMovePreferenceTypeAt(PreferenceType aPreferenceType, int index)
  {
    boolean wasAdded = false;
    if(preferenceTypes.contains(aPreferenceType))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfPreferenceTypes()) { index = numberOfPreferenceTypes() - 1; }
      preferenceTypes.remove(aPreferenceType);
      preferenceTypes.add(index, aPreferenceType);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addPreferenceTypeAt(aPreferenceType, index);
    }
    return wasAdded;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfUsers()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addUser(User aUser)
  {
    boolean wasAdded = false;
    if (users.contains(aUser)) { return false; }
    users.add(aUser);
    if (aUser.indexOfDietType(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aUser.addDietType(this);
      if (!wasAdded)
      {
        users.remove(aUser);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removeUser(User aUser)
  {
    boolean wasRemoved = false;
    if (!users.contains(aUser))
    {
      return wasRemoved;
    }

    int oldIndex = users.indexOf(aUser);
    users.remove(oldIndex);
    if (aUser.indexOfDietType(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aUser.removeDietType(this);
      if (!wasRemoved)
      {
        users.add(oldIndex,aUser);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addUserAt(User aUser, int index)
  {  
    boolean wasAdded = false;
    if(addUser(aUser))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfUsers()) { index = numberOfUsers() - 1; }
      users.remove(aUser);
      users.add(index, aUser);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveUserAt(User aUser, int index)
  {
    boolean wasAdded = false;
    if(users.contains(aUser))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfUsers()) { index = numberOfUsers() - 1; }
      users.remove(aUser);
      users.add(index, aUser);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addUserAt(aUser, index);
    }
    return wasAdded;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfRecipes()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addRecipe(Recipe aRecipe)
  {
    boolean wasAdded = false;
    if (recipes.contains(aRecipe)) { return false; }
    recipes.add(aRecipe);
    if (aRecipe.indexOfDietType(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aRecipe.addDietType(this);
      if (!wasAdded)
      {
        recipes.remove(aRecipe);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removeRecipe(Recipe aRecipe)
  {
    boolean wasRemoved = false;
    if (!recipes.contains(aRecipe))
    {
      return wasRemoved;
    }

    int oldIndex = recipes.indexOf(aRecipe);
    recipes.remove(oldIndex);
    if (aRecipe.indexOfDietType(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aRecipe.removeDietType(this);
      if (!wasRemoved)
      {
        recipes.add(oldIndex,aRecipe);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addRecipeAt(Recipe aRecipe, int index)
  {  
    boolean wasAdded = false;
    if(addRecipe(aRecipe))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfRecipes()) { index = numberOfRecipes() - 1; }
      recipes.remove(aRecipe);
      recipes.add(index, aRecipe);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveRecipeAt(Recipe aRecipe, int index)
  {
    boolean wasAdded = false;
    if(recipes.contains(aRecipe))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfRecipes()) { index = numberOfRecipes() - 1; }
      recipes.remove(aRecipe);
      recipes.add(index, aRecipe);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addRecipeAt(aRecipe, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    ArrayList<PreferenceType> copyOfPreferenceTypes = new ArrayList<PreferenceType>(preferenceTypes);
    preferenceTypes.clear();
    for(PreferenceType aPreferenceType : copyOfPreferenceTypes)
    {
      aPreferenceType.removeDietType(this);
    }
    ArrayList<User> copyOfUsers = new ArrayList<User>(users);
    users.clear();
    for(User aUser : copyOfUsers)
    {
      aUser.removeDietType(this);
    }
    ArrayList<Recipe> copyOfRecipes = new ArrayList<Recipe>(recipes);
    recipes.clear();
    for(Recipe aRecipe : copyOfRecipes)
    {
      aRecipe.removeDietType(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "dieId" + ":" + getDieId()+ "]";
  }
}