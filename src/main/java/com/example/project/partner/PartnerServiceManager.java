package com.example.project.partner;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerServiceManager {

    private final PartnerServiceRepository serviceRepository;

    public PartnerServiceManager(PartnerServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Transactional(readOnly = true)
    public List<PartnerServiceResponse> listActive() {
        return serviceRepository.findByStatus(PartnerServiceStatus.ACTIVE)
                .stream()
                .map(PartnerServiceResponse::from)
                .toList();
    }

    @Transactional
    public PartnerServiceResponse create(PartnerServiceRequest request) {
        serviceRepository.findByPartnerNameAndExternalReference(request.getPartnerName(), request.getExternalReference())
                .ifPresent(existing -> {
                    throw new PartnerServiceConflictException("External reference already exists for this partner.");
                });

        PartnerService service = new PartnerService();
        applyRequest(service, request);
        return PartnerServiceResponse.from(serviceRepository.save(service));
    }

    @Transactional
    public PartnerServiceResponse update(Long id, PartnerServiceRequest request) {
        PartnerService service = findById(id);
        applyRequest(service, request);
        service.markUpdated();
        return PartnerServiceResponse.from(serviceRepository.save(service));
    }

    @Transactional
    public PartnerServiceResponse cancel(Long id) {
        PartnerService service = findById(id);
        service.setStatus(PartnerServiceStatus.CANCELLED);
        service.markUpdated();
        return PartnerServiceResponse.from(serviceRepository.save(service));
    }

    private PartnerService findById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new PartnerServiceNotFoundException("Partner service was not found."));
    }

    private void applyRequest(PartnerService service, PartnerServiceRequest request) {
        service.setPartnerName(request.getPartnerName());
        service.setExternalReference(request.getExternalReference());
        service.setServiceType(request.getServiceType());
        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setLocation(request.getLocation());
        service.setPrice(request.getPrice());
        service.setStatus(PartnerServiceStatus.ACTIVE);
    }
}
