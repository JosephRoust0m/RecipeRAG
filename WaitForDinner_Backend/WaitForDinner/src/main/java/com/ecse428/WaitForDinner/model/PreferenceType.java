/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8091.03bcab5b3 modeling language!*/

package com.ecse428.WaitForDinner.model;
import java.util.*;

import jakarta.persistence.*;

// line 40 "../../../../../model.ump"
// line 94 "../../../../../model.ump"
@Entity
@Table(name = "preference_types")
public class PreferenceType
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //PreferenceType Attributes
  private String name;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int preferenceTypeId;

  @ManyToMany(mappedBy = "preferenceTypes", fetch = FetchType.LAZY)
  private List<User> users;

  @ManyToMany(mappedBy = "preferenceTypes", fetch = FetchType.LAZY)
  private List<Recipe> recipes;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "preference_type_diet_types",
      joinColumns = @JoinColumn(name = "preference_type_id"),
      inverseJoinColumns = @JoinColumn(name = "diet_type_id"))
  private List<DietType> dietTypes;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  protected PreferenceType() {}

  public PreferenceType(String aName, int aPreferenceTypeId)
  {
    name = aName;
    preferenceTypeId = aPreferenceTypeId;
    users = new ArrayList<User>();
    recipes = new ArrayList<Recipe>();
    dietTypes = new ArrayList<DietType>();
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

  public boolean setPreferenceTypeId(int aPreferenceTypeId)
  {
    boolean wasSet = false;
    preferenceTypeId = aPreferenceTypeId;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public int getPreferenceTypeId()
  {
    return preferenceTypeId;
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
  /* Code from template association_GetMany */
  public DietType getDietType(int index)
  {
    DietType aDietType = dietTypes.get(index);
    return aDietType;
  }

  public List<DietType> getDietTypes()
  {
    List<DietType> newDietTypes = Collections.unmodifiableList(dietTypes);
    return newDietTypes;
  }

  public int numberOfDietTypes()
  {
    int number = dietTypes.size();
    return number;
  }

  public boolean hasDietTypes()
  {
    boolean has = dietTypes.size() > 0;
    return has;
  }

  public int indexOfDietType(DietType aDietType)
  {
    int index = dietTypes.indexOf(aDietType);
    return index;
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
    if (aUser.indexOfPreferenceType(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aUser.addPreferenceType(this);
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
    if (aUser.indexOfPreferenceType(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aUser.removePreferenceType(this);
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
    if (aRecipe.indexOfPreferenceType(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aRecipe.addPreferenceType(this);
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
    if (aRecipe.indexOfPreferenceType(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aRecipe.removePreferenceType(this);
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfDietTypes()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addDietType(DietType aDietType)
  {
    boolean wasAdded = false;
    if (dietTypes.contains(aDietType)) { return false; }
    dietTypes.add(aDietType);
    if (aDietType.indexOfPreferenceType(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aDietType.addPreferenceType(this);
      if (!wasAdded)
      {
        dietTypes.remove(aDietType);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removeDietType(DietType aDietType)
  {
    boolean wasRemoved = false;
    if (!dietTypes.contains(aDietType))
    {
      return wasRemoved;
    }

    int oldIndex = dietTypes.indexOf(aDietType);
    dietTypes.remove(oldIndex);
    if (aDietType.indexOfPreferenceType(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aDietType.removePreferenceType(this);
      if (!wasRemoved)
      {
        dietTypes.add(oldIndex,aDietType);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addDietTypeAt(DietType aDietType, int index)
  {  
    boolean wasAdded = false;
    if(addDietType(aDietType))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfDietTypes()) { index = numberOfDietTypes() - 1; }
      dietTypes.remove(aDietType);
      dietTypes.add(index, aDietType);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveDietTypeAt(DietType aDietType, int index)
  {
    boolean wasAdded = false;
    if(dietTypes.contains(aDietType))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfDietTypes()) { index = numberOfDietTypes() - 1; }
      dietTypes.remove(aDietType);
      dietTypes.add(index, aDietType);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addDietTypeAt(aDietType, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    ArrayList<User> copyOfUsers = new ArrayList<User>(users);
    users.clear();
    for(User aUser : copyOfUsers)
    {
      aUser.removePreferenceType(this);
    }
    ArrayList<Recipe> copyOfRecipes = new ArrayList<Recipe>(recipes);
    recipes.clear();
    for(Recipe aRecipe : copyOfRecipes)
    {
      aRecipe.removePreferenceType(this);
    }
    ArrayList<DietType> copyOfDietTypes = new ArrayList<DietType>(dietTypes);
    dietTypes.clear();
    for(DietType aDietType : copyOfDietTypes)
    {
      aDietType.removePreferenceType(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "preferenceTypeId" + ":" + getPreferenceTypeId()+ "]";
  }
}