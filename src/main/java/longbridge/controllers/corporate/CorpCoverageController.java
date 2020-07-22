package longbridge.controllers.corporate;



import longbridge.config.CoverageInfo;
import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.models.CorporateUser;
import longbridge.models.EntityId;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountCoverageAdministrationService;
import longbridge.services.AccountCoverageService;
import longbridge.services.CorporateUserService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/corporate/accountcoverage")
public class CorpCoverageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private AccountCoverageService coverageService;
    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    MessageSource messageSource;


    @Resource(name = "sessionCoverage")
    private Set<CoverageDetailsDTO> coverageDetails;

    @Autowired
    AccountCoverageAdministrationService administrationService;

    @GetMapping(path = "/{corpId}")
    @ResponseBody
    public Set<CoverageDetailsDTO> getEnabledCoverageForCorporate(@PathVariable Long corpId){

        return coverageDetails;
    }

    @GetMapping(path = "/admin")
    public String CoverageForCorporateAdmin(Model model){
        CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user =  principal.getUser();
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getUsername());
        Long corpId = corporateUser.getCorporate().getId();
        model.addAttribute("corpId",corpId);
        return "corp/account/coverage";
    }

    @GetMapping(path = "/admin/{corpId}/all")
    public @ResponseBody DataTablesOutput<AccountCoverageDTO> getAllCoverageForCorporate(@PathVariable Long corpId, DataTablesInput input) {
        EntityId entityId = new EntityId();
        entityId.setEid(corpId);
        entityId.setType(UserType.CORPORATE);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountCoverageDTO> coverage = administrationService.getAllCoverage(entityId,pageable);
        DataTablesOutput<AccountCoverageDTO> out = new DataTablesOutput<AccountCoverageDTO>();
        out.setDraw(input.getDraw());
        out.setData(coverage.getContent());
        out.setRecordsFiltered(coverage.getTotalElements());
        out.setRecordsTotal(coverage.getTotalElements());
        return out;
    }


    @GetMapping(path = "/admin/update")
    public String updateCoverageForCorporateAdmin(){
        return "corp/account/coverage";
    }


}

