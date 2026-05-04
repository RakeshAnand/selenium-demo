package com.example.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import com.example.ui.utils.ExtentJsonParser;
import com.example.ui.utils.TestStats;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TestController {

    private final String PROJECT_ROOT = System.getProperty("user.dir").contains("ui")
            ? new File(System.getProperty("user.dir")).getParent()
            : System.getProperty("user.dir");

    @GetMapping("/run-tests")
    @ResponseBody
    public String runTests(@RequestParam(value = "feature", required = false) String feature) {
        try {
            System.setProperty("webdriver.headless", "true");

            // 1. Define paths
            String historyDir = PROJECT_ROOT + "/reports/history";
            String latestExtentJson = PROJECT_ROOT + "/reports/extent.json";
            String cucumberJson = PROJECT_ROOT + "/reports/cucumber.json";

            new File(historyDir).mkdirs();

            // 2. Configure Cucumber Arguments (Removed historyFile from here)
            List<String> argsList = new ArrayList<>();
            argsList.add("--glue");
            argsList.add("com.example.core.stepDefinitions");

            String featurePath = (feature != null && !feature.equals("All Features"))
                    ? "classpath:features/" + feature
                    : "classpath:features";
            argsList.add(featurePath);

            argsList.add("--plugin");
            argsList.add("pretty");
            argsList.add("--plugin");
            argsList.add("json:" + cucumberJson);
            argsList.add("--plugin");
            argsList.add("com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:");

            // 3. Execute Tests
            io.cucumber.core.cli.Main.run(argsList.toArray(new String[0]),
                    Thread.currentThread().getContextClassLoader());

            // 4. IMPORTANT: Manual copy of Extent JSON to History
            // This ensures the history file has the "children" structure your parser
            // expects
            File source = new File(latestExtentJson);
            if (source.exists()) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                File dest = new File(historyDir + "/run_" + timestamp + ".json");
                java.nio.file.Files.copy(source.toPath(), dest.toPath());
            }

            return "Tests Completed. <a href='/dashboard'>Go to Dashboard</a>";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // 1. Construct the path
        String jsonPath = PROJECT_ROOT + "/reports/extent.json";
        File jsonFile = new File(jsonPath);

        // 2. Debug Logging (Check your IntelliJ/Console output!)
        System.out.println("DEBUG: Looking for report at: " + jsonFile.getAbsolutePath());
        System.out.println("DEBUG: File exists? " + jsonFile.exists());

        TestStats stats;
        if (jsonFile.exists() && jsonFile.length() > 0) {
            stats = ExtentJsonParser.parse(jsonPath);
            // Fallback: If parser returns null or empty stats
            if (stats == null || stats.getTotal() == 0) {
                System.out.println("DEBUG: Parser returned 0 stats. Check JSON content.");
            }
        } else {
            stats = new TestStats(0, 0, 0, 0, "No Runs Found");
        }

        model.addAttribute("stats", stats);
        model.addAttribute("history", getTestHistory());
        return "index";
    }

    /**
     * Helper method to scan the history directory and parse all previous run
     * results.
     */
    private List<TestStats> getTestHistory() {
        File folder = new File(PROJECT_ROOT + "/reports/history");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                // Sort by last modified time so the trend chart goes from oldest to newest
                .sorted(Comparator.comparingLong(File::lastModified))
                .map(file -> {
                    // Assuming your parser can handle the cucumber JSON format or
                    // that you've set up the history files to be compatible
                    return ExtentJsonParser.parse(file.getAbsolutePath());
                })
                .collect(Collectors.toList());
    }
}