package com.example.ui.controller;

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
            // 1. Enable Headless Mode
            System.setProperty("webdriver.headless", "true");

            // 2. Create unique filenames for history
            String timestamp = String.valueOf(System.currentTimeMillis());
            // Using ../ to ensure we hit the root reports folder, not the 'ui' module
            // folder
            String historyDir = "../reports/history";
            String historyFile = historyDir + "/run_" + timestamp + ".json";
            String latestJson = "../reports/cucumber.json";

            // 3. Ensure the history directory exists
            new File(historyDir).mkdirs();

            // 4. Configure Cucumber Arguments
            String[] args = new String[] {
                    "--glue", "com.example.core.stepDefinitions",
                    "classpath:features",
                    "--plugin", "pretty",
                    // Generate the 'Latest' report
                    "--plugin", "json:" + latestJson,
                    // Generate the 'History' report for the trend chart
                    "--plugin", "json:" + historyFile,
                    "--plugin", "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
            };

            // 5. Execute Tests
            byte exitCode = io.cucumber.core.cli.Main.run(args, Thread.currentThread().getContextClassLoader());
            if (exitCode != 0) {
                return "Tests Finished with Failures. <a href='/'>View Dashboard</a>";
            }

            // 6. Brief wait to ensure file system handles are closed
            Thread.sleep(2000);

            return "Tests Completed (Exit Code: " + exitCode + "). <a href='/'>Go to Dashboard</a>";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during test execution: " + e.getMessage();
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