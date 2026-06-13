Feature: Delete Pantry Items
  As a registered user
  I want to delete items from my pantry
  So that my pantry reflects what I actually have available

  Background:
    Given the user is authenticated
    And the user has the following items in their pantry:
      | name        | quantity | unit |
      | Eggs        | 12       | unit |
      | Milk        | 2        | litre |
      | Flour       | 500      | gram |

  Scenario: Successfully delete a single pantry item
    When the user deletes "Eggs" from their pantry
    Then "Eggs" is no longer in the user's pantry
    And the pantry contains 2 items

  Scenario: Delete an item that does not exist in the pantry
    When the user deletes "Butter" from their pantry
    Then the user receives an error message indicating the item was not found
    And the pantry still contains 3 items

  Scenario Outline: Delete multiple different pantry items
    When the user deletes "<item>" from their pantry
    Then "<item>" is no longer in the user's pantry

    Examples:
      | item  |
      | Eggs  |
      | Milk  |
      | Flour |

  Scenario: Delete all pantry items one by one
    When the user deletes "Eggs" from their pantry
    And the user deletes "Milk" from their pantry
    And the user deletes "Flour" from their pantry
    Then the user's pantry is empty

  Scenario: Unauthenticated user cannot delete a pantry item
    Given the user is not authenticated
    When the user attempts to delete "Eggs" from their pantry
    Then the user receives an unauthorized error
    And the pantry is unchanged