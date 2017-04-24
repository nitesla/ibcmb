package longbridge.controllers.retail;

import longbridge.models.Beneficiary;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.LocalBeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Fortune on 4/3/2017.
 */

@Controller
@RequestMapping("/retail/beneficiary")
public class BeneficiaryController {

    @Autowired
    private LocalBeneficiaryService beneficiaryService;

    private RetailUser user  = new RetailUser();//TODO the current user must be authenticated

    @GetMapping
    public String getBeneficiaries(){
        return "cust/beneficiary/view";
    }

    @GetMapping("/new")
    public String addBeneficiary(){
        return "cust/beneficiary/add";
    }

    @PostMapping
    public String createLocalBeneficiary(@ModelAttribute("beneficiary") @Valid  LocalBeneficiary localBeneficiary, BindingResult result, Model model){

        if(result.hasErrors()){
            return "add";
        }
        beneficiaryService.addLocalBeneficiary(user,localBeneficiary);
        model.addAttribute("success","Beneficiary added successfully");
        return "redirect:/retail/beneficiaries";
    }


    @GetMapping("/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable Long beneficiaryId, Model model){
        Beneficiary localBeneficiary = beneficiaryService.getLocalBeneficiary(beneficiaryId);
        model.addAttribute("beneficiary",localBeneficiary);
        return localBeneficiary;
    }

    @GetMapping
    public Iterable<LocalBeneficiary> getLocalBeneficiaries(Model model){
        Iterable<LocalBeneficiary> localBeneficiaries = beneficiaryService.getLocalBeneficiaries(user);
        model.addAttribute("beneficiaries",localBeneficiaries);
        return localBeneficiaries;
    }

//    @GetMapping
//    public Iterable<Beneficiary> getLocalBeneficiaries(Model model){
//        Iterable<Beneficiary> localBeneficiaries = beneficiaryService.getLocalBeneficiaries(user);
//        model.addAttribute("beneficiaries",localBeneficiaries);
//        return localBeneficiaries;
//    }

    @GetMapping("/{beneficiaryId}/delete")
    public String deleteBeneficiary(@PathVariable Long beneficiaryId, Model model){
        beneficiaryService.deleteLocalBeneficiary(beneficiaryId);
        model.addAttribute("success","Beneficiary deleted successfully");
        return "redirect:/retail/beneficiaries";
    }

}
