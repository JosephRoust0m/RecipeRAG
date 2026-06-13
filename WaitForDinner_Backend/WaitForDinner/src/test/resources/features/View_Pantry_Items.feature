Feature: User can view pantry items
  As a registered user
  I want to view the items currently in my pantry
  So that I know what ingredients I have available

  Background:
    Given the user is authenticated
    And the user's pantry contains the following items:
      | name    | quantity | unit  |
      | Eggs    | 12       | unit  |
      | Milk    | 2        | litre |
      | Flour   | 500      | gram  |
      | Butter  | 200      | gram  |

  Scenario: User views all items in their pantry
    When the user requests their pantry items
    Then the system returns a list of 4 pantry items
    And each item includes a name, quantity, and unit

  Scenario: Pantry items are returned in alphabetical order by name
    When the user requests their pantry items
    Then the first item in the list is "Butter"
    And the last item in the list is "Milk"

  Scenario: User views a specific pantry item by name
    When the user requests the pantry item "Eggs"
    Then the system returns an item with the name "Eggs", a quantity of 12, and a unit of "unit"

  Scenario: User requests a pantry item that does not exist
    When the user requests the pantry item "Olive Oil"
    Then the user receives an error message indicating the item was not found
    And no item data is returned

  Scenario: User with an empty pantry receives an empty list
    Given the user's pantry is empty
    When the user requests their pantry items
    Then the system returns an empty list of pantry items
    And the user does not receive an error message

  Scenario: Pantry list reflects items added since last viewed
    Given the user's pantry also contains the following items:
      | name        | quantity | unit |
      | Olive Oil   | 250      | ml   |
    When the user requests their pantry items
    Then the system returns a list of 5 pantry items
    And the list includes "Olive Oil"

  Scenario: Pantry list reflects items removed since last viewed
    Given "Milk" has been removed from the user's pantry
    When the user requests their pantry items
    Then the system returns a list of 3 pantry items
    And the list does not include "Milk"

  Scenario Outline: User views details of each pantry item individually
    When the user requests the pantry item "<name>"
    Then the system returns an item with the name "<name>", a quantity of <quantity>, and a unit of "<unit>"

    Examples:
      | name   | quantity | unit  |
      | Eggs   | 12       | unit  |
      | Milk   | 2        | litre |
      | Flour  | 500      | gram  |
      | Butter | 200      | gram  |

  Scenario: Unauthenticated user cannot view pantry items
    Given the user is not authenticated
    When the user attempts to request their pantry items
    Then the user receives an unauthorized error
    And no pantry items are returned
