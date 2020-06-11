package longbridge.controllers.admin;

import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.CodeTypeDTO;
import longbridge.exception.InternetBankingException;

import longbridge.models.AccountCoverage;
import longbridge.models.MakerChecker;
import longbridge.services.AccountCoverageService;
import longbridge.services.CodeService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/admin/accountcoverage")
public class AdmAccountCoverageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountCoverageService coverageService;

    @Autowired
    private CodeService codeService;

    @Autowired
    MessageSource messageSource;
    private  String coverageCode = "ACCOUNT_COVERAGE";


    @GetMapping()
    public String listCoverages(DataTablesInput input)
    {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Iterable<CodeDTO> codes  = codeService.getCodesByType(coverageCode);
        Iterable<AccountCoverageDTO> coverages = coverageService.getAllCoverage();
        StreamSupport.stream(codes.spliterator(),false).forEach(h->{for (AccountCoverageDTO c:coverages) {

            if (h.getCode().equals(c.getCode())){
                c.setDescription(h.getDescription());
            }
        }});
        List<AccountCoverageDTO> list = IteratorUtils.toList(coverages.iterator());
        Page<AccountCoverageDTO> coverageDTOPage = new PageImpl<AccountCoverageDTO>(list,pageable,list.size());
        System.out.println(coverages +"coverages");
        System.out.println(coverageDTOPage+"CoverageDTOPage");
        DataTablesOutput<AccountCoverageDTO> out = new DataTablesOutput<AccountCoverageDTO>();
        out.setDraw(input.getDraw());
        out.setData(coverageDTOPage.getContent());
        out.setRecordsFiltered(coverageDTOPage.getTotalElements());
        out.setRecordsTotal(coverageDTOPage.getTotalElements());
        System.out.println(out+"Out");

       return "adm/accountcoverage/view";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<AccountCoverageDTO> getAllCoverage(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Iterable<CodeDTO> codes  = codeService.getCodesByType(coverageCode);
        Iterable<AccountCoverageDTO> coverages = coverageService.getAllCoverage();
        StreamSupport.stream(codes.spliterator(),false).forEach(h->{for (AccountCoverageDTO c:coverages) {

            if (h.getCode().equals(c.getCode())){
                c.setDescription(h.getDescription());
            }
           }});
        List<AccountCoverageDTO> list = IteratorUtils.toList(coverages.iterator());
        Page<AccountCoverageDTO> coverageDTOPage = new PageImpl<AccountCoverageDTO>(list,pageable,list.size());
        DataTablesOutput<AccountCoverageDTO> out = new DataTablesOutput<AccountCoverageDTO>();
        out.setDraw(input.getDraw());
        out.setData(coverageDTOPage.getContent());
        out.setRecordsFiltered(coverageDTOPage.getTotalElements());
        out.setRecordsTotal(coverageDTOPage.getTotalElements());
        return out;
    }


}
