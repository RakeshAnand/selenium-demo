package com.example.ui.controller;

import com.example.ui.utils.ExtentJsonParser;
import com.example.ui.utils.TestStats;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

        // Adding the stats object to the model for Thymeleaf
        model.addAttribute("stats", stats);

        // Explicitly adding individual counts (helps if your HTML uses ${passed} etc.)
        model.addAttribute("total", stats.getTotal());
        model.addAttribute("passed", stats.getPassed());
        model.addAttribute("failed", stats.getFailed());
        model.addAttribute("skipped", stats.getSkipped());
        model.addAttribute("lastRunTime", stats.getLastRunTime());

        return "index";
    }
}