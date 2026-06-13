Feature: Ingredient quantities are updated after a recipe is marked as cooked
  As a registered user
  I want my pantry ingredient quantities to be automatically updated when I mark a recipe as cooked
  So that my pantry accurately reflects what I have left after cooking

  Background:
    Given the user is authenticated
    And the user's pantry contains the following items:
      | name    | quantity | unit  |
      | Eggs    | 6        | unit  |
      | Butter  | 200      | gram  |
      | Flour   | 500      | gram  |
      | Milk    | 1        | litre |
    And the following recipes exist in the system:
      | name      | ingredient | amount_required | unit  |
      | Pancakes  | Eggs       | 2               | unit  |
      | Pancakes  | Butter     | 50              | gram  |
      | Pancakes  | Flour      | 200             | gram  |
      | Pancakes  | Milk       | 300             | ml    |
      | Omelette  | Eggs       | 3               | unit  |
      | Omelette  | Butter     | 30              | gram  |

  Scenario: Pantry quantities are decremented for all ingredients after marking a recipe as cooked
    When the user marks "Pancakes" as cooked
    Then the quantity of "Eggs" in the pantry is 4
    And the quantity of "Butter" in the pantry is 150 gram
    And the quantity of "Flour" in the pantry is 300 gram

  Scenario: An ingredient is removed from the pantry when its quantity reaches zero after cooking
    Given the user's pantry contains the following items:
      | name  | quantity | unit |
      | Eggs  | 3        | unit |
      | Butter| 30       | gram |
    When the user marks "Omelette" as cooked
    Then "Eggs" is no longer in the user's pantry
    And "Butter" is no longer in the user's pantry

  Scenario: Pantry quantities reflect cooking the same recipe multiple times
    When the user marks "Pancakes" as cooked
    And the user marks "Pancakes" as cooked
    Then the quantity of "Eggs" in the pantry is 2
    And the quantity of "Flour" in the pantry is 100 gram

  Scenario: Marking a second recipe as cooked further decrements shared ingredients
    When the user marks "Pancakes" as cooked
    And the user marks "Omelette" as cooked
    Then the quantity of "Eggs" in the pantry is 1
    And the quantity of "Butter" in the pantry is 120 gram

  Scenario: System prevents marking a recipe as cooked when pantry has insufficient ingredients
    Given the user's pantry contains the following items:
      | name  | quantity | unit |
      | Eggs  | 1        | unit |
      | Butter| 10       | gram |
    When the user marks "Pancakes" as cooked
    Then the user receives an error message indicating insufficient pantry ingredients
    And the pantry quantities remain unchanged

  Scenario: Pantry history records a removal event for each ingredient consumed after cooking
    When the user marks "Pancakes" as cooked
    Then the pantry history contains a removal event for "Eggs" with a quantity of 2
    And the pantry history contains a removal event for "Butter" with a quantity of 50

  Scenario Outline: Correct quantity remains in pantry after cooking a recipe
    When the user marks "<recipe>" as cooked
    Then the quantity of "<ingredient>" in the pantry is <remaining_quantity>

    Examples:
      | recipe    | ingredient | remaining_quantity |
      | Pancakes  | Eggs       | 4                  |
      | Pancakes  | Flour      | 300                |
      | Omelette  | Eggs       | 3                  |
      | Omelette  | Butter     | 170                |

  Scenario: Unauthenticated user cannot mark a recipe as cooked
    Given the user is not authenticated
    When the user attempts to mark "Pancakes" as cooked
    Then the user receives an unauthorized error
    And no pantry quantities are updated
