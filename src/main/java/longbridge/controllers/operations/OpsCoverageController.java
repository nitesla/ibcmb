package longbridge.controllers.operations;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.UpdateCoverageDTO;
import longbridge.repositories.AccountCoverageRepo;
import longbridge.services.AccountCoverageService;
import longbridge.services.CodeService;
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



import java.io.IOException;

@Controller
@RequestMapping("/ops/accountcoverage")
public class OpsCoverageController {



    @Autowired
    private AccountCoverageService coverageService;




    @GetMapping(path = "/{corpId}/all")
    public @ResponseBody DataTablesOutput<AccountCoverageDTO> getAllCoverage(@PathVariable Long corpId,DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AccountCoverageDTO> coverage = coverageService.getAllCoverageForCorporate(corpId,pageable);
        DataTablesOutput<AccountCoverageDTO> out = new DataTablesOutput<AccountCoverageDTO>();
        out.setDraw(input.getDraw());
        out.setData(coverage.getContent());
        out.setRecordsFiltered(coverage.getTotalElements());
        out.setRecordsTotal(coverage.getTotalElements());
        return out;
    }



    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<HttpStatus> enableCoverage(@RequestBody UpdateCoverageDTO updateCoverageDTO) throws IOException {
        coverageService.enableCoverage(updateCoverageDTO);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}


