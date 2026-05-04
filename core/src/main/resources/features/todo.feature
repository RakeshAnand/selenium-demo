@api
Feature: Todo API Validation

    Scenario: Fetch and verify a specific Todo item
        Given the Todo API endpoint is "https://jsonplaceholder.typicode.com/todos/1"
        When I send a GET request
        Then the response status code should be 200
        And the response body should contain the following data:
            | userId    | 1                  |
            | id        | 1                  |
            | title     | delectus aut autem |
            | completed | false              |