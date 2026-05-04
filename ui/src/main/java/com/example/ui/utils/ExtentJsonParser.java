package com.example.ui.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ExtentJsonParser {

    // Formatter for readable chart labels (e.g., 04 May 12:50)
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm");

    public static TestStats parse(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new TestStats(0, 0, 0, 0, "No Data");
            }

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray features = new JSONArray(content);
            int passed = 0, failed = 0, skipped = 0;

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);

                // Fix: Changed "children" to "elements"
                if (feature.has("elements")) {
                    JSONArray scenarios = feature.getJSONArray("elements");

                    for (int j = 0; j < scenarios.length(); j++) {
                        JSONObject scenario = scenarios.getJSONObject(j);
                        JSONArray steps = scenario.getJSONArray("steps");

                        boolean isScenarioFailed = false;
                        boolean isScenarioSkipped = false;

                        // In Cucumber JSON, a scenario's status is determined by its steps
                        for (int k = 0; k < steps.length(); k++) {
                            String stepStatus = steps.getJSONObject(k)
                                    .getJSONObject("result")
                                    .getString("status");

                            if (stepStatus.equalsIgnoreCase("failed")) {
                                isScenarioFailed = true;
                                break;
                            } else if (stepStatus.equalsIgnoreCase("skipped")
                                    || stepStatus.equalsIgnoreCase("pending")) {
                                isScenarioSkipped = true;
                            }
                        }

                        if (isScenarioFailed)
                            failed++;
                        else if (isScenarioSkipped)
                            skipped++;
                        else
                            passed++;
                    }
                }
            }

            // Get actual file time for the chart label
            String formattedDate = Files.getLastModifiedTime(file.toPath())
                    .toInstant().atZone(ZoneId.systemDefault()).format(formatter);

            return new TestStats(passed + failed + skipped, passed, failed, skipped, formattedDate);

        } catch (Exception e) {
            e.printStackTrace();
            return new TestStats(0, 0, 0, 0, "Error Parsing");
        }
    }

    /**
     * Scans the history directory for past cucumber JSON reports.
     */
    public static List<TestStats> getPastExecutions123() {
        List<TestStats> history = new ArrayList<>();

        // Use path relative to the project root (where reports folder likely sits)
        // If your folder is inside 'ui', use "ui/reports/history"
        File folder = new File("../reports/history");

        // Debug print to your console to verify path
        System.out.println("Checking for history in: " + folder.getAbsolutePath());

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    history.add(parse(file.getAbsolutePath()));
                }
            }
        } else {
            System.err.println("History directory NOT FOUND at: " + folder.getAbsolutePath());
        }

        // Sort by timestamp so the trend moves forward in time
        history.sort(Comparator.comparing(TestStats::getLastRunTime));

        return history;
    }

    public static List<TestStats> getPastExecutions() {
        List<TestStats> history = new ArrayList<>();

        // 1. Get the path where the application is actually running
        String currentDir = System.getProperty("user.dir");
        File root;

        // 2. If we are in the 'ui' folder, go to the parent. If not, stay here.
        if (currentDir.endsWith("ui")) {
            root = new File(currentDir).getParentFile();
        } else {
            root = new File(currentDir);
        }

        // 3. Construct the clean, absolute path to reports/history
        File historyFolder = new File(root, "reports/history");

        // This will now print the clean path you want:
        // C:\Users\DELL\...\reports\history
        System.out.println("Checking for history in: " + historyFolder.getAbsolutePath());

        if (historyFolder.exists() && historyFolder.isDirectory()) {
            File[] files = historyFolder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null && files.length > 0) {
                // Sort files by last modified (oldest first for trend)
                Arrays.sort(files, Comparator.comparingLong(File::lastModified));

                for (File file : files) {
                    history.add(parse(file.getAbsolutePath()));
                }
            } else {
                System.out.println("No history files found in: " + historyFolder.getAbsolutePath());
            }
        } else {
            System.err.println("Directory NOT FOUND: " + historyFolder.getAbsolutePath());
        }

        return history;
    }
}