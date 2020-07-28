package longbridge.controllers.operations;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.CodeDTO;
import longbridge.services.AccountCoverageService;
import longbridge.services.CodeService;
import longbridge.utils.DataTablesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/ops/accountcoverage")
public class OpsCoverageController {


    @Autowired
    private CodeService codeService;
    @Autowired
    private AccountCoverageService coverageService;
    private final String  coverageCode = "ACCOUNT_COVERAGE";

    @GetMapping()
    public String listCoverages(DataTablesInput input)
    {
        System.out.println(coverageCode);
        return "/ops/corporate/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<CodeDTO> getAllCoverage(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CodeDTO> codes = codeService.getCodesByType(coverageCode, pageable);
        DataTablesOutput<CodeDTO> out = new DataTablesOutput<CodeDTO>();
        out.setDraw(input.getDraw());
        out.setData(codes.getContent());
        out.setRecordsFiltered(codes.getTotalElements());
        out.setRecordsTotal(codes.getTotalElements());
        return out;
    }

    @PostMapping(path = "/new")
    @ResponseBody
    public String createCoverage(@RequestBody String coverageData) throws IOException {
        System.out.println(coverageData);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode coverage = mapper.readTree(coverageData);
        Long codeId = coverage.get("codeId").asLong();
        Long corpId = coverage.get("corpId").asLong();
        coverageService.addCoverage(corpId,codeId);
        return "work";
    }
}


