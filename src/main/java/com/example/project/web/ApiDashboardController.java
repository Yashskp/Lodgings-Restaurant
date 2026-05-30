package com.example.project.web;

import com.example.project.ai.AiInsightRepository;
import com.example.project.ai.GroqInsightService;
import com.example.project.partner.ApiUsageLogRepository;
import com.example.project.partner.PartnerServiceRepository;
import com.example.project.partner.PartnerServiceStatus;
import com.example.project.partner.PartnerServiceType;
import java.time.LocalDateTime;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ApiDashboardController {

    private final ApiUsageLogRepository usageLogRepository;
    private final PartnerServiceRepository partnerServiceRepository;
    private final AiInsightRepository aiInsightRepository;
    private final GroqInsightService groqInsightService;

    public ApiDashboardController(
            ApiUsageLogRepository usageLogRepository,
            PartnerServiceRepository partnerServiceRepository,
            AiInsightRepository aiInsightRepository,
            GroqInsightService groqInsightService) {
        this.usageLogRepository = usageLogRepository;
        this.partnerServiceRepository = partnerServiceRepository;
        this.aiInsightRepository = aiInsightRepository;
        this.groqInsightService = groqInsightService;
    }

    @GetMapping("/api-dashboard")
    public String apiDashboard(Model model) {
        long totalRequests = usageLogRepository.count();
        long successfulRequests = usageLogRepository.countBySuccessful(true);
        long failedRequests = totalRequests - successfulRequests;
        LocalDateTime sinceYesterday = LocalDateTime.now().minusHours(24);
        long requestsLast24Hours = usageLogRepository.countByRequestedAtAfter(sinceYesterday);
        long successfulLast24Hours = usageLogRepository.countBySuccessfulAndRequestedAtAfter(true, sinceYesterday);
        model.addAttribute("totalRequests", totalRequests);
        model.addAttribute("successfulRequests", successfulRequests);
        model.addAttribute("failedRequests", failedRequests);
        model.addAttribute("successRate", percentage(successfulRequests, totalRequests));
        model.addAttribute("requestsLast24Hours", requestsLast24Hours);
        model.addAttribute("failedLast24Hours", requestsLast24Hours - successfulLast24Hours);
        model.addAttribute("activeServices", partnerServiceRepository.countByStatus(PartnerServiceStatus.ACTIVE));
        model.addAttribute("cancelledServices", partnerServiceRepository.countByStatus(PartnerServiceStatus.CANCELLED));
        model.addAttribute("lodgingServices", partnerServiceRepository.countByServiceTypeAndStatus(PartnerServiceType.LODGING, PartnerServiceStatus.ACTIVE));
        model.addAttribute("restaurantServices", partnerServiceRepository.countByServiceTypeAndStatus(PartnerServiceType.RESTAURANT, PartnerServiceStatus.ACTIVE));
        model.addAttribute("endpointSummaries", usageLogRepository.summarizeByEndpoint());
        model.addAttribute("partnerSummaries", usageLogRepository.summarizeByPartner());
        model.addAttribute("serviceTypeSummaries", partnerServiceRepository.summarizeActiveServicesByType());
        model.addAttribute("recentServices", partnerServiceRepository.findTop10ByOrderByCreatedAtDesc());
        model.addAttribute("recentLogs", usageLogRepository.findTop25ByOrderByRequestedAtDesc());
        model.addAttribute("aiInsights", aiInsightRepository.findTop5ByOrderByCreatedAtDesc());
        return "api-dashboard";
    }

    @PostMapping("/api-dashboard/insights/generate")
    public String generateInsight(RedirectAttributes redirectAttributes) {
        try {
            groqInsightService.generateOperationalInsight();
            redirectAttributes.addFlashAttribute("insightMessage", "AI insight generated from the latest stored application data.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("insightError", exception.getMessage());
        }
        return "redirect:/api-dashboard";
    }

    private long percentage(long numerator, long denominator) {
        if (denominator == 0) {
            return 0;
        }
        return Math.round((numerator * 100.0) / denominator);
    }
}
