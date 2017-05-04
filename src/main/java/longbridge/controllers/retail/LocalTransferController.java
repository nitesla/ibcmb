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
 * Created by ayoade_farooq@yahoo.com on 5/4/2017.
 */
@Controller
@RequestMapping("/retail/transfer/local")
public class LocalTransferController {

    private RetailUserService retailUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    public LocalTransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
    }

    @GetMapping("")
    public String bankTransfer(Model model, Principal principal) throws Exception {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        model.addAttribute("localBen", localBeneficiaryService.getLocalBeneficiaries(retailUser));

        return "cust/transfer/local/add";
    }

    @PostMapping("")
    public String bankTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {


        boolean ok = transferService.makeTransfer(transferRequestDTO);
        if (ok) {
            transferService.saveTransfer(transferRequestDTO);
        }
        redirectAttributes.addFlashAttribute("message", messages.getMessage("transaction.success", null, locale));


        return "redirect:/retail/transfer/local";
    }

    @PostMapping("/summary")
    public String bankTransferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {
        model.addAttribute("transferRequest", transferRequestDTO);


        return "cust/transfer/local/summary";
    }


    @GetMapping("/{id}")
    public String makeLocaltransfer(@PathVariable Long id, Model model) throws Exception {
        LocalBeneficiary beneficiary = localBeneficiaryService.getLocalBeneficiary(id);
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());

        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("beneficiary", beneficiary);
        return "cust/transfer/local/transfer";
    }


}
