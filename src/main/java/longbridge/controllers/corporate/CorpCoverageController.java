package longbridge.controllers.corporate;



import longbridge.dtos.CoverageDTO;
import longbridge.dtos.CoverageDetailsDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.models.CorporateUser;
import longbridge.models.EntityId;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.CoverageAdministrationService;
import longbridge.services.CoverageService;
import longbridge.services.CorporateUserService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Set;


@Controller
@RequestMapping("/corporate/coverage")
public class CorpCoverageController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CoverageService coverageService;
    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    MessageSource messageSource;


    @Resource(name = "sessionCoverage")
    private Set<CoverageDetailsDTO> coverageDetails;

    @Autowired
    CoverageAdministrationService administrationService;

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
    public @ResponseBody DataTablesOutput<CoverageDTO> getAllCoverageForCorporate(@PathVariable Long corpId, DataTablesInput input) {
        EntityId entityId = new EntityId();
        entityId.setEid(corpId);
        entityId.setType(UserType.CORPORATE);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CoverageDTO> coverage = administrationService.getAllCoverage(entityId,pageable);
        DataTablesOutput<CoverageDTO> out = new DataTablesOutput<CoverageDTO>();
        out.setDraw(input.getDraw());
        out.setData(coverage.getContent());
        out.setRecordsFiltered(coverage.getTotalElements());
        out.setRecordsTotal(coverage.getTotalElements());
        return out;
    }


    @PostMapping(path = "/admin/update")
    public ResponseEntity<HttpStatus> updateCoverage(@RequestBody UpdateCoverageDTO updateCoverageDTO) {
        administrationService.updateCoverage(updateCoverageDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}

