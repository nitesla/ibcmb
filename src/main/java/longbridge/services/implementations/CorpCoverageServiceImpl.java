package longbridge.services.implementations;

import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CodeService;
import longbridge.services.CorpCoverageService;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;

@Service
public class CorpCoverageServiceImpl implements CorpCoverageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private CodeService codeService;
    private IntegrationService integrationService;

    public CorpCoverageServiceImpl(CodeService codeService, IntegrationService integrationService) {
        this.codeService = codeService;
        this.integrationService = integrationService;
    }

    @Override
    public JsonNode getCoverage(String coverage) {
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        CorporateUser corpuser = (CorporateUser) principal.getUser();
        Corporate corporate = corpuser.getCorporate();
        String coverages = corporate.getCoverage();

        boolean corpChecked = Arrays.stream(coverages.split(",")).filter(s -> s.equals(coverage)).count() >= 1;

        boolean confChecked = codeService.getCodesByType("ACCOUNT_COVERAGE").stream()
                .map(CodeDTO::getCode).filter(s -> s.equals(coverage)).count() >= 1;

        logger.info("Coverage check authorized : {} , configured {}", corpChecked, confChecked);

        if (!corpChecked || !confChecked) {
            throw new InternetBankingException("You are not authorized to use this service");
        }
        return integrationService.getCoverageDetails(coverage, corporate.getCustomerId());
    }
}
