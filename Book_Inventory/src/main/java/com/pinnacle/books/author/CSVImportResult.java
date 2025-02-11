package com.pinnacle.books.author;

import java.util.List;

public class CSVImportResult {
    private int totalProcessed;
    private int successful;
    private int failed;
    private List<String> failedAuthors;

    public CSVImportResult(int totalProcessed, int successful, int failed, List<String> failedAuthors) {
        this.totalProcessed = totalProcessed;
        this.successful = successful;
        this.failed = failed;
        this.failedAuthors = failedAuthors;
    }

    // Getters and Setters
    public int getTotalProcessed() {
        return totalProcessed;
    }

    public int getSuccessful() {
        return successful;
    }

    public int getFailed() {
        return failed;
    }

    public List<String> getFailedAuthors() {
        return failedAuthors;
    }
}
