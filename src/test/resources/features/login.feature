Feature: Login functionality

    Scenario: Valid login
        Given user is on login page
        When user enters credentials
        And clicks login
        Then user should be navigated to home page
        Then user should see welcome message
