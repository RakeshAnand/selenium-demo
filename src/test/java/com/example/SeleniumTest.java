package com.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SeleniumTest {
    private WebDriver driver;

    @BeforeClass
    public void setup() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\DELL\\Downloads\\Projects\\selenium-demo\\demo\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @Test
    public void openGoogle() {
        driver.get("https://www.google.com");
        Assert.assertTrue(driver.getTitle().contains("Google"));
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
