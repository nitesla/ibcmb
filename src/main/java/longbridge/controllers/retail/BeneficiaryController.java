package longbridge.controllers.retail;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.InternationalBeneficiaryService;
import longbridge.services.LocalBeneficiaryService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Fortune on 4/3/2017.
 * Modified by Wunmi
 */

@Controller
@RequestMapping("/retail/beneficiary")
public class BeneficiaryController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LocalBeneficiaryService localBeneficiaryService;
    @Autowired
    private InternationalBeneficiaryService internationalBeneficiaryService;
    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private RetailUserService retailUserService;

    @GetMapping
    public String getBeneficiaries(Model model, Principal principal){
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        logger.info("local BEN {}", localBeneficiaryService.getLocalBeneficiaries(retailUser));
        Iterable<LocalBeneficiary> localBeneficiary = localBeneficiaryService.getLocalBeneficiaries(retailUser);
        for (LocalBeneficiary localBenef : localBeneficiary){
            localBenef.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(localBenef.getBeneficiaryBank()).getInstitutionName());
        }
        model.addAttribute("localBen", localBeneficiary);

        Iterable<InternationalBeneficiary> intBeneficiary = internationalBeneficiaryService.getInternationalBeneficiaries(retailUser);
        for (InternationalBeneficiary intBenef : intBeneficiary){
            intBenef.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(intBenef.getBeneficiaryBank()).getInstitutionName());
        }
        model.addAttribute("intBen", intBeneficiary);
        return "cust/beneficiary/view";
    }

    @GetMapping("/new")
    public String addBeneficiary(Model model){
        model.addAttribute("localBeneficiaryDTO", new LocalBeneficiaryDTO());
        model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
        model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));


        return "cust/beneficiary/add";
    }

    @PostMapping("/local")
    public String createLocalBeneficiary(@Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Principal principal,  Model model, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            model.addAttribute("internationalBeneficiaryDTO", new InternationalBeneficiaryDTO());
            model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));
            model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
            return "cust/beneficiary/add";
        }

        try{
            RetailUser user = retailUserService.getUserByName(principal.getName());
            String message = localBeneficiaryService.addLocalBeneficiary(user,localBeneficiaryDTO);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("International Beneficiary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/beneficiary";
    }

    @PostMapping("/foreign")
    public String createForeignBeneficiary(@ModelAttribute("internationalBeneficiary") @Valid  InternationalBeneficiaryDTO internationalBeneficiaryDTO, Principal principal, BindingResult result, Model model, RedirectAttributes redirectAttributes){
        if(result.hasErrors()){
            model.addAttribute("localBeneficiaryDTO", new LocalBeneficiaryDTO());
            model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));
            model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
            return "cust/beneficiary/add";
        }

        try{
            RetailUser user = retailUserService.getUserByName(principal.getName());
            String message = internationalBeneficiaryService.addInternationalBeneficiary(user,internationalBeneficiaryDTO);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("International Beneficiary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/beneficiary";
    }


    @GetMapping("/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable Long beneficiaryId, Model model){
        Beneficiary localBeneficiary = localBeneficiaryService.getLocalBeneficiary(beneficiaryId);
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
    public String deleteLocBeneficiary(@PathVariable Long beneficiaryId, Model model, RedirectAttributes redirectAttributes){

        try{
            String message = localBeneficiaryService.deleteLocalBeneficiary(beneficiaryId);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("International Beneficiary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/beneficiary";
    }

    @GetMapping("/{beneficiaryId}/int/delete")
    public String deleteIncBeneficiary(@PathVariable Long beneficiaryId, Model model, RedirectAttributes redirectAttributes){

        try{
            String message = internationalBeneficiaryService.deleteInternationalBeneficiary(beneficiaryId);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("International Beneficiary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/beneficiary";
    }

}
