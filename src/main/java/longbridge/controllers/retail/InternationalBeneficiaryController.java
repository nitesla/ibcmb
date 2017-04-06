package longbridge.controllers.retail;

import longbridge.models.Beneficiary;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.BeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Fortune on 4/3/2017.
 */

@RestController
@RequestMapping("/retail/international/beneficiaries")
public class InternationalBeneficiaryController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    private RetailUser user  = new RetailUser();//TODO the current user must be authenticated

    @GetMapping("/new")
    public String addInternationalBeneficiary(){
        return "add-international";
    }

    @PostMapping
    public String createInternationalBeneficiary(@ModelAttribute("beneficiary") @Valid InternationalBeneficiary internationalBeneficiary, BindingResult result, Model model){

        if(result.hasErrors()){
            return "add";
        }
        beneficiaryService.addInternationalBeneficiary(user,internationalBeneficiary);
        model.addAttribute("success","Beneficiary added successfully");
        return "redirect:/retail/beneficiaries";
    }


    @GetMapping("/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable Long beneficiaryId, Model model){
        Beneficiary internationalBeneficiary = beneficiaryService.getInternationalBeneficiary(beneficiaryId);
        model.addAttribute("beneficiary",internationalBeneficiary);
        return internationalBeneficiary;
    }

    @GetMapping
    public Iterable<Beneficiary> getInternationalBeneficiaries(Model model){
        Iterable<Beneficiary> internationalBeneficiaries = beneficiaryService.getInternationalBeneficiaries(user);
        model.addAttribute("beneficiaries",internationalBeneficiaries);
        return internationalBeneficiaries;
    }

    @PostMapping("/{beneficiaryId}/delete")
    public String deleteBeneficiary(@PathVariable Long beneficiaryId, Model model){
        beneficiaryService.deleteInternationalBeneficiary(beneficiaryId);
        model.addAttribute("success","Beneficiary deleted successfully");
        return "redirect:/retail/beneficiaries";
    }

}
