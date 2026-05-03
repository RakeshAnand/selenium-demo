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

            // 1. Set up ChromeOptions for a headless environment
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new"); // Run without a GUI
            options.addArguments("--no-sandbox"); // Bypass OS security model (required for CI)
            options.addArguments("--disable-dev-shm-usage"); // Overcome limited resource problems
            options.addArguments("--window-size=1920,1080"); // Set a fixed resolution

            // 2. Initialize driver with options
            driver = new ChromeDriver(options);

            // Optional: Keep maximize for local runs,
            // though window-size argument above handles it for headless
            driver.manage().window().maximize();
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}