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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class TestController {

    // Use a relative path from the 'ui' module to the root 'reports' folder
    private final String REPORT_PATH = "reports/extent.html";

    @GetMapping("/run-tests")
    @ResponseBody
    public String runTests() {
        try {
            // We use the classpath for glue and features so it's module-independent
            String[] args = new String[] {
                    "--glue", "com.example.core.stepDefinitions",
                    "classpath:features",
                    "--plugin", "pretty",
                    // Explicitly tell Cucumber to put the JSON in the root reports folder
                    "--plugin", "json:../reports/cucumber.json",
                    "--plugin", "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
            };

            // Execute Cucumber
            byte exitStatus = Main.run(args, Thread.currentThread().getContextClassLoader());

            // CRITICAL: Wait for ExtentReports to finish writing the file
            Thread.sleep(1000);

            if (exitStatus == 0) {
                return "✅ Tests passed! <a href='/view-report' target='_blank'>View Extent Report</a>";
            } else {
                return "❌ Tests failed. <a href='/view-report' target='_blank'>View Report for Details</a>";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/view-report")
    public ResponseEntity<Resource> viewReport() {
        File file = new File(REPORT_PATH);
        if (file.exists()) {
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(resource);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
    }
}