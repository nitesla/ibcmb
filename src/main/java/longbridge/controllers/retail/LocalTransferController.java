package longbridge.controllers.retail;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
    private String page = "cust/transfer/local/";
    @Value("${bank.code}")
    private String bankCode;

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
    public String index(Model model, Principal principal) throws Exception {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        model.addAttribute("localBen", localBeneficiaryService.getLocalBeneficiaries(retailUser));

        return page + "pagei";
    }

    @PostMapping("")
    public String bankTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request) throws Exception {

        try {

            String token = request.getParameter("token");
            // boolean tokenOk = integrationService.performTokenValidation(principal.getName(), token);
            boolean tokenOk = !token.isEmpty();
            if (!tokenOk) {
                redirectAttributes.addFlashAttribute("message", messages.getMessage("auth.token.failure", null, locale));
                return page + "pageiv";
            }
            boolean ok = transferService.makeTransfer(transferRequestDTO);
            if (ok) {
                transferService.saveTransfer(transferRequestDTO);
                redirectAttributes.addFlashAttribute("message", messages.getMessage("transaction.success", null, locale));

            }


            return "redirect:/retail/transfer/local";
        } catch (InternetBankingTransferException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/retail/transfer/local";
        }
    }

    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {
        model.addAttribute("transferRequest", transferRequestDTO);
        return page + "pageiii";
    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model) throws Exception {
        LocalBeneficiary beneficiary = localBeneficiaryService.getLocalBeneficiary(id);
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("beneficiary", beneficiary);
        return page + "pageii";
    }


    @GetMapping("/new")
    public String addCoronationBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));

        return page + "pageiA";
    }

    @PostMapping("/new")
    public String newBeneficiary(@ModelAttribute("localBeneficiary") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            return page + "pageiA";
        }

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(localBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(localBeneficiaryDTO.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);

        model.addAttribute("beneficiary", localBeneficiaryDTO);


        return page + "pageii";
    }


    @PostMapping("/auth")
    public String processTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {


        model.addAttribute("transferRequest", transferRequestDTO);
        return page + "pageiv";

    }

}
