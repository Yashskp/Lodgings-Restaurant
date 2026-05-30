package com.example.project.partner;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PartnerServiceRepository extends JpaRepository<PartnerService, Long> {

    List<PartnerService> findByStatus(PartnerServiceStatus status);

    long countByStatus(PartnerServiceStatus status);

    long countByServiceTypeAndStatus(PartnerServiceType serviceType, PartnerServiceStatus status);

    List<PartnerService> findTop10ByOrderByCreatedAtDesc();

    Optional<PartnerService> findByPartnerNameAndExternalReference(String partnerName, String externalReference);

    @Query("""
            select new com.example.project.partner.ServiceTypeSummary(s.serviceType, count(s), avg(s.price))
            from PartnerService s
            where s.status = com.example.project.partner.PartnerServiceStatus.ACTIVE
            group by s.serviceType
            order by count(s) desc
            """)
    List<ServiceTypeSummary> summarizeActiveServicesByType();
}
