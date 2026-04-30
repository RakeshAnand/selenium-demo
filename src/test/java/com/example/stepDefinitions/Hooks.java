package com.example.stepDefinitions;

import com.example.utils.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Scenario; //  import Scenario
import org.openqa.selenium.OutputType; //  import OutputType
import org.openqa.selenium.TakesScreenshot; //  import TakesScreenshot

public class Hooks {

    @Before
    public void setUp() {
        DriverFactory.getDriver(); // initialize driver once
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
