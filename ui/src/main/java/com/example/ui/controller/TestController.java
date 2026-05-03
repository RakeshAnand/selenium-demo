package com.example.ui.controller;

import io.cucumber.core.cli.Main;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ui.utils.ExtentJsonParser;
import com.example.ui.utils.TestStats;

import java.io.File;

@Controller
public class TestController {

    private final String PROJECT_ROOT = System.getProperty("user.dir").contains("ui")
            ? new File(System.getProperty("user.dir")).getParent()
            : System.getProperty("user.dir");

    @GetMapping("/run-tests")
    @ResponseBody
    public String runTests() {
        try {
            // Ensure the reports folder exists
            new File(PROJECT_ROOT + "/reports").mkdirs();

            String[] args = new String[] {
                    "--glue", "com.example.core.stepDefinitions",
                    "classpath:features",
                    "--plugin", "pretty",
                    // We still generate cucumber.json for Jenkins, but UI will ignore it
                    "--plugin", "json:" + PROJECT_ROOT + "/reports/cucumber.json",
                    "--plugin", "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
            };

            io.cucumber.core.cli.Main.run(args, Thread.currentThread().getContextClassLoader());
            Thread.sleep(3000); // Wait for Extent to write extent.json

            return "Tests Completed. <a href='/dashboard'>Go to Dashboard</a>";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(org.springframework.ui.Model model) {
        // Point to the EXTENT JSON file
        String jsonPath = PROJECT_ROOT + "/reports/extent.json";
        TestStats stats = ExtentJsonParser.parse(jsonPath);

        model.addAttribute("stats", stats);
        return "index";
    }
}