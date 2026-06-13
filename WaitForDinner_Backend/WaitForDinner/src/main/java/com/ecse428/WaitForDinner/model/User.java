/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8123.5b4c718a3 modeling language!*/

package com.ecse428.WaitForDinner.model;
import jakarta.persistence.*;

import java.util.*;
import java.util.Date;

// line 5 "../../../../../model.ump"
// line 91 "../../../../../model.ump"
@Entity
@Table(name = "users")
public class User
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static Map<String, User> usersByEmail = new HashMap<String, User>();

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //User Attributes
  private String name;
  @Column(unique = true)
  private String email;
  private String password;
  private Date createdAt;
  private Date token;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int userId;

  //User Associations
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<UserIngredientPreference> userIngredientPreferences;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_preference_types",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "preference_type_id"))
  private List<PreferenceType> preferenceTypes;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_diet_types",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "diet_type_id"))
  private List<DietType> dietTypes;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_pantries",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "pantry_id"))
  private List<Pantry> pantries;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Folder> folders;
  //------------------------
  // CONSTRUCTOR
  //------------------------


  public User(String aName, String aEmail, String aPassword, Date aCreatedAt, Date aToken, int aUserId)
  {
    name = aName;
    password = aPassword;
    createdAt = aCreatedAt;
    token = aToken;
    userId = aUserId;
    if (!setEmail(aEmail))
    {
      throw new RuntimeException("Cannot create due to duplicate email. See https://manual.umple.org?RE003ViolationofUniqueness.html");
    }
    preferenceTypes = new ArrayList<PreferenceType>();
    dietTypes = new ArrayList<DietType>();
    pantries = new ArrayList<Pantry>();
    folders = new ArrayList<Folder>();
    userIngredientPreferences = new ArrayList<UserIngredientPreference>();
  }

  public User () {

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

  public boolean setEmail(String aEmail)
  {
    boolean wasSet = false;
    email = aEmail;
    wasSet = true;
    return wasSet;
  }

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public boolean setCreatedAt(Date aCreatedAt)
  {
    boolean wasSet = false;
    createdAt = aCreatedAt;
    wasSet = true;
    return wasSet;
  }

  public boolean setToken(Date aToken)
  {
    boolean wasSet = false;
    token = aToken;
    wasSet = true;
    return wasSet;
  }

  public boolean setUserId(int aUserId)
  {
    boolean wasSet = false;
    userId = aUserId;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public String getEmail()
  {
    return email;
  }
  /* Code from template attribute_GetUnique */
  public static User getWithEmail(String aEmail)
  {
    return usersByEmail.get(aEmail);
  }
  /* Code from template attribute_HasUnique */
  public static boolean hasWithEmail(String aEmail)
  {
    return getWithEmail(aEmail) != null;
  }

  public String getPassword()
  {
    return password;
  }

  public Date getCreatedAt()
  {
    return createdAt;
  }

  public Date getToken()
  {
    return token;
  }

  public int getUserId()
  {
    return userId;
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
  /* Code from template association_GetMany */
  public Pantry getPantry(int index)
  {
    Pantry aPantry = pantries.get(index);
    return aPantry;
  }

  public List<Pantry> getPantries()
  {
    List<Pantry> newPantries = Collections.unmodifiableList(pantries);
    return newPantries;
  }

  public int numberOfPantries()
  {
    int number = pantries.size();
    return number;
  }

  public boolean hasPantries()
  {
    boolean has = pantries.size() > 0;
    return has;
  }

  public int indexOfPantry(Pantry aPantry)
  {
    int index = pantries.indexOf(aPantry);
    return index;
  }
  /* Code from template association_GetMany */
  public Folder getFolder(int index)
  {
    Folder aFolder = folders.get(index);
    return aFolder;
  }

  public List<Folder> getFolders()
  {
    List<Folder> newFolders = Collections.unmodifiableList(folders);
    return newFolders;
  }

  public int numberOfFolders()
  {
    int number = folders.size();
    return number;
  }

  public boolean hasFolders()
  {
    boolean has = folders.size() > 0;
    return has;
  }

  public int indexOfFolder(Folder aFolder)
  {
    int index = folders.indexOf(aFolder);
    return index;
  }
  /* Code from template association_GetMany */
  public UserIngredientPreference getUserIngredientPreference(int index)
  {
    UserIngredientPreference aUserIngredientPreference = userIngredientPreferences.get(index);
    return aUserIngredientPreference;
  }

  public List<UserIngredientPreference> getUserIngredientPreferences()
  {
    List<UserIngredientPreference> newUserIngredientPreferences = Collections.unmodifiableList(userIngredientPreferences);
    return newUserIngredientPreferences;
  }

  public int numberOfUserIngredientPreferences()
  {
    int number = userIngredientPreferences.size();
    return number;
  }

  public boolean hasUserIngredientPreferences()
  {
    boolean has = userIngredientPreferences.size() > 0;
    return has;
  }

  public int indexOfUserIngredientPreference(UserIngredientPreference aUserIngredientPreference)
  {
    int index = userIngredientPreferences.indexOf(aUserIngredientPreference);
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
    if (aPreferenceType.indexOfUser(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aPreferenceType.addUser(this);
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
    if (aPreferenceType.indexOfUser(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aPreferenceType.removeUser(this);
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
    if (aDietType.indexOfUser(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aDietType.addUser(this);
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
    if (aDietType.indexOfUser(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aDietType.removeUser(this);
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfPantries()
  {
    return 0;
  }
  /* Code from template association_AddManyToManyMethod */
  public boolean addPantry(Pantry aPantry)
  {
    boolean wasAdded = false;
    if (pantries.contains(aPantry)) { return false; }
    pantries.add(aPantry);
    if (aPantry.indexOfUser(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aPantry.addUser(this);
      if (!wasAdded)
      {
        pantries.remove(aPantry);
      }
    }
    return wasAdded;
  }
  /* Code from template association_RemoveMany */
  public boolean removePantry(Pantry aPantry)
  {
    boolean wasRemoved = false;
    if (!pantries.contains(aPantry))
    {
      return wasRemoved;
    }

    int oldIndex = pantries.indexOf(aPantry);
    pantries.remove(oldIndex);
    if (aPantry.indexOfUser(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aPantry.removeUser(this);
      if (!wasRemoved)
      {
        pantries.add(oldIndex,aPantry);
      }
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addPantryAt(Pantry aPantry, int index)
  {
    boolean wasAdded = false;
    if(addPantry(aPantry))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfPantries()) { index = numberOfPantries() - 1; }
      pantries.remove(aPantry);
      pantries.add(index, aPantry);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMovePantryAt(Pantry aPantry, int index)
  {
    boolean wasAdded = false;
    if(pantries.contains(aPantry))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfPantries()) { index = numberOfPantries() - 1; }
      pantries.remove(aPantry);
      pantries.add(index, aPantry);
      wasAdded = true;
    }
    else
    {
      wasAdded = addPantryAt(aPantry, index);
    }
    return wasAdded;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfFolders()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public Folder addFolder(String aFolderName, int aFolderId)
  {
    return new Folder(aFolderName, aFolderId, this);
  }

  public boolean addFolder(Folder aFolder)
  {
    boolean wasAdded = false;
    if (folders.contains(aFolder)) { return false; }
    User existingUser = aFolder.getUser();
    boolean isNewUser = existingUser != null && !this.equals(existingUser);
    if (isNewUser)
    {
      aFolder.setUser(this);
    }
    else
    {
      folders.add(aFolder);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeFolder(Folder aFolder)
  {
    boolean wasRemoved = false;
    //Unable to remove aFolder, as it must always have a user
    if (!this.equals(aFolder.getUser()))
    {
      folders.remove(aFolder);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addFolderAt(Folder aFolder, int index)
  {
    boolean wasAdded = false;
    if(addFolder(aFolder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfFolders()) { index = numberOfFolders() - 1; }
      folders.remove(aFolder);
      folders.add(index, aFolder);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveFolderAt(Folder aFolder, int index)
  {
    boolean wasAdded = false;
    if(folders.contains(aFolder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfFolders()) { index = numberOfFolders() - 1; }
      folders.remove(aFolder);
      folders.add(index, aFolder);
      wasAdded = true;
    }
    else
    {
      wasAdded = addFolderAt(aFolder, index);
    }
    return wasAdded;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfUserIngredientPreferences()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public UserIngredientPreference addUserIngredientPreference(UserIngredientPreference.Status aStatus, int aUserIngredientPreferenceId, Ingredient aIngredient)
  {
    return new UserIngredientPreference(aStatus, aUserIngredientPreferenceId, this, aIngredient);
  }


  public boolean addUserIngredientPreference(UserIngredientPreference aUserIngredientPreference)
  {
    boolean wasAdded = false;
    if (userIngredientPreferences.contains(aUserIngredientPreference)) { return false; }
    User existingUser = aUserIngredientPreference.getUser();
    boolean isNewUser = existingUser != null && !this.equals(existingUser);
    if (isNewUser)
    {
      aUserIngredientPreference.setUser(this);
    }
    else
    {
      userIngredientPreferences.add(aUserIngredientPreference);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeUserIngredientPreference(UserIngredientPreference aUserIngredientPreference)
  {
    boolean wasRemoved = false;
    //Unable to remove aUserIngredientPreference, as it must always have a user
    if (!this.equals(aUserIngredientPreference.getUser()))
    {
      userIngredientPreferences.remove(aUserIngredientPreference);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addUserIngredientPreferenceAt(UserIngredientPreference aUserIngredientPreference, int index)
  {
    boolean wasAdded = false;
    if(addUserIngredientPreference(aUserIngredientPreference))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfUserIngredientPreferences()) { index = numberOfUserIngredientPreferences() - 1; }
      userIngredientPreferences.remove(aUserIngredientPreference);
      userIngredientPreferences.add(index, aUserIngredientPreference);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveUserIngredientPreferenceAt(UserIngredientPreference aUserIngredientPreference, int index)
  {
    boolean wasAdded = false;
    if(userIngredientPreferences.contains(aUserIngredientPreference))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfUserIngredientPreferences()) { index = numberOfUserIngredientPreferences() - 1; }
      userIngredientPreferences.remove(aUserIngredientPreference);
      userIngredientPreferences.add(index, aUserIngredientPreference);
      wasAdded = true;
    }
    else
    {
      wasAdded = addUserIngredientPreferenceAt(aUserIngredientPreference, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    usersByEmail.remove(getEmail());
    ArrayList<PreferenceType> copyOfPreferenceTypes = new ArrayList<PreferenceType>(preferenceTypes);
    preferenceTypes.clear();
    for(PreferenceType aPreferenceType : copyOfPreferenceTypes)
    {
      aPreferenceType.removeUser(this);
    }
    ArrayList<DietType> copyOfDietTypes = new ArrayList<DietType>(dietTypes);
    dietTypes.clear();
    for(DietType aDietType : copyOfDietTypes)
    {
      aDietType.removeUser(this);
    }
    ArrayList<Pantry> copyOfPantries = new ArrayList<Pantry>(pantries);
    pantries.clear();
    for(Pantry aPantry : copyOfPantries)
    {
      aPantry.removeUser(this);
    }
    for(int i=folders.size(); i > 0; i--)
    {
      Folder aFolder = folders.get(i - 1);
      aFolder.delete();
    }
    for(int i=userIngredientPreferences.size(); i > 0; i--)
    {
      UserIngredientPreference aUserIngredientPreference = userIngredientPreferences.get(i - 1);
      aUserIngredientPreference.delete();
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "email" + ":" + getEmail()+ "," +
            "password" + ":" + getPassword()+ "," +
            "userId" + ":" + getUserId()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "createdAt" + "=" + (getCreatedAt() != null ? !getCreatedAt().equals(this)  ? getCreatedAt().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "token" + "=" + (getToken() != null ? !getToken().equals(this)  ? getToken().toString().replaceAll("  ","    ") : "this" : "null");
  }
}