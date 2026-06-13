Feature: User can filter recipes by dietary preference, cooking time, and difficulty level
  As a registered user
  I want to filter recipes by dietary preference, cooking time, and difficulty level
  So that I can find recipes that fit my lifestyle and available time

  Background:
    Given the user is authenticated
    And the following recipes exist in the system:
      | name                  | dietary_tag  | cooking_time_minutes | difficulty |
      | Omelette              | vegetarian   | 10                   | easy       |
      | Chicken Tikka Masala  | gluten-free  | 45                   | medium     |
      | Beef Wellington       | none         | 120                  | hard       |
      | Tofu Stir Fry         | vegan        | 20                   | easy       |
      | Pancakes              | vegetarian   | 15                   | easy       |
      | Mushroom Risotto      | vegan        | 40                   | medium     |
      | Lamb Tagine           | gluten-free  | 90                   | hard       |

  Scenario: User filters recipes by a single dietary preference
    When the user filters recipes by dietary preference "vegan"
    Then the system returns only recipes tagged as "vegan"
    And no recipes without the "vegan" tag are included in the results

  Scenario: User filters recipes by maximum cooking time
    When the user filters recipes by a maximum cooking time of 20 minutes
    Then the system returns only recipes with a cooking time of 20 minutes or less
    And no recipes with a cooking time greater than 20 minutes are included in the results

  Scenario: User filters recipes by difficulty level
    When the user filters recipes by difficulty level "easy"
    Then the system returns only recipes with the difficulty level "easy"
    And no recipes with a different difficulty level are included in the results

  Scenario: User filters recipes by dietary preference and difficulty level combined
    When the user filters recipes by dietary preference "vegetarian" and difficulty level "easy"
    Then the system returns only recipes that are tagged as "vegetarian" and have the difficulty level "easy"
    And no recipes that fail either criterion are included in the results

  Scenario: User filters recipes by dietary preference and maximum cooking time combined
    When the user filters recipes by dietary preference "gluten-free" and a maximum cooking time of 50 minutes
    Then the system returns only recipes tagged as "gluten-free" with a cooking time of 50 minutes or less
    And no recipes that fail either criterion are included in the results

  Scenario: User applies all three filters simultaneously
    When the user filters recipes by dietary preference "vegan", a maximum cooking time of 25 minutes, and difficulty level "easy"
    Then the system returns only recipes that satisfy all three criteria
    And no recipes that fail any criterion are included in the results

  Scenario: No recipes match the applied filters
    When the user filters recipes by dietary preference "vegan" and difficulty level "hard"
    Then the system returns an empty list of recipes
    And the user does not receive an error message

  Scenario Outline: User filters recipes by each supported difficulty level
    When the user filters recipes by difficulty level "<difficulty>"
    Then the system returns only recipes with the difficulty level "<difficulty>"

    Examples:
      | difficulty |
      | easy       |
      | medium     |
      | hard       |

  Scenario Outline: User filters recipes by each supported dietary preference
    When the user filters recipes by dietary preference "<dietary_tag>"
    Then the system returns only recipes tagged as "<dietary_tag>"

    Examples:
      | dietary_tag  |
      | vegan        |
      | vegetarian   |
      | gluten-free  |

  Scenario: Unauthenticated user cannot filter recipes
    Given the user is not authenticated
    When the user attempts to filter recipes by dietary preference "vegan"
    Then the user receives an unauthorized error
    And no recipes are returned
