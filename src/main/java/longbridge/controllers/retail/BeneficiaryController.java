package longbridge.controllers.retail;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.models.Beneficiary;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.RetailUser;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.InternationalBeneficiaryService;
import longbridge.services.LocalBeneficiaryService;
import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/retail/beneficiary")
public class BeneficiaryController {

    @Autowired
    private LocalBeneficiaryService localBeneficiaryService;
    @Autowired
    private InternationalBeneficiaryService internationalBeneficiaryService;
    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    private RetailUserService retailUserService;

    @GetMapping
    public String getBeneficiaries(LocalBeneficiaryDTO localBeneficiaryDTO, InternationalBeneficiaryDTO internationalBeneficiaryDTO){
        return "cust/beneficiary/view";
    }

    @GetMapping("/new")
    public String addBeneficiary(Model model){
        model.addAttribute("foreignBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.FOREIGN));
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
        return "cust/beneficiary/add";
    }

    @PostMapping("/local")
    public String createLocalBeneficiary(@ModelAttribute("localBeneficiary") @Valid  LocalBeneficiaryDTO localBeneficiaryDTO, Principal principal, BindingResult result, Model model){

        if(result.hasErrors()){
            return "cust/beneficiary/add";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());
        localBeneficiaryService.addLocalBeneficiary(user,localBeneficiaryDTO);
        model.addAttribute("success","Beneficiary added successfully");
        return "redirect:/retail/beneficiary";
    }

    @PostMapping("/foreign")
    public String createForeignBeneficiary(@ModelAttribute("internationalBeneficiary") @Valid  InternationalBeneficiaryDTO internationalBeneficiaryDTO, Principal principal, BindingResult result, Model model){
        if(result.hasErrors()){
            return "cust/beneficiary/add";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());
        internationalBeneficiaryService.addInternationalBeneficiary(user,internationalBeneficiaryDTO);
        model.addAttribute("success","Beneficiary added successfully");
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

    @GetMapping("/{beneficiaryId}/delete")
    public String deleteBeneficiary(@PathVariable Long beneficiaryId, Model model){
        localBeneficiaryService.deleteLocalBeneficiary(beneficiaryId);
        model.addAttribute("success","Beneficiary deleted successfully");
        return "redirect:/retail/beneficiaries";
    }

}
