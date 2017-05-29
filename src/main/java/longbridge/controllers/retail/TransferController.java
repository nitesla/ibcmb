package longbridge.controllers.retail;


import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.ResultType;
import longbridge.utils.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.StreamSupport;


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
    private TransferErrorService transferErrorService;
    private SecurityService securityService;


    @Autowired
    public TransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService, SecurityService securityService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.transferErrorService = transferErrorService;
        this.securityService = securityService;

    }


    @GetMapping(value = "")
    public String index(TransferType tranType) {

        switch (tranType) {
            case CORONATION_BANK_TRANSFER:

            {
                return "redirect:/retail/transfer/local";
            }
            case INTER_BANK_TRANSFER: {
                return "redirect:/retail/transfer/interbank";
            }
            case INTERNATIONAL_TRANSFER: {

            }
            case NAPS: {

            }
            case OWN_ACCOUNT_TRANSFER: {

                return "redirect:/retail/transfer/ownaccount";
            }

            case RTGS: {
                return "redirect:/retail/transfer/interbank";
            }
        }
        return "redirect:/retail/transfer/ownaccount";
    }


    @GetMapping("/dest/{accountId}/accounts")
    public
    @ResponseBody
    List<String> getDestinationAccounts(@PathVariable String accountId) {


        List<String> accountList = new ArrayList<>();


        Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());

        StreamSupport.stream(accounts.spliterator(), false)
                .filter(Objects::nonNull)
                .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                .forEach(i -> accountList.add(i.getAccountNumber()))
        ;
        return accountList;


    }


    @GetMapping("/{accountId}/currency")
    public
    @ResponseBody
    String getAccountCurrency(@PathVariable String accountId) {
        return accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
    }


    @GetMapping("/local/{accountNo}/nameEnquiry")
    public
    @ResponseBody
    String getBankAccountName(@PathVariable String accountNo) {
        return integrationService.viewAccountDetails(accountNo).getAcctName();

    }


    @GetMapping("/{accountNo}/{bank}/nameEnquiry")
    public
    @ResponseBody
    String getInterBankAccountName(@PathVariable String accountNo, @PathVariable String bank) {
        return (integrationService.doNameEnquiry(bank, accountNo)).getAccountName();
        // return (integrationService.doNameEnquiry("000005",accountNo)).getAccountName();


    }


    @PostMapping("/process")
    public String bankTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {

        try {

            if (request.getSession().getAttribute("AUTH") != null) {
                String token = request.getParameter("token");
                boolean tokenOk = securityService.performTokenValidation(principal.getName(), token);
                boolean ok = tokenOk;
                if (!ok) {
                    model.addAttribute("error", messages.getMessage("auth.token.failure", null, locale));
                    return "/transfer/transferauth";
                } else {
                    request.getSession().removeAttribute("AUTH");
                }


            }
            transferRequestDTO = (TransferRequestDTO) request.getSession().getAttribute("transferRequest");

            transferRequestDTO = transferService.makeTransfer(transferRequestDTO);
            request.getSession().removeAttribute("transferRequest");

            redirectAttributes.addFlashAttribute("message", messages.getMessage("transaction.success", null, locale));


            return index(transferRequestDTO.getTransferType());
            //return "redirect:/retail/dashboard";
        } catch (InternetBankingTransferException e) {
            e.printStackTrace();
            String errorMessage = transferErrorService.getMessage(e, request);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/retail/dashboard";
        }
    }

//    @PostMapping("/auth")
//    public String processTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {
//
//
//        model.addAttribute("transferRequest", transferRequestDTO);
//        return;
//
//    }
}
