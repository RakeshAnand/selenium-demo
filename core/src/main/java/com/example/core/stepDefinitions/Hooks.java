package com.example.core.stepDefinitions;

import com.example.core.utils.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario; //  import Scenario
import org.openqa.selenium.OutputType; //  import OutputType
import org.openqa.selenium.TakesScreenshot; //  import TakesScreenshot
import org.openqa.selenium.chrome.ChromeDriver;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        // 1. Check if the scenario has the @API tag
        boolean isApiTest = scenario.getSourceTagNames().contains("@api") ||
                scenario.getSourceTagNames().contains("@API");

        if (!isApiTest) {
            // 2. Only initialize the driver if it's NOT an API test
            // Note: Use your DriverFactory to handle the instantiation logic
            DriverFactory.getDriver();
            System.out.println("UI Test detected: Browser Launched.");
        } else {
            System.out.println("API Test detected: Skipping Browser Launch.");
        }
    }

    @After
    public void tearDown() {
        DriverFactory.quitDriver(); // quit driver once
    }

    @AfterStep
    public void addScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            final byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failed Step Screenshot");
        }
    }
}
