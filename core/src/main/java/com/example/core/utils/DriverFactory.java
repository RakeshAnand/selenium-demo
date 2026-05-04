package com.example.core.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();

            // 1. Detect Environment & Controller Flags
            boolean isCloudRun = System.getenv("GITHUB_ACTIONS") != null;
            boolean isUIHeadless = Boolean.parseBoolean(System.getProperty("webdriver.headless", "false"));

            // 2. Apply Headless Arguments if either condition is true
            if (isCloudRun || isUIHeadless) {
                options.addArguments("--headless=new");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
            }

            driver = new ChromeDriver(options);

            // 3. Only maximize if the browser is visible
            if (!isCloudRun && !isUIHeadless) {
                driver.manage().window().maximize();
            }
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            // Clear the property so next run doesn't stay headless unless explicitly set
            System.clearProperty("webdriver.headless");
        }
    }
}