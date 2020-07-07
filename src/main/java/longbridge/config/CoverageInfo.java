package longbridge.config;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.models.EntityId;
import longbridge.models.RetailUser;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountCoverageAdministrationService;
import longbridge.services.CorporateService;
import longbridge.services.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.Set;

@Component
@SessionScope
public class CoverageInfo {
    @Autowired
    private AccountCoverageAdministrationService accountCvAdminService;

    private Set<CoverageDetailsDTO> coverage;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private CorporateService corporateService;


    private void init() {

        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        User user = principal.getUser();
        EntityId entityId = new EntityId();
        Set<String> cifs = new HashSet<>();
        if (UserType.CORPORATE.equals(user.getUserType())) {
            entityId.setEid(principal.getCorpId());
            entityId.setType(UserType.CORPORATE);
            Set<String> cifids = corporateService.getCorp(principal.getCorpId()).getCifids();
            cifs.addAll(cifids);
        } else if (UserType.RETAIL.equals(user.getUserType())) {
            entityId.setEid(user.getId());
            entityId.setType(UserType.RETAIL);
            String cif = ((RetailUser) user).getCustomerId();
            cifs.add(cif);
        }


        Page<AccountCoverageDTO> allCoverage = accountCvAdminService.getAllCoverage(entityId, Pageable.unpaged());
//        List<AccountCoverageDTO> collect = allCoverage.stream().filter(AccountCoverageDTO::isEnabled).collect(Collectors.toList());
        // go integration service
        coverage = new HashSet<>();
        for (AccountCoverageDTO dto : allCoverage) {
            if (dto.isEnabled()) {
                //Adjust the service so no loop is necessary
                for (String cif : cifs) {
                    CoverageDetailsDTO coverageDetails = integrationService.getCoverageDetails(dto.getCode(), cif);
                    coverage.add(coverageDetails);
                }

            }
        }


    }

    public Set<CoverageDetailsDTO> getCoverage() {
        init();
        return coverage;
    }

}
