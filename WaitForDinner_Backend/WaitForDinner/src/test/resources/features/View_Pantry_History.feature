Feature: User can view pantry history
  As a registered user
  I want to view the history of changes to my pantry
  So that I can track what ingredients I have added and removed over time

  Background:
    Given the user is authenticated
    And the user's pantry history contains the following events:
      | item    | action  | quantity | unit  | date       |
      | Eggs    | added   | 12       | unit  | 2025-01-01 |
      | Milk    | added   | 2        | litre | 2025-01-02 |
      | Eggs    | removed | 6        | unit  | 2025-01-03 |
      | Flour   | added   | 500      | gram  | 2025-01-04 |
      | Milk    | removed | 2        | litre | 2025-01-05 |

  Scenario: User views their full pantry history
    When the user requests their pantry history
    Then the system returns a list of 5 pantry events
    And each event includes an item name, action, quantity, unit, and date

  Scenario: User views pantry history filtered by item name
    When the user requests their pantry history filtered by item "Eggs"
    Then the system returns only events involving "Eggs"
    And no events for other items are included in the results

  Scenario Outline: User filters pantry history by date range
    When the user requests their pantry history between "<start_date>" and "<end_date>"
    Then the system returns only events that occurred within that date range

    Examples:
      | start_date | end_date   |
      | 2025-01-01 | 2025-01-02 |
      | 2025-01-03 | 2025-01-05 |
      | 2025-01-02 | 2025-01-04 |

  Scenario: User with no pantry history receives an empty history
    Given the user has no pantry history
    When the user requests their pantry history
    Then the system returns an empty list of pantry events
    And the user does not receive an error message

  Scenario: Unauthenticated user cannot view pantry history
    Given the user is not authenticated
    When the user attempts to request their pantry history
    Then the user receives an unauthorized error
    And no pantry history is returned
