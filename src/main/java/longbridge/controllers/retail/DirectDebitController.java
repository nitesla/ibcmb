package longbridge.controllers.retail;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import longbridge.dtos.DirectDebitDTO;
import longbridge.models.DirectDebit;
import longbridge.models.InternationalBeneficiary;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.DirectDebitService;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.LocalBeneficiaryService;
import longbridge.services.RetailUserService;

/**
 * Created by Chigozirim Torti
 */
@Controller
@RequestMapping("/retail/directdebit")
public class DirectDebitController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DirectDebitService directDebitService;
    @Autowired
    private LocalBeneficiaryService localBeneficiaryService;
    @Autowired
    private FinancialInstitutionService financialInstitutionService;
    
    @Autowired
    private AccountService accountService;

    @Autowired
    private RetailUserService retailUserService;

    @GetMapping
    public String getDirectDebits(Model model, Principal principal){
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        logger.info("local BEN {}", localBeneficiaryService.getLocalBeneficiaries(retailUser));
        
        List<DirectDebit> directDebits = directDebitService.getUserDirectDebits(retailUser); //(directDebitId); //getDebit(retailUser);
//        
//        for (LocalBeneficiary localBenef : localBeneficiary){
//            localBenef.setBeneficiaryBank(financialInstitutionService.getFinancialInstitutionByCode(localBenef.getBeneficiaryBank()).getInstitutionName());
//        }
        model.addAttribute("directDebits", directDebits);
        return "cust/directdebit/view";
    }

    @GetMapping("/new")
    public String addDirectDebit(Model model, Principal principal){
    	RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        model.addAttribute("beneficiaries", localBeneficiaryService.getLocalBeneficiaries(retailUser));
        model.addAttribute("accounts", accountService.getAccountByCustomerId(retailUser.getCustomerId()));
        return "cust/directdebit/add";
    }

    @PostMapping
    public String createDirectDebit(@ModelAttribute("directDebit") @Valid  DirectDebitDTO directDebitDTO, Principal principal, BindingResult result, Model model){
        if(result.hasErrors()){
            return "cust/directdebit/add";
        }
        RetailUser user = retailUserService.getUserByName(principal.getName());
        directDebitService.addDirectDebit(user, directDebitDTO);
        model.addAttribute("success","Direct Debit added successfully");
        return "redirect:/retail/directdebit";
    }


    @GetMapping("/{directDebitId}/delete")
    public String deleteDirectDebit(@PathVariable Long directDebitId, Model model){
        localBeneficiaryService.deleteLocalBeneficiary(directDebitId);
        model.addAttribute("success","Direct Debit deleted successfully");
        return "redirect:/retail/directdebit";
    }

   

}
