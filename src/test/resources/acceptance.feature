Feature: Testing Monthly Expenses Script
  This should test out various scenarios of using my program to load files into monthly expenses excel

  Scenario: Loader should return empty value
    Given call loader with empty csv
    When we load expenses
    Then result should be empty

  Scenario: Loader should not be empty
    Given call loader with data csv
    When we load expenses
    Then result should not be empty