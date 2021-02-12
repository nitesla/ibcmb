package longbridge.services.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.RetailUser;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CodeService;
import longbridge.services.CoverageService;
import longbridge.services.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CoverageServiceImpl implements CoverageService {

   private final Logger logger=LoggerFactory.getLogger(this.getClass());
   private CodeService codeService;
   private IntegrationService integrationService;

    public CoverageServiceImpl(CodeService codeService, IntegrationService integrationService) {
        this.codeService = codeService;
        this.integrationService = integrationService;
    }

    @Override
    public JsonNode getCoverage(String coverage) {

        boolean configChecked=codeService.getCodesByType("ACCOUNT_COVERAGE").stream()
                .map(CodeDTO::getCode).filter(s-> s.equals(coverage)).count() >=1;
        logger.info("Coverage configured  : {} ",configChecked);
        if(!isCoverageEnabled(coverage)||!configChecked){
            throw  new InternetBankingException("You are not authorized to use this service");
        }
        return integrationService.getCoverageDetails(coverage, getRetailUser().getCustomerId());
    }

    @Override
    public boolean isCoverageEnabled(String coverage) {

        String coverages=getRetailUser().getCoverage();
        if (coverages == null){
            logger.info("No Coverage enabled yet for the logged in user");
            return  false;
        }
        boolean isEnabled= Arrays.stream(coverages.split(",")).filter(s-> s.equals(coverage)).count() >=1;
        logger.info("The Coverage {} enabled ? : {}",coverage,isEnabled);
        return isEnabled;
    }


    public RetailUser getRetailUser(){
        CustomUserPrincipal principal=(CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        RetailUser retailUser=(RetailUser)principal.getUser();

        return  retailUser;
    }

}

