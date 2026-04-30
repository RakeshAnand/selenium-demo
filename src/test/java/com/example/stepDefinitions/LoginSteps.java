package com.example.stepDefinitions;

import com.example.pages.LoginPage;
import com.example.utils.DriverFactory;
import com.example.utils.ConfigReader;
import io.cucumber.java.en.*;

public class LoginSteps {
    private LoginPage loginPage;

    @Given("user is on login page")
    public void user_is_on_login_page() {
        String url = ConfigReader.getProperty("url");
        DriverFactory.getDriver().get(url);
        loginPage = new LoginPage(DriverFactory.getDriver());
    }

    @When("user enters credentials")
    public void user_enters_credentials_from_config() {
        String username = ConfigReader.getProperty("username");
        String password = ConfigReader.getProperty("password");
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
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
