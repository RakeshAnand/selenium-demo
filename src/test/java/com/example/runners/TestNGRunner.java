package com.example.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "src/test/resources/features", // path to your .feature files
                glue = { "com.example.stepDefinitions" }, // package with step defs & hooks
                plugin = { "pretty", "html:target/cucumber-report.html" }, monochrome = true)
public class TestNGRunner extends AbstractTestNGCucumberTests {
}
