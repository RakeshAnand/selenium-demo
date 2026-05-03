package com.example.core.stepDefinitions;

import com.example.core.pages.HomePage;
import com.example.core.utils.DriverFactory;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class HomePageSteps {
    private HomePage homePage;

    @Then("user should see welcome message")
    public void user_should_see_welcome_message() {
        homePage = new HomePage(DriverFactory.getDriver());
        String message = homePage.getWelcomeMessage(); // implement this in HomePage
        Assert.assertTrue(message.contains("Welcome"), "Welcome message not found!");
    }
}
