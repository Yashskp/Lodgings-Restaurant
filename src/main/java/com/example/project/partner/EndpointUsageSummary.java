package com.example.project.partner;

public record EndpointUsageSummary(String endpoint, String method, long totalRequests, long successfulRequests) {

    public long failedRequests() {
        return totalRequests - successfulRequests;
    }
}
