package com.example.ui.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentJsonParser {

    public static TestStats parse(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            if (content.trim().isEmpty()) {
                System.err.println("File is empty: " + filePath);
                return new TestStats(0, 0, 0, 0, "File Empty");
            }

            JSONArray tests = new JSONArray(content);
            int passed = 0, failed = 0, skipped = 0;

            for (int i = 0; i < tests.length(); i++) {
                JSONObject feature = tests.getJSONObject(i);
                // Extent 5+ uses 'children' for scenarios
                if (feature.has("children")) {
                    JSONArray scenarios = feature.getJSONArray("children");
                    for (int j = 0; j < scenarios.length(); j++) {
                        String status = scenarios.getJSONObject(j).optString("status", "clear").toLowerCase();
                        if (status.contains("pass"))
                            passed++;
                        else if (status.contains("fail"))
                            failed++;
                        else
                            skipped++;
                    }
                }
            }
            return new TestStats(passed + failed + skipped, passed, failed, skipped, LocalDateTime.now().toString());
        } catch (Exception e) {
            e.printStackTrace(); // This will show the exact error in your IDE console
            return new TestStats(0, 0, 0, 0, "Error");
        }
    }
}