package com.example.ui.controller;

import com.example.ui.utils.ExtentJsonParser;
import com.example.ui.utils.TestStats;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    /**
     * This handles the main dashboard landing page.
     * It parses the cucumber.json from the shared reports folder
     * and passes the stats to the Thymeleaf template.
     */
    @GetMapping("/")
    public String index(Model model) {
        // Path points to the shared root reports folder relative to the 'ui' module
        String jsonPath = "../reports/cucumber.json";

        // Calling the static parse method from ExtentJsonParser
        TestStats stats = ExtentJsonParser.parse(jsonPath);

        // 2. Get historical stats (Mocking this for now, but you'd fetch from a DB or
        // list of files)
        List<TestStats> history = ExtentJsonParser.getPastExecutions();

        // Adding the stats object to the model for Thymeleaf
        model.addAttribute("stats", stats);
        model.addAttribute("history", history);// Pass the list to Thymeleaf

        // Explicitly adding individual counts (helps if your HTML uses ${passed} etc.)
        model.addAttribute("total", stats.getTotal());
        model.addAttribute("passed", stats.getPassed());
        model.addAttribute("failed", stats.getFailed());
        model.addAttribute("skipped", stats.getSkipped());
        model.addAttribute("lastRunTime", stats.getLastRunTime());

        // Add this line to pass the feature list to the UI
        // Path to the features folder in the 'core' module
        File featureFolder = new File("../core/src/main/resources/features");
        List<String> featureFiles = new ArrayList<>();

        if (featureFolder.exists() && featureFolder.isDirectory()) {
            File[] files = featureFolder.listFiles((dir, name) -> name.endsWith(".feature"));
            if (files != null) {
                for (File file : files) {
                    featureFiles.add(file.getName());
                }
            }
        }

        // This MUST match the 'features' variable name in your HTML th:each
        model.addAttribute("features", featureFiles);

        // Debug to console to see what path it is actually using
        try {
            System.out.println("Dashboard is reading from: " + new File(jsonPath).getCanonicalPath());
            System.out.println("Stats Found - Passed: " + stats.getPassed() + ", Failed: " + stats.getFailed());
            System.out.println("Features Found: " + featureFiles.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index";
    }

    @GetMapping("/view-report")
    @ResponseBody
    public String viewReport() {
        try {
            // 1. Get the root directory
            String currentDir = System.getProperty("user.dir");
            File root = currentDir.endsWith("ui") ? new File(currentDir).getParentFile() : new File(currentDir);

            // 2. Point to the actual report location in the root /reports folder
            // Change "extent.html" to whatever your report filename actually is
            File reportFile = new File(root, "reports/extent.html");

            if (!reportFile.exists()) {
                return "<h3>Report not found at: " + reportFile.getAbsolutePath() + "</h3>" +
                        "<p>Please run the tests first to generate the report.</p>";
            }

            // 3. Read and return the HTML content
            return new String(Files.readAllBytes(reportFile.toPath()));

        } catch (IOException e) {
            return "Error reading report: " + e.getMessage();
        }
    }
}