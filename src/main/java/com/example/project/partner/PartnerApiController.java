package com.example.project.partner;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/partner-services")
public class PartnerApiController {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final PartnerServiceManager serviceManager;
    private final ApiUsageLogRepository usageLogRepository;
    private final PartnerApiProperties apiProperties;

    public PartnerApiController(
            PartnerServiceManager serviceManager,
            ApiUsageLogRepository usageLogRepository,
            PartnerApiProperties apiProperties) {
        this.serviceManager = serviceManager;
        this.usageLogRepository = usageLogRepository;
        this.apiProperties = apiProperties;
    }

    @GetMapping
    public ResponseEntity<List<PartnerServiceResponse>> list(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            HttpServletRequest request) {
        if (!isAuthorized(apiKey)) {
            logUsage("UNKNOWN", request, HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PartnerServiceResponse> services = serviceManager.listActive();
        logUsage("PARTNER", request, HttpStatus.OK);
        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<PartnerServiceResponse> create(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @Valid @RequestBody PartnerServiceRequest serviceRequest,
            HttpServletRequest request) {
        if (!isAuthorized(apiKey)) {
            logUsage(serviceRequest.getPartnerName(), request, HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PartnerServiceResponse response = serviceManager.create(serviceRequest);
        logUsage(serviceRequest.getPartnerName(), request, HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartnerServiceResponse> update(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @PathVariable Long id,
            @Valid @RequestBody PartnerServiceRequest serviceRequest,
            HttpServletRequest request) {
        if (!isAuthorized(apiKey)) {
            logUsage(serviceRequest.getPartnerName(), request, HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PartnerServiceResponse response = serviceManager.update(id, serviceRequest);
        logUsage(serviceRequest.getPartnerName(), request, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PartnerServiceResponse> cancel(
            @RequestHeader(value = API_KEY_HEADER, required = false) String apiKey,
            @PathVariable Long id,
            HttpServletRequest request) {
        if (!isAuthorized(apiKey)) {
            logUsage("UNKNOWN", request, HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PartnerServiceResponse response = serviceManager.cancel(id);
        logUsage(response.partnerName(), request, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(PartnerServiceNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(PartnerServiceNotFoundException exception, HttpServletRequest request) {
        logUsage("PARTNER", request, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(PartnerServiceConflictException.class)
    ResponseEntity<Map<String, String>> handleConflict(PartnerServiceConflictException exception, HttpServletRequest request) {
        logUsage("PARTNER", request, HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        FieldError error = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = error == null ? "Invalid request." : error.getField() + " " + error.getDefaultMessage();
        logUsage("PARTNER", request, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }

    private boolean isAuthorized(String apiKey) {
        return apiProperties.getKey().equals(apiKey);
    }

    private void logUsage(String partnerName, HttpServletRequest request, HttpStatus status) {
        usageLogRepository.save(new ApiUsageLog(
                partnerName == null || partnerName.isBlank() ? "UNKNOWN" : partnerName,
                request.getMethod(),
                request.getRequestURI(),
                status.value(),
                status.is2xxSuccessful()));
    }
}
