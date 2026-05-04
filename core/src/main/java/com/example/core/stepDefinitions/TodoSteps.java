package com.example.core.stepDefinitions;

import io.cucumber.java.en.Then;
import org.testng.Assert;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class TodoSteps {
    private String endpoint;
    private Response response;
    private RequestSpecification request;

    @Given("the Todo API endpoint is {string}")
    public void setEndpoint(String url) {
        this.endpoint = url;
        this.request = RestAssured.given();
    }

    @When("I send a GET request")
    public void sendGetRequest() {
        this.response = request.get(endpoint);
        // Log the response to console as requested in your use case
        System.out.println("Response Body: " + response.asPrettyString());
    }

    @Then("the response status code should be {int}")
    public void verifyStatusCode(int code) {
        Assert.assertEquals(response.getStatusCode(), code);
    }

    @Then("the response body should contain the following data:")
    public void verifyResponseBody(Map<String, String> expectedData) {
        // Asserting individual fields from the table
        response.then().body("userId", equalTo(Integer.parseInt(expectedData.get("userId"))));
        response.then().body("id", equalTo(Integer.parseInt(expectedData.get("id"))));
        response.then().body("title", equalTo(expectedData.get("title")));
        response.then().body("completed", equalTo(Boolean.parseBoolean(expectedData.get("completed"))));
    }
}