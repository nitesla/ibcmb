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

        boolean confChecked = codeService.getCodesByType("ACCOUNT_COVERAGE").stream()
                .map(CodeDTO::getCode).filter(s -> s.equals(coverage)).count() >= 1;

        logger.info("Coverage configured : {}", confChecked);

        if (!isCoverageEnabled(coverage)|| !confChecked) {
            throw new InternetBankingException("You are not authorized to use this service");
        }
        return integrationService.getCoverageDetails(coverage, getCorporate().getCustomerId());
    }

    @Override
    public boolean isCoverageEnabled(String coverage) {
  
        String coverages = getCorporate().getCoverage();
        if (coverages == null){
            logger.info("No Coverage enabled yet for the logged in user");
            return  false;
        }
        boolean isEnabled = Arrays.stream(coverages.split(",")).filter(s -> s.equals(coverage)).count() >= 1;
        logger.info("The Coverage {} enabled ? : {}",coverage,isEnabled);
        return isEnabled;

    }


    public Corporate getCorporate(){
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        CorporateUser corpuser = (CorporateUser) principal.getUser();
        Corporate corporate = corpuser.getCorporate();
        return corporate;

    }
}
