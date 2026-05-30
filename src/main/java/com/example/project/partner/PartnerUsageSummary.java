package com.example.project.partner;

public record PartnerUsageSummary(String partnerName, long totalRequests, long successfulRequests) {

    public long failedRequests() {
        return totalRequests - successfulRequests;
    }

    public long successRate() {
        if (totalRequests == 0) {
            return 0;
        }
        return Math.round((successfulRequests * 100.0) / totalRequests);
    }
}
