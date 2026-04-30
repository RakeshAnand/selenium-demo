package com.example.stepDefinitions;

import com.example.pages.HomePage;
import com.example.utils.DriverFactory;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class HomePageSteps {
    HomePage homePage;

    @Then("user should see welcome message")
    public void user_should_see_welcome_message() {
        HomePage homePage = new HomePage(DriverFactory.getDriver());
        String actualText = homePage.getDashboardText();
        Assert.assertTrue(actualText.contains("Welcome"));
    }

}
