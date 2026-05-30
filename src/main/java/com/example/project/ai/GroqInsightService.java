package com.example.project.ai;

import com.example.project.partner.ApiUsageLogRepository;
import com.example.project.partner.PartnerServiceRepository;
import com.example.project.partner.PartnerServiceStatus;
import com.example.project.partner.PartnerServiceType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class GroqInsightService {

    private final ApiUsageLogRepository usageLogRepository;
    private final PartnerServiceRepository partnerServiceRepository;
    private final AiInsightRepository aiInsightRepository;
    private final GroqProperties groqProperties;
    private final RestClient restClient;

    public GroqInsightService(
            ApiUsageLogRepository usageLogRepository,
            PartnerServiceRepository partnerServiceRepository,
            AiInsightRepository aiInsightRepository,
            GroqProperties groqProperties,
            RestClient.Builder restClientBuilder) {
        this.usageLogRepository = usageLogRepository;
        this.partnerServiceRepository = partnerServiceRepository;
        this.aiInsightRepository = aiInsightRepository;
        this.groqProperties = groqProperties;
        this.restClient = restClientBuilder.baseUrl(groqProperties.getBaseUrl()).build();
    }

    @Transactional
    public AiInsight generateOperationalInsight() {
        if (groqProperties.getApiKey() == null || groqProperties.getApiKey().isBlank()) {
            throw new IllegalStateException("GROQ_API_KEY is not configured.");
        }

        String prompt = buildPrompt();
        Map<String, Object> request = Map.of(
                "model", groqProperties.getModel(),
                "temperature", 0.2,
                "max_tokens", 500,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are a concise hospitality operations analyst. Use only the supplied application metrics."),
                        Map.of("role", "user", "content", prompt)));

        Map<?, ?> response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + groqProperties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        String content = extractContent(response);

        return aiInsightRepository.save(new AiInsight("AI operations insight", content.strip(), groqProperties.getModel()));
    }

    private String extractContent(Map<?, ?> response) {
        if (response == null) {
            return "Groq returned an empty response.";
        }
        Object choices = response.get("choices");
        if (choices instanceof List<?> choiceList
                && !choiceList.isEmpty()
                && choiceList.get(0) instanceof Map<?, ?> choice
                && choice.get("message") instanceof Map<?, ?> message
                && message.get("content") instanceof String content
                && !content.isBlank()) {
            return content;
        }
        return "Groq did not return insight content.";
    }

    private String buildPrompt() {
        long totalRequests = usageLogRepository.count();
        long successfulRequests = usageLogRepository.countBySuccessful(true);
        long failedRequests = totalRequests - successfulRequests;
        LocalDateTime sinceYesterday = LocalDateTime.now().minusHours(24);
        long requestsLast24Hours = usageLogRepository.countByRequestedAtAfter(sinceYesterday);
        long failedLast24Hours = requestsLast24Hours - usageLogRepository.countBySuccessfulAndRequestedAtAfter(true, sinceYesterday);

        return """
                Analyze this lodgings and restaurant management application data.

                API traffic:
                - Total partner API requests: %d
                - Successful requests: %d
                - Failed requests: %d
                - Requests in last 24 hours: %d
                - Failed requests in last 24 hours: %d

                Services:
                - Active services: %d
                - Cancelled services: %d
                - Active lodging services: %d
                - Active restaurant services: %d

                Endpoint usage: %s
                Partner usage: %s
                Active service type summary: %s

                Return:
                1. A three sentence executive summary.
                2. Three specific observations.
                3. Three recommended actions for the admin team.
                """.formatted(
                totalRequests,
                successfulRequests,
                failedRequests,
                requestsLast24Hours,
                failedLast24Hours,
                partnerServiceRepository.countByStatus(PartnerServiceStatus.ACTIVE),
                partnerServiceRepository.countByStatus(PartnerServiceStatus.CANCELLED),
                partnerServiceRepository.countByServiceTypeAndStatus(PartnerServiceType.LODGING, PartnerServiceStatus.ACTIVE),
                partnerServiceRepository.countByServiceTypeAndStatus(PartnerServiceType.RESTAURANT, PartnerServiceStatus.ACTIVE),
                usageLogRepository.summarizeByEndpoint(),
                usageLogRepository.summarizeByPartner(),
                partnerServiceRepository.summarizeActiveServicesByType());
    }
}
