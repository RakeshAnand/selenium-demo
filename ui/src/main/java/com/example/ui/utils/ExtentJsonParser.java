package com.example.ui.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentJsonParser {

    // Added 'static' so HomeController can call it easily
    public static TestStats parse(String filePath) {
        int passed = 0;
        int failed = 0;
        int skipped = 0;

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            // cucumber.json is ALWAYS an array at the root
            JSONArray features = new JSONArray(content);

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                if (!feature.has("elements"))
                    continue;

                JSONArray scenarios = feature.getJSONArray("elements");
                for (int j = 0; j < scenarios.length(); j++) {
                    JSONObject scenario = scenarios.getJSONObject(j);

                    // We determine Scenario status by looking at its steps
                    if (!scenario.has("steps"))
                        continue;

                    JSONArray steps = scenario.getJSONArray("steps");
                    String finalStatus = "passed"; // Assume pass until proven otherwise

                    for (int k = 0; k < steps.length(); k++) {
                        JSONObject step = steps.getJSONObject(k);
                        if (step.has("result")) {
                            String stepStatus = step.getJSONObject("result").getString("status");

                            if (stepStatus.equalsIgnoreCase("failed")) {
                                finalStatus = "failed";
                                break; // One failure fails the whole scenario
                            } else if (stepStatus.equalsIgnoreCase("skipped")
                                    || stepStatus.equalsIgnoreCase("pending")) {
                                finalStatus = "skipped";
                            }
                        }
                    }

                    if (finalStatus.equals("passed"))
                        passed++;
                    else if (finalStatus.equals("failed"))
                        failed++;
                    else
                        skipped++;
                }
            }
        } catch (Exception e) {
            System.err.println("Dashboard Parse Error: " + e.getMessage());
            // If we hit an error, we still return the 'Never' time or current time
        }

        int total = passed + failed + skipped;
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new TestStats(total, passed, failed, skipped, now);
    }
}