Feature: User can search recipes
  As a registered user
  I want to search for recipes by keyword, ingredient, or filter
  So that I can find recipes that match my needs and preferences

  Background:
    Given the user is authenticated
    And the following recipes exist in the system:
      | name                  | cuisine  | dietary_tag  | main_ingredient |
      | Spaghetti Carbonara   | Italian  | none         | Pasta           |
      | Chicken Tikka Masala  | Indian   | gluten-free  | Chicken         |
      | Margherita Pizza      | Italian  | vegetarian   | Tomato          |
      | Beef Tacos            | Mexican  | none         | Beef            |
      | Tofu Stir Fry         | Japanese | vegan        | Tofu            |

  Scenario: User searches for recipes by keyword and receives matching results
    When the user searches for recipes with the keyword "Chicken"
    Then the system returns a list containing "Chicken Tikka Masala"
    And no recipes without "Chicken" in their name or ingredients are included

  Scenario: User searches for recipes by keyword that matches no results
    When the user searches for recipes with the keyword "Lobster"
    Then the system returns an empty list of recipes
    And the user does not receive an error message

  Scenario: User searches for recipes filtered by cuisine
    When the user searches for recipes with the cuisine filter "Italian"
    Then the system returns only recipes belonging to the "Italian" cuisine
    And no recipes from other cuisines are included in the results

  Scenario: User searches for recipes filtered by dietary tag
    When the user searches for recipes with the dietary tag "vegan"
    Then the system returns only recipes tagged as "vegan"
    And no recipes without the "vegan" tag are included in the results

  Scenario: User searches for recipes by keyword and dietary tag combined
    When the user searches for recipes with the keyword "Pizza" and the dietary tag "vegetarian"
    Then the system returns only recipes that match both criteria
    And no recipes that fail either criterion are included in the results

  Scenario Outline: User searches for recipes using different cuisine filters
    When the user searches for recipes with the cuisine filter "<cuisine>"
    Then the system returns at least one recipe belonging to the "<cuisine>" cuisine

    Examples:
      | cuisine  |
      | Italian  |
      | Indian   |
      | Mexican  |
      | Japanese |

  Scenario: Search results include required ingredient information
    When the user searches for recipes with the keyword "Tacos"
    Then each returned recipe includes a list of required ingredients

  Scenario: Unauthenticated user cannot search for recipes
    Given the user is not authenticated
    When the user attempts to search for recipes with the keyword "Pasta"
    Then the user receives an unauthorized error
    And no recipes are returned
