Feature: User can filter recipes to show only those fully cookable with current pantry
  As a registered user
  I want to filter recipes to only those I can fully cook with my current pantry
  So that I can immediately prepare a meal without needing to buy additional ingredients

  Background:
    Given the user is authenticated
    And the user's pantry contains the following items:
      | name          | quantity | unit  |
      | Eggs          | 6        | unit  |
      | Butter        | 200      | gram  |
      | Flour         | 500      | gram  |
      | Milk          | 1        | litre |
      | Chicken       | 300      | gram  |
    And the following recipes exist in the system:
      | name                  | required_ingredients                          |
      | Omelette              | Eggs, Butter                                  |
      | Pancakes              | Eggs, Butter, Flour, Milk                     |
      | Chicken Alfredo       | Chicken, Butter, Milk, Flour                  |
      | Beef Stew             | Beef, Potatoes, Carrots                       |
      | Pasta Carbonara       | Pasta, Eggs, Butter                           |

  Scenario: User filters recipes and receives only fully cookable recipes
    When the user filters recipes to show only those fully cookable with their current pantry
    Then the system returns "Omelette"
    And the system returns "Pancakes"
    And the system returns "Chicken Alfredo" 
    And the system does not return "Beef Stew"
    And the system does not return "Pasta Carbonara"

  Scenario: All returned recipes have every required ingredient covered by the pantry
    When the user filters recipes to show only those fully cookable with their current pantry
    Then each returned recipe has all of its required ingredients present in the user's pantry

  Scenario: No recipes are returned when the pantry cannot fully cover any recipe
    Given the user's pantry contains only the following items:
      | name   | quantity | unit |
      | Salt   | 50       | gram |
    When the user filters recipes to show only those fully cookable with their current pantry
    Then the system returns an empty list of recipes
    And the user does not receive an error message

  Scenario: Filter result changes after a new ingredient is added to the pantry
    Given the user's pantry also contains the following items:
      | name   | quantity | unit |
      | Pasta  | 200      | gram |
    When the user filters recipes to show only those fully cookable with their current pantry
    Then the system returns "Pasta Carbonara"

  Scenario: Filter result changes after an ingredient is removed from the pantry
    Given "Flour" has been removed from the user's pantry
    When the user filters recipes to show only those fully cookable with their current pantry
    Then the system does not return "Pancakes"
    And the system does not return "Chicken Alfredo"
    And the system returns "Omelette"

  Scenario Outline: Recipes requiring a missing ingredient are excluded from filtered results
    Given the user's pantry does not contain "<missing_ingredient>"
    When the user filters recipes to show only those fully cookable with their current pantry
    Then the system does not return "<recipe>"

    Examples:
      | missing_ingredient | recipe          |
      | Flour              | Pancakes        |
      | Chicken            | Chicken Alfredo |
      | Milk               | Pancakes        |

  Scenario: Unauthenticated user cannot filter recipes by pantry
    Given the user is not authenticated
    When the user attempts to filter recipes to show only those fully cookable with their current pantry
    Then the user receives an unauthorized error
    And no recipes are returned
