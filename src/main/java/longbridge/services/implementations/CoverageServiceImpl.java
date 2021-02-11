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

        CustomUserPrincipal principal=(CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        RetailUser retailUser=(RetailUser)principal.getUser();

        String coverages=retailUser.getCoverage();
        boolean userChecked= Arrays.stream(coverages.split(",")).filter(s-> s.equals(coverage)).count() >=1;
        boolean configChecked=codeService.getCodesByType("ACCOUNT_COVERAGE").stream()
                .map(CodeDTO::getCode).filter(s-> s.equals(coverage)).count() >=1;
        logger.info("Coverage check authorized : {} , configured {} , " ,userChecked,configChecked);
        if(!userChecked||!configChecked){
            throw  new InternetBankingException("You are not authorized to use this service");
        }
        return integrationService.getCoverageDetails(coverage, retailUser.getCustomerId());
    }

    }

