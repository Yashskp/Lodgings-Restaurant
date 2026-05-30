package com.example.project.partner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PartnerApiControllerTests {

    private static final String API_KEY = "test-partner-key";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PartnerServiceRepository serviceRepository;

    @Autowired
    private ApiUsageLogRepository usageLogRepository;

    @BeforeEach
    void cleanDatabase() {
        usageLogRepository.deleteAll();
        serviceRepository.deleteAll();
    }

    @Test
    void supportsCreateListUpdateAndCancel() throws Exception {
        mockMvc.perform(post("/api/v1/partner-services")
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Zomato", "ZOM-100", "Deluxe Suite")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        long serviceId = serviceRepository.findByPartnerNameAndExternalReference("Zomato", "ZOM-100")
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/api/v1/partner-services").header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].externalReference").value("ZOM-100"));

        assertThat(serviceRepository.summarizeActiveServicesByType())
                .singleElement()
                .satisfies(summary -> {
                    assertThat(summary.serviceType()).isEqualTo(PartnerServiceType.LODGING);
                    assertThat(summary.totalServices()).isEqualTo(1);
                    assertThat(summary.roundedAveragePrice()).isEqualTo(4999.0);
                });

        mockMvc.perform(put("/api/v1/partner-services/{id}", serviceId)
                        .header("X-API-KEY", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request("Zomato", "ZOM-100", "Premium Suite")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Premium Suite"));

        mockMvc.perform(delete("/api/v1/partner-services/{id}", serviceId).header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(get("/api/v1/partner-services").header("X-API-KEY", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        assertThat(usageLogRepository.count()).isEqualTo(5);
        assertThat(usageLogRepository.summarizeByPartner())
                .anySatisfy(summary -> {
                    assertThat(summary.partnerName()).isEqualTo("Zomato");
                    assertThat(summary.totalRequests()).isEqualTo(3);
                });
    }

    @Test
    void rejectsMissingApiKeyAndLogsUsage() throws Exception {
        mockMvc.perform(get("/api/v1/partner-services"))
                .andExpect(status().isUnauthorized());

        assertThat(usageLogRepository.count()).isEqualTo(1);
        ApiUsageLog log = usageLogRepository.findTop25ByOrderByRequestedAtDesc().get(0);
        assertThat(log.getStatusCode()).isEqualTo(401);
        assertThat(log.isSuccessful()).isFalse();
    }

    private String request(String partnerName, String externalReference, String title) {
        return """
                {
                  "partnerName": "%s",
                  "externalReference": "%s",
                  "serviceType": "%s",
                  "title": "%s",
                  "description": "Partner visible service",
                  "location": "Bengaluru",
                  "price": 4999
                }
                """.formatted(partnerName, externalReference, PartnerServiceType.LODGING.name(), title);
    }
}
