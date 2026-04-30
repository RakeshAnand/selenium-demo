package com.example.stepDefinitions;

import com.example.pages.LoginPage;
import com.example.utils.DriverFactory;
import io.cucumber.java.en.*;

public class LoginSteps {
    private LoginPage loginPage;

    @Given("user is on login page")
    public void user_is_on_login_page() {
        // Reuse driver created in Hooks
        DriverFactory.getDriver().get("http://demo.guru99.com/V4/");
        loginPage = new LoginPage(DriverFactory.getDriver());
    }

    @When("user enters {string} and {string}")
    public void user_enters_and(String user, String pass) {
        loginPage.enterUsername(user);
        loginPage.enterPassword(pass);
    }

    @When("clicks login")
    public void clicks_login() {
        loginPage.clickLogin();
    }

    @Then("user should be navigated to home page")
    public void user_should_be_navigated_to_home_page() {
        // Assertions only, no quitDriver here
    }
}
