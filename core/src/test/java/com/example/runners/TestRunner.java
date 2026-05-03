package com.example.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Master Test Runner for Cucumber + TestNG + ExtentReports
 * Generates:
 * - cucumber.json (for Jenkins publishing)
 * - Extent Spark HTML report
 * - Extent JSON report (parsed by dashboard)
 */
@CucumberOptions(features = "classpath:features", // Change from "src/test/resources/features" to this
                glue = "com.example.core.stepDefinitions", plugin = {
                                "pretty",
                                "html:target/cucumber-reports.html",
                                "json:target/cucumber.json",
                                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
                })
public class TestRunner extends AbstractTestNGCucumberTests {
}
