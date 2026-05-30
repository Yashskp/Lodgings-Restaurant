package com.example.project.partner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PartnerServiceResponse(
        Long id,
        String partnerName,
        String externalReference,
        PartnerServiceType serviceType,
        String title,
        String description,
        String location,
        BigDecimal price,
        PartnerServiceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    static PartnerServiceResponse from(PartnerService service) {
        return new PartnerServiceResponse(
                service.getId(),
                service.getPartnerName(),
                service.getExternalReference(),
                service.getServiceType(),
                service.getTitle(),
                service.getDescription(),
                service.getLocation(),
                service.getPrice(),
                service.getStatus(),
                service.getCreatedAt(),
                service.getUpdatedAt());
    }
}
