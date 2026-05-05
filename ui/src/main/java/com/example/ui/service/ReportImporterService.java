package com.example.ui.service;

import com.example.ui.model.TestRun;
import com.example.ui.repository.TestRunRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class ReportImporterService implements CommandLineRunner {

    private final TestRunRepository repository;
    private final ObjectMapper objectMapper;

    // This points to your reports/history folder
    private final String reportPath = "../reports/history";

    public ReportImporterService(TestRunRepository repository) {
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> Scanning for new test reports in: " + reportPath);

        File folder = new File(reportPath);
        if (!folder.exists()) {
            System.err.println("Report directory not found!");
            return;
        }

        try (Stream<Path> paths = Files.walk(Paths.get(reportPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(this::importJsonFile);
        }
    }

    private void importJsonFile(Path path) {
        String fileName = path.getFileName().toString();
        if (repository.existsByFileName(fileName))
            return;

        try {
            JsonNode root = objectMapper.readTree(path.toFile());

            int totalPassed = 0;
            int totalFailed = 0;

            // 1. Loop through each Feature (Level 0)
            for (JsonNode feature : root) {
                JsonNode scenarios = feature.get("children");

                if (scenarios != null && scenarios.isArray()) {
                    // 2. Loop through each Scenario (Level 1)
                    for (JsonNode scenario : scenarios) {
                        String status = scenario.get("status").asText();

                        if ("PASS".equalsIgnoreCase(status)) {
                            totalPassed++;
                        } else if ("FAIL".equalsIgnoreCase(status)) {
                            totalFailed++;
                        }
                    }
                }
            }

            // 3. Create and save the TestRun record
            TestRun run = new TestRun();
            run.setFileName(fileName);
            run.setPassed(totalPassed);
            run.setFailed(totalFailed);
            run.setTotalTests(totalPassed + totalFailed);
            run.setExecutionTime(java.time.LocalDateTime.now());

            repository.save(run);
            System.out.println("Imported " + fileName + " - Passed: " + totalPassed + ", Failed: " + totalFailed);

        } catch (Exception e) {
            System.err.println("Error parsing " + fileName + ": " + e.getMessage());
        }
    }
}