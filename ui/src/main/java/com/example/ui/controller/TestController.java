package com.example.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ui.utils.ExtentJsonParser;
import com.example.ui.utils.TestStats;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    /**
     * Executes the Cucumber Test Suite.
     * Redirects back to dashboard with a success/error message.
     */
    @GetMapping("/run-tests")
    public String runTests(@RequestParam(value = "feature", required = false) String feature,
            RedirectAttributes redirectAttributes) {
        try {
            // Force Headless mode for Server-side execution
            System.setProperty("webdriver.chrome.driver", "path/to/chromedriver"); // Ensure driver is in path
            System.setProperty("webdriver.headless", "true");

            // 1. Define paths
            String reportsDir = PROJECT_ROOT + "/reports";
            String historyDir = reportsDir + "/history";
            String latestExtentJson = reportsDir + "/extent.json";

            new File(historyDir).mkdirs();

            // 2. Build Cucumber Arguments
            List<String> argsList = new ArrayList<>();

            // Glue code (Step Definitions)
            argsList.add("--glue");
            argsList.add("com.example.core.stepDefinitions");

            // Feature Selection
            String featurePath = (feature != null && !feature.equals("All Features"))
                    ? "classpath:features/" + feature
                    : "classpath:features";
            argsList.add(featurePath);

            // Plugins (Pretty console output + JSON + Extent)
            argsList.add("--plugin");
            argsList.add("pretty");
            argsList.add("--plugin");
            argsList.add("json:" + reportsDir + "/cucumber.json");
            argsList.add("--plugin");
            argsList.add("com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:");

            // 3. Execute Cucumber (Blocking call)
            byte exitCode = io.cucumber.core.cli.Main.run(argsList.toArray(new String[0]),
                    Thread.currentThread().getContextClassLoader());

            // 4. Archive to History
            File source = new File(latestExtentJson);
            if (source.exists()) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                File dest = new File(historyDir + "/run_" + timestamp + ".json");
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            redirectAttributes.addFlashAttribute("message", "Test Execution Completed with exit code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Execution Failed: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    /**
     * Displays the Analytics Dashboard.
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        String jsonPath = PROJECT_ROOT + "/reports/extent.json";
        File jsonFile = new File(jsonPath);

        TestStats stats;
        if (jsonFile.exists() && jsonFile.length() > 0) {
            stats = ExtentJsonParser.parse(jsonPath);
            if (stats == null) {
                stats = createEmptyStats("Parsing Error");
            }
        } else {
            stats = createEmptyStats("No Runs Found");
        }

        model.addAttribute("stats", stats);
        model.addAttribute("history", getTestHistory());

        // Add this line to pass the feature list to the UI
        model.addAttribute("features", getAvailableFeatures());

        // Return the name of your HTML file (if it's index.html, return "index")
        return "index";
    }

    private TestStats createEmptyStats(String msg) {
        return new TestStats(0, 0, 0, 0, msg);
    }

    /**
     * Scans history and returns a list of previous run stats.
     */
    private List<TestStats> getTestHistory() {
        File folder = new File(PROJECT_ROOT + "/reports/history");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified))
                .map(file -> ExtentJsonParser.parse(file.getAbsolutePath()))
                .filter(s -> s != null)
                .collect(Collectors.toList());
    }

    /**
     * Scans the classpath for available feature files and returns their names.
     */
    private List<String> getAvailableFeatures() {
        String featuresPath = PROJECT_ROOT + "/core/src/main/resources/features";
        File folder = new File(featuresPath);

        if (folder.exists() && folder.isDirectory()) {
            return Arrays.stream(folder.listFiles())
                    .filter(f -> f.getName().endsWith(".feature"))
                    .map(File::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}