Feature: System automatically ranks recipes by available pantry ingredients then dietary restrictions
  As a registered user
  I want the system to automatically rank recipes by how well they match my pantry ingredients and then by my dietary restrictions
  So that the most relevant and immediately cookable recipes appear first

  Background:
    Given the user is authenticated
    And the user's pantry contains the following items:
      | name    | quantity | unit  |
      | Eggs    | 6        | unit  |
      | Butter  | 200      | gram  |
      | Milk    | 1        | litre |
      | Flour   | 500      | gram  |
    And the user has the following dietary restrictions:
      | restriction  |
      | gluten-free  |
    And the following recipes exist in the system:
      | name               | required_ingredients          | dietary_tags               |
      | Omelette           | Eggs, Butter                  | gluten-free                |
      | Pancakes           | Eggs, Butter, Flour, Milk     | none                       |
      | Buttered Eggs      | Eggs, Butter                  | none                       |
      | Crepes             | Eggs, Butter, Flour, Milk     | none                       |
      | Beef Stew          | Beef, Potatoes, Carrots       | gluten-free                |

  Scenario: Recipes are ranked with highest pantry ingredient coverage first
    When the system ranks the available recipes
    Then recipes with more required ingredients present in the pantry appear before recipes with fewer
    And "Beef Stew" appears after "Omelette" in the ranked list

  Scenario: Among recipes with equal pantry coverage, recipes matching dietary restrictions rank higher
    When the system ranks the available recipes
    Then among recipes with equal pantry ingredient coverage, "Omelette" appears before "Buttered Eggs"

  Scenario: A fully cookable recipe that matches dietary restrictions ranks above a fully cookable recipe that does not
    When the system ranks the available recipes
    Then "Omelette" appears before "Buttered Eggs" in the ranked list

  Scenario: A fully cookable recipe ranks above a partially cookable recipe regardless of dietary tags
    When the system ranks the available recipes
    Then "Omelette" appears before "Beef Stew" in the ranked list

  Scenario: Rankings are updated when a new ingredient is added to the pantry
    Given the user's pantry also contains the following items:
      | name    | quantity | unit |
      | Beef    | 400      | gram |
      | Potatoes| 3        | unit |
      | Carrots | 4        | unit |
    When the system ranks the available recipes
    Then "Beef Stew" appears before "Omelette" in the ranked list

  Scenario: Rankings are updated when an ingredient is removed from the pantry
    Given "Butter" has been removed from the user's pantry
    When the system ranks the available recipes
    Then "Omelette" appears after recipes whose required ingredients are all still present in the pantry

  Scenario Outline: Recipes with more pantry ingredient matches rank above recipes with fewer matches
    Given the user's pantry satisfies "<higher_match_count>" ingredients of "<top_recipe>" and "<lower_match_count>" ingredients of "<bottom_recipe>"
    When the system ranks the available recipes
    Then "<top_recipe>" appears before "<bottom_recipe>" in the ranked list

    Examples:
      | top_recipe | higher_match_count | bottom_recipe | lower_match_count |
      | Pancakes   | 4                  | Beef Stew     | 0                 |
      | Omelette   | 2                  | Beef Stew     | 0                 |

  Scenario: Unauthenticated user cannot retrieve ranked recipes
    Given the user is not authenticated
    When the user attempts to retrieve ranked recipes
    Then the user receives an unauthorized error
    And no recipes are returned
