Feature: Login functionality

    Scenario: Valid login
        Given user is on login page
        When user enters "admin" and "password123"
        And clicks login
        Then user should be navigated to home page
