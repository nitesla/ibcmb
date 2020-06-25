package longbridge.controllers.admin;


import longbridge.dtos.AccountCoverageDTO;
import longbridge.dtos.CodeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.services.AccountCoverageService;
import longbridge.services.CodeService;
import longbridge.services.IntegrationService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/admin/accountcoverage")
public class AdmAccountCoverageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private AccountCoverageService coverageService;

    @Autowired
    private CodeService codeService;

    @Autowired
    MessageSource messageSource;

    private final String  coverageCode = "ACCOUNT_COVERAGE";


    @GetMapping()
    public String listCoverages(DataTablesInput input)
    {

        return "adm/accountcoverage/view";
    }

//    @GetMapping(path = "/all")
//    @ResponseBody
//    public DataTablesOutput<AccountCoverageDTO> getAllCoverage(DataTablesInput input){
//        Pageable pageable = DataTablesUtils.getPageable(input);
//        Iterable<CodeDTO> codes  = codeService.getCodesByType(coverageCode);
//        Iterable<AccountCoverageDTO> coverages = coverageService.getAllCoverage();
//        StreamSupport.stream(codes.spliterator(),false).forEach(h->{for (AccountCoverageDTO c:coverages) {
//
//            if (h.getCode().equals(c.getCode())){
//                c.setDescription(h.getDescription());
//            }
//           }});
//        List<AccountCoverageDTO> list = IteratorUtils.toList(coverages.iterator());
//        Page<AccountCoverageDTO> coverageDTOPage = new PageImpl<AccountCoverageDTO>(list,pageable,list.size());
//        DataTablesOutput<AccountCoverageDTO> out = new DataTablesOutput<AccountCoverageDTO>();
//        out.setDraw(input.getDraw());
//        out.setData(coverageDTOPage.getContent());
//        out.setRecordsFiltered(coverageDTOPage.getTotalElements());
//        out.setRecordsTotal(coverageDTOPage.getTotalElements());
//        return out;
//    }
//
//    @PostMapping("/update")
//    @ResponseBody
//    public ResponseEntity<HttpStatus> enableCoverage(@RequestBody String coverageJson) throws IOException {
//       coverageService. enableCoverage(coverageJson);
//       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//
//    }
//    @GetMapping("/new")
//    public String addCoverage(){
//
//        return "adm/accountcoverage/add";
//    }
//
//    @PostMapping("/new")
//    public String createCoverage(@ModelAttribute("accountCoverageDTO") @Valid AccountCoverageDTO accountCoverageDTO, BindingResult result, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
//        if(result.hasErrors()){
//            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
//            return "adm/accountcoverage/add";
//        }
//
//
//        try {
//
//            CodeDTO codeDTO = new CodeDTO();
//            codeDTO.setType(coverageCode);
//            codeDTO.setCode(accountCoverageDTO.getCode());
//            codeDTO.setDescription(accountCoverageDTO.getDescription());
//            String message = coverageService.addCoverage(codeDTO);
//            redirectAttributes.addFlashAttribute("message", message);
//            return "redirect:/admin/accountcoverage";
//        }
//        catch (InternetBankingException ibe){
//            result.addError(new ObjectError("error",ibe.getMessage()));
//            logger.error("Error creating code",ibe);
//            return "adm/accountcoverage/add";
//        }
//    }
//    @GetMapping("/edit")
//    public String editCoverage(){
//        return "adm/accountcoverage/edit";
//    }
//
//    @GetMapping("/{code}/delete")
//    public String deleteCoverage(@PathVariable String code, RedirectAttributes redirectAttributes){
////        Long coverageId = coverageService.getCoverageId(code);
////        Long codeId = coverageService.getCodeId(code);
//
//        try {
////            String message = coverageService.deleteCoverage(coverageId);
////            codeService.deleteCode(codeId);
//            redirectAttributes.addFlashAttribute("message", "message");
//        }
//        catch (InternetBankingException ibe){
//            logger.error("Error deleting Coverage",ibe);
//            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
//
//        }
//
//        return "redirect:/admin/accountcoverage";
//
//    }
//

}
