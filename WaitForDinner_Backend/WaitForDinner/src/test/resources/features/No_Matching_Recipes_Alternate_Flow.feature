Feature: System informs user when there are no matching recipes for constraints
  As a registered user
  I want to be informed when no recipes match my applied constraints
  So that I understand why no results are shown and can adjust my search accordingly

  Background:
    Given the user is authenticated
    And the following recipes exist in the system:
      | name                 | dietary_tag  | cooking_time_minutes | difficulty | cuisine   |
      | Omelette             | vegetarian   | 10                   | easy       | French    |
      | Chicken Tikka Masala | gluten-free  | 45                   | medium     | Indian    |
      | Beef Wellington      | none         | 120                  | hard       | British   |
      | Tofu Stir Fry        | vegan        | 20                   | easy       | Japanese  |

  Scenario: System informs user when no recipes match a dietary preference filter
    When the user filters recipes by dietary preference "keto"
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints
    And the user does not receive an error message

  Scenario: System informs user when no recipes match a cooking time filter
    When the user filters recipes by a maximum cooking time of 5 minutes
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints

  Scenario: System informs user when no recipes match a difficulty filter
    When the user filters recipes by difficulty level "beginner"
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints

  Scenario: System informs user when a valid keyword search returns no results
    When the user searches for recipes with the keyword "Lobster"
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints

  Scenario: System informs user when combined filters return no results
    When the user filters recipes by dietary preference "vegan" and difficulty level "hard"
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints

  Scenario: System informs user when pantry-based filter returns no cookable recipes
    Given the user's pantry contains the following items:
      | name  | quantity | unit |
      | Salt  | 10       | gram |
    When the user filters recipes to show only those fully cookable with their current pantry
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints

  Scenario: System informs user when no recipes match their dietary restrictions during ranking
    Given the user has set their dietary restrictions to "raw vegan"
    When the system ranks the available recipes
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints

  Scenario Outline: System informs user when no recipes match a single constraint
    When the user filters recipes by dietary preference "<constraint>"
    Then the system returns an empty list of recipes
    And the system provides a message indicating no recipes match the applied constraints

    Examples:
      | constraint   |
      | keto         |
      | halal        |
      | raw vegan    |

  Scenario: Unauthenticated user cannot apply recipe filters
    Given the user is not authenticated
    When the user attempts to filter recipes by dietary preference "vegan"
    Then the user receives an unauthorized error
    And no recipes are returned
