package com.example.project.partner;

public record ServiceTypeSummary(PartnerServiceType serviceType, long totalServices, Double averagePrice) {

    public double roundedAveragePrice() {
        if (averagePrice == null) {
            return 0;
        }
        return Math.round(averagePrice * 100.0) / 100.0;
    }
}
