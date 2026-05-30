package com.example.project.partner;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApiUsageLogRepository extends JpaRepository<ApiUsageLog, Long> {

    long countBySuccessful(boolean successful);

    long countByRequestedAtAfter(java.time.LocalDateTime requestedAt);

    long countBySuccessfulAndRequestedAtAfter(boolean successful, java.time.LocalDateTime requestedAt);

    List<ApiUsageLog> findTop25ByOrderByRequestedAtDesc();

    @Query("""
            select new com.example.project.partner.EndpointUsageSummary(l.endpoint, l.method, count(l), sum(case when l.successful = true then 1 else 0 end))
            from ApiUsageLog l
            group by l.endpoint, l.method
            order by count(l) desc
            """)
    List<EndpointUsageSummary> summarizeByEndpoint();

    @Query("""
            select new com.example.project.partner.PartnerUsageSummary(l.partnerName, count(l), sum(case when l.successful = true then 1 else 0 end))
            from ApiUsageLog l
            group by l.partnerName
            order by count(l) desc
            """)
    List<PartnerUsageSummary> summarizeByPartner();
}
