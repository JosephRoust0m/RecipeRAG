Feature: Display possible recipes when pantry is empty
  As a registered user
  I want to see recipe suggestions even when my pantry is empty
  So that I can discover new recipes and know what ingredients to buy

  Background:
    Given the user is authenticated
    And the user's pantry is empty

  Scenario: System suggests recipes when pantry is empty
    When the user requests recipe suggestions
    Then the system returns a non-empty list of recipes
    And each recipe includes a list of required ingredients

  Scenario: Recipes displayed when pantry is empty are marked as requiring ingredients
    When the user requests recipe suggestions
    Then each suggested recipe is indicated as requiring ingredients to be purchased

  Scenario: User with empty pantry receives different suggestions than user with stocked pantry
    Given a second authenticated user has the following items in their pantry:
      | name   | quantity | unit |
      | Eggs   | 6        | unit | 
      | Butter | 100      | gram |
    When both users request recipe suggestions
    Then the suggestions returned to the first user differ from those returned to the second user

  Scenario: System does not return an error when pantry is empty and recipes are requested
    When the user requests recipe suggestions
    Then the user does not receive an error message
    And the system returns a valid response

  Scenario Outline: System returns recipes matching cuisine preference even with empty pantry
    Given the user has set their cuisine preference to "<cuisine>"
    When the user requests recipe suggestions
    Then the system returns at least one recipe matching the "<cuisine>" cuisine

    Examples:
      | cuisine  |
      | Italian  |
      | Japanese |
      | Mexican  |

  Scenario: Unauthenticated user cannot request recipe suggestions
    Given the user is not authenticated
    When the user attempts to request recipe suggestions
    Then the user receives an unauthorized error
    And no recipes are returned
