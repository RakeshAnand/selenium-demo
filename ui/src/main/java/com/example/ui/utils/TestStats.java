package com.example.ui.utils;

public class TestStats {
    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private String lastRunTime;

    // The comprehensive constructor required by your HomeController
    public TestStats(int total, int passed, int failed, int skipped, String lastRunTime) {
        this.total = total;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.lastRunTime = lastRunTime;
    }

    // Default constructor for error handling
    public TestStats() {
        this(0, 0, 0, 0, "Never");
    }

    public int getTotal() {
        return total;
    }

    public int getPassed() {
        return passed;
    }

    public int getFailed() {
        return failed;
    }

    public int getSkipped() {
        return skipped;
    }

    public String getLastRunTime() {
        return lastRunTime;
    }
}