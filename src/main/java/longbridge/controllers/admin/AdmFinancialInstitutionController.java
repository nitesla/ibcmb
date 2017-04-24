package longbridge.controllers.admin;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.services.FinancialInstitutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Showboy on 24/04/2017.
 */
@Controller
@RequestMapping("/admin/finst")
public class AdmFinancialInstitutionController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @GetMapping
    public String getFis() {
        return "adm/financialinstitution/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<FinancialInstitutionDTO> getAllFis(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FinancialInstitutionDTO> fis = financialInstitutionService.getFinancialInstitutions(pageable);
        DataTablesOutput<FinancialInstitutionDTO> out = new DataTablesOutput<FinancialInstitutionDTO>();
        out.setDraw(input.getDraw());
        out.setData(fis.getContent());
        out.setRecordsFiltered(fis.getTotalElements());
        out.setRecordsTotal(fis.getTotalElements());
        return out;
    }
}
