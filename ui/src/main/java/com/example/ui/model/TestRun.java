package com.example.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // ADD THIS
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true) // ADD THIS
public class TestRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @JsonProperty("total_tests")
    private int totalTests;

    private int passed;
    private int failed;

    private LocalDateTime executionTime;

    // Standard Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    } // THIS WAS MISSING

    public int getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }
}