package longbridge.config;


import longbridge.dtos.CoverageDetailsDTO;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountCoverageService;
import longbridge.services.CorporateUserService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.SessionScope;


import java.util.List;
@Configuration
public class CoverageInfo {
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private AccountCoverageService accountCoverageService;
    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    private RetailUserService retailUserService;

    @Bean
    @SessionScope
    public CoverageInfo accountCoverage(){
        return new CoverageInfo();
    }

    private List<CoverageDetailsDTO> coverage;
    private String customerId;

    private void init() {

        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        User user =  principal.getUser();

        if (UserType.CORPORATE.equals(user.getUserType())) {
            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getUsername());
            customerId = corporateUser.getCorporate().getCustomerId();
        }
        else if(UserType.RETAIL.equals(user.getUserType())){
            RetailUser retailUser = retailUserService.getUserByName(principal.getUsername());
            customerId = retailUser.getCustomerId();
        }
        this.coverage = accountCoverageService.getAllEnabledCoverageDetailsForCustomer(customerId);
    }

    public List<CoverageDetailsDTO> getCoverage() {
        return coverage;
    }

    public void setCoverage(List<CoverageDetailsDTO> coverage) {
        this.coverage = coverage;
    }
}
