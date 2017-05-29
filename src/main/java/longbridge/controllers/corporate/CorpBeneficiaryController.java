package longbridge.controllers.corporate;

import longbridge.dtos.CorpInternationalBeneficiaryDTO;
import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.models.*;
import longbridge.services.CorpInternationalBeneficiaryService;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.CorpLocalBeneficiaryService;
import longbridge.services.CorporateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by SYLVESTER on 5/19/2017.
 */
@Controller
@RequestMapping("/corporate/beneficiary")
public class CorpBeneficiaryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    @Autowired
    private CorpInternationalBeneficiaryService corpInternationalBeneficiaryService;
    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private CorporateUserService corporateUserService;


    @GetMapping
    public String getBeneficiaries(Model model, Principal principal){
       CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        logger.info("local BEN {}", corpLocalBeneficiaryService.getCorpLocalBeneficiaries(corporateUser));
        Iterable<CorpLocalBeneficiary> corpLocalBeneficiaries = corpLocalBeneficiaryService.getCorpLocalBeneficiaries(corporateUser);

        for (CorpLocalBeneficiary localBenef : corpLocalBeneficiaries){
            localBenef.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(localBenef.getBeneficiaryBank()).getInstitutionName());
        }
        model.addAttribute("localBen", corpLocalBeneficiaries);

       /* Iterable<CorpInterBen> intBeneficiary = corpInternationalBeneficiaryService.getCorpInternationalBeneficiaries(corporateUser);
        for (CorpInterBen intBenef : intBeneficiary){
            intBenef.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(intBenef.getBeneficiaryBank()).getInstitutionName());
        }
        model.addAttribute("intBen", intBeneficiary);*/
        return "corp/beneficiary/view";
    }

    @GetMapping("/new")
    public String addBeneficiary(Model model){
        model.addAttribute("corpLocalBeneficiaryDTO", new CorpLocalBeneficiaryDTO());
        model.addAttribute("corpInternationalBeneficiaryDTO", new CorpInternationalBeneficiaryDTO());
        model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
        return "corp/beneficiary/add";
    }

    @PostMapping("/local")
    public String createCorpLocalBeneficiary(@Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result, Principal principal, Model model){
        if(result.hasErrors()){
            model.addAttribute("corpInternationalBeneficiaryDTO",new CorpInternationalBeneficiaryDTO());
            model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));
            model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
            return "corp/beneficiary/add";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        corpLocalBeneficiaryService.addCorpLocalBeneficiary(user,corpLocalBeneficiaryDTO);
        model.addAttribute("success","Beneficiary added successfully");
        return "redirect:/corporate/beneficiary";
    }

    @PostMapping("/foreign")
    public String createForeignBeneficiary(@ModelAttribute("corpInternationalBeneficiary") @Valid  CorpInternationalBeneficiaryDTO corpInternationalBeneficiaryDTO, Principal principal, BindingResult result, Model model){
        if(result.hasErrors()){
            return "corp/beneficiary/add";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        corpInternationalBeneficiaryService.addCorpInternationalBeneficiary(user,corpInternationalBeneficiaryDTO);
        model.addAttribute("success","Beneficiary added successfully");
        return "redirect:/corporate/beneficiary";
    }


    @GetMapping("/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable Long beneficiaryId, Model model){
        Beneficiary localBeneficiary = corpLocalBeneficiaryService.getCorpLocalBeneficiary(beneficiaryId);
        model.addAttribute("beneficiary",localBeneficiary);
        return localBeneficiary;
    }


//    @GetMapping
//    public Iterable<Beneficiary> getLocalBeneficiaries(Model model){
//        Iterable<Beneficiary> localBeneficiaries = beneficiaryService.getLocalBeneficiaries(user);
//        model.addAttribute("beneficiaries",localBeneficiaries);
//        return localBeneficiaries;
//    }

    @GetMapping("/{beneficiaryId}/loc/delete")
    public String deleteLocBeneficiary(@PathVariable Long beneficiaryId, Model model){
        corpLocalBeneficiaryService.deleteCorpLocalBeneficiary(beneficiaryId);
        model.addAttribute("success","Beneficiary deleted successfully");
        return "redirect:/corporate/beneficiary";
    }

    @GetMapping("/{beneficiaryId}/int/delete")
    public String deleteIncBeneficiary(@PathVariable Long beneficiaryId, Model model){
        corpInternationalBeneficiaryService.deleteCorpInternationalBeneficiary(beneficiaryId);
        model.addAttribute("success","Beneficiary deleted successfully");
        return "redirect:/corporate/beneficiary";
    }

}
