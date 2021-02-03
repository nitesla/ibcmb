package longbridge.services.implementations;

import longbridge.dtos.CoverageDetailsDTO;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.CoverageService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CoverageServiceImpl implements CoverageService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private IntegrationService integrationService;


    @Autowired
    RetailUserService retailUserService;

    @Autowired
    RetailUserRepo retailUserRepo;



    @Override
    public Page<CoverageDetailsDTO> getCoverages(String coverageName, String customerId,  Pageable pageable) {
        List<CoverageDetailsDTO> coverageDetails = integrationService.getCoverages(coverageName, customerId);
        return new PageImpl<>(coverageDetails, pageable, coverageDetails.size());

    }

    @Override
    public Map<String, List<String>> getCoverageDetails(String coverageName, String customerId) {
        return integrationService.getCoverageDetails(coverageName, customerId);
    }

    @Override
    public List<CoverageDetailsDTO> getCoverage(String coverageName, String customerId) {
       return integrationService.getCoverages(coverageName, customerId);
    }
}
