package com.example.project.partner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_usage_logs")
public class ApiUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String partnerName;

    @Column(nullable = false, length = 12)
    private String method;

    @Column(nullable = false, length = 240)
    private String endpoint;

    @Column(nullable = false)
    private int statusCode;

    @Column(nullable = false)
    private boolean successful;

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    protected ApiUsageLog() {
    }

    public ApiUsageLog(String partnerName, String method, String endpoint, int statusCode, boolean successful) {
        this.partnerName = partnerName;
        this.method = method;
        this.endpoint = endpoint;
        this.statusCode = statusCode;
        this.successful = successful;
    }

    public Long getId() {
        return id;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
}
