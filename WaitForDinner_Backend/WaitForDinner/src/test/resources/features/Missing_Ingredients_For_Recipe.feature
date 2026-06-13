Feature: User can see which ingredients are missing from pantry for a recipe
  As a registered user
  I want to see which ingredients are missing from my pantry for a given recipe
  So that I know exactly what I need to purchase before I can cook it

  Background:
    Given the user is authenticated
    And the user's pantry contains the following items:
      | name    | quantity | unit  |
      | Eggs    | 6        | unit  |
      | Butter  | 200      | gram  |
      | Flour   | 500      | gram  |
    And the following recipes exist in the system:
      | name              | required_ingredients                    |
      | Pancakes          | Eggs, Butter, Flour, Milk               |
      | Omelette          | Eggs, Butter                            |
      | Beef Stew         | Beef, Potatoes, Carrots, Salt           |
      | Crepes            | Eggs, Butter, Flour, Milk, Sugar        |

  Scenario: System identifies a single missing ingredient for a recipe
    When the user requests the missing ingredients for "Pancakes"
    Then the system returns a missing ingredients list containing "Milk"
    And the missing ingredients list does not contain "Eggs"
    And the missing ingredients list does not contain "Butter"
    And the missing ingredients list does not contain "Flour"

  Scenario: System returns an empty missing ingredients list when all ingredients are available
    When the user requests the missing ingredients for "Omelette"
    Then the system returns an empty missing ingredients list
    And the user does not receive an error message

  Scenario: System identifies multiple missing ingredients for a recipe
    When the user requests the missing ingredients for "Beef Stew"
    Then the missing ingredients list contains "Beef"
    And the missing ingredients list contains "Potatoes"
    And the missing ingredients list contains "Carrots"
    And the missing ingredients list contains "Salt"

  Scenario: Missing ingredients list is updated after a new item is added to the pantry
    Given the user's pantry also contains the following items:
      | name  | quantity | unit  |
      | Milk  | 1        | litre |
    When the user requests the missing ingredients for "Pancakes"
    Then the system returns an empty missing ingredients list

  Scenario: Missing ingredients list is updated after an item is removed from the pantry
    Given "Butter" has been removed from the user's pantry
    When the user requests the missing ingredients for "Omelette"
    Then the missing ingredients list contains "Butter"

  Scenario: System returns an error when the requested recipe does not exist
    When the user requests the missing ingredients for "Dragon Soup"
    Then the user receives an error message indicating the recipe was not found
    And no missing ingredients list is returned

  Scenario Outline: System correctly identifies missing ingredients across different recipes
    When the user requests the missing ingredients for "<recipe>"
    Then the missing ingredients list contains "<missing_ingredient>"

    Examples:
      | recipe    | missing_ingredient |
      | Pancakes  | Milk               |
      | Beef Stew | Beef               |
      | Beef Stew | Potatoes           |
      | Crepes    | Milk               |
      | Crepes    | Sugar              |

  Scenario: Unauthenticated user cannot request missing ingredients for a recipe
    Given the user is not authenticated
    When the user attempts to request the missing ingredients for "Pancakes"
    Then the user receives an unauthorized error
    And no missing ingredients list is returned
