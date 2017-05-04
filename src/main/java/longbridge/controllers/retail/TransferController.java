package longbridge.controllers.retail;


import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {


    private RetailUserService retailUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;


    @Autowired
    public TransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
    }


    @GetMapping
    public String makeInternationalTransfer(Principal principal) throws Exception {
        return "cust/transfer/internationaltransfer/view";

    }


    @GetMapping("/local/new")
    public String addCoronationBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));

        return "cust/transfer/local/addbeneficiary";
    }

    @PostMapping("/local/new")
    public String createCoronationBeneficiary(@ModelAttribute("localBeneficiary") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, Principal principal, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            return "cust/transfer/local/addbeneficiary";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());
        localBeneficiaryService.addLocalBeneficiary(user, localBeneficiaryDTO);
        model.addAttribute("success", "Beneficiary added successfully");

        return "redirect:/retail/transfer/local";
    }

    @GetMapping("/interbanktransfer")
    public String getInterBank(Model model) throws Exception {
        return "cust/transfer/interbanktransfer/add";
    }






    @GetMapping("/settransferlimit")
    public String getTransferLimit(Model model) throws Exception {
        return "cust/transfer/settransferlimit/view";
    }


}
