/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8091.03bcab5b3 modeling language!*/

package com.ecse428.WaitForDinner.model;
import java.util.*;
import java.sql.Date;

import jakarta.persistence.*;

// line 52 "../../../../../model.ump"
// line 104 "../../../../../model.ump"
@Entity
@Table(name = "pantries")
public class Pantry
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Pantry Attributes
  private String name;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int pantryId;

  @OneToMany(mappedBy = "pantry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<History> histories;

  @ManyToMany(mappedBy = "pantries", fetch = FetchType.LAZY)
  private List<User> users;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  protected Pantry() {}

  public Pantry(String aName, int aPantryId)
  {
    name = aName;
    pantryId = aPantryId;
    histories = new ArrayList<History>();
    users = new ArrayList<User>();
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

  public boolean setPantryId(int aPantryId)
  {
    boolean wasSet = false;
    pantryId = aPantryId;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public int getPantryId()
  {
    return pantryId;
  }
  /* Code from template association_GetMany */
  public History getHistory(int index)
  {
    History aHistory = histories.get(index);
    return aHistory;
  }

  public List<History> getHistories()
  {
    List<History> newHistories = Collections.unmodifiableList(histories);
    return newHistories;
  }

  public int numberOfHistories()
  {
    int number = histories.size();
    return number;
  }

  public boolean hasHistories()
  {
    boolean has = histories.size() > 0;
    return has;
  }

  public int indexOfHistory(History aHistory)
  {
    int index = histories.indexOf(aHistory);
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
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfHistories()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public History addHistory(boolean aHas, Date aDateAdded, Date aExpiryDate, int aAmount)
  {
    return new History(aHas, aDateAdded, aExpiryDate, aAmount, this);
  }

  public boolean addHistory(History aHistory)
  {
    boolean wasAdded = false;
    if (histories.contains(aHistory)) { return false; }
    Pantry existingPantry = aHistory.getPantry();
    boolean isNewPantry = existingPantry != null && !this.equals(existingPantry);
    if (isNewPantry)
    {
      aHistory.setPantry(this);
    }
    else
    {
      histories.add(aHistory);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeHistory(History aHistory)
  {
    boolean wasRemoved = false;
    //Unable to remove aHistory, as it must always have a pantry
    if (!this.equals(aHistory.getPantry()))
    {
      histories.remove(aHistory);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addHistoryAt(History aHistory, int index)
  {  
    boolean wasAdded = false;
    if(addHistory(aHistory))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfHistories()) { index = numberOfHistories() - 1; }
      histories.remove(aHistory);
      histories.add(index, aHistory);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveHistoryAt(History aHistory, int index)
  {
    boolean wasAdded = false;
    if(histories.contains(aHistory))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfHistories()) { index = numberOfHistories() - 1; }
      histories.remove(aHistory);
      histories.add(index, aHistory);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addHistoryAt(aHistory, index);
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
    if (aUser.indexOfPantry(this) != -1)
    {
      wasAdded = true;
    }
    else
    {
      wasAdded = aUser.addPantry(this);
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
    if (aUser.indexOfPantry(this) == -1)
    {
      wasRemoved = true;
    }
    else
    {
      wasRemoved = aUser.removePantry(this);
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

  public void delete()
  {
    for(int i=histories.size(); i > 0; i--)
    {
      History aHistory = histories.get(i - 1);
      aHistory.delete();
    }
    ArrayList<User> copyOfUsers = new ArrayList<User>(users);
    users.clear();
    for(User aUser : copyOfUsers)
    {
      aUser.removePantry(this);
    }
  }


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "pantryId" + ":" + getPantryId()+ "]";
  }
}