package longbridge.controllers.operations;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.models.EntityId;
import longbridge.models.UserType;
import longbridge.services.AccountCoverageAdministrationService;
import longbridge.utils.DataTablesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ops/accountcoverage")
public class OpsCoverageController {


    @Autowired
    private AccountCoverageAdministrationService coverageService;

    private final String  coverageCode = "ACCOUNT_COVERAGE";

    @GetMapping()
    public String listCoverages(DataTablesInput input)
    {

        return "/ops/corporate/view";
    }

    @GetMapping(path = "/corporate/{corpId}/all")
    public @ResponseBody DataTablesOutput<AccountCoverageDTO> getAllCoverageCorporate(DataTablesInput input, @PathVariable Long corpId) {
        EntityId entityId = new EntityId();
        entityId.setEid(corpId);
        entityId.setType(UserType.CORPORATE);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountCoverageDTO> codes = coverageService.getAllCoverage(entityId,pageable);
        DataTablesOutput<AccountCoverageDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(codes.getContent());
        out.setRecordsFiltered(codes.getTotalElements());
        out.setRecordsTotal(codes.getTotalElements());
        return out;
    }

    @PostMapping(path = "/corporate/update")
    public ResponseEntity<HttpStatus> updateCoverageCorporate(@RequestBody UpdateCoverageDTO updateCoverageDTO) {
        coverageService.updateCoverage(updateCoverageDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @GetMapping(path = "/retail/{retId}/all")
    public @ResponseBody DataTablesOutput<AccountCoverageDTO> getAllCoverageRetail(DataTablesInput input, @PathVariable Long retId) {
        EntityId entityId = new EntityId();
        entityId.setEid(retId);
        entityId.setType(UserType.RETAIL);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountCoverageDTO> codes = coverageService.getAllCoverage(entityId,pageable);
        DataTablesOutput<AccountCoverageDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(codes.getContent());
        out.setRecordsFiltered(codes.getTotalElements());
        out.setRecordsTotal(codes.getTotalElements());
        return out;
    }

    @PostMapping(path = "/retail/update")
    public ResponseEntity<HttpStatus> updateCoverageRetail(@RequestBody UpdateCoverageDTO updateCoverageDTO) {
        coverageService.updateCoverage(updateCoverageDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }



}


