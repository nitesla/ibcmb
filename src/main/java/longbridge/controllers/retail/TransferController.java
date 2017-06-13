package longbridge.controllers.retail;


import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.*;
import longbridge.utils.ResultType;
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

    @Value("${bank.code}")
    private String bankCode;


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

        try {
            Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                    .forEach(i -> accountList.add(i.getAccountNumber()))
            ;

        } catch (Exception e) {

        }


        return accountList;


    }


    @GetMapping("/{accountId}/currency")
    public
    @ResponseBody
    String getAccountCurrency(@PathVariable String accountId) {

        try {
            return accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
        } catch (Exception e) {
            return "";
        }

    }


    @GetMapping("/local/{accountNo}/nameEnquiry")
    public
    @ResponseBody
    String getBankAccountName(@PathVariable String accountNo,Principal principal) {

        try {
            if (principal!=null){
                return integrationService.viewAccountDetails(accountNo).getAcctName();
            }

        } catch (Exception e) {
          e.printStackTrace();
        }

        return "";
    }


    @GetMapping("/{accountNo}/{bank}/nameEnquiry")
    public
    @ResponseBody
    String getInterBankAccountName(@PathVariable String accountNo, @PathVariable String bank,Principal principal) {

        try {

            if (principal!=null)
            {
                if (bank.equalsIgnoreCase(bankCode)) return integrationService.viewAccountDetails(accountNo).getAcctName();

                return (integrationService.doNameEnquiry(bank, accountNo)).getAccountName();

            }



        } catch (Exception e) {

        }

        return "";
    }


    @PostMapping("/process")
    public String bankTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {

        try {

            if (request.getSession().getAttribute("auth-needed") != null) {
                String token = request.getParameter("token");
                boolean ok = securityService.performTokenValidation(principal.getName(), token);

                if (!ok) {
                    model.addAttribute("failure", messages.getMessage("auth.token.failure", null, locale));
                    return "/cust/transfer/transferauth";
                } else {
                    request.getSession().removeAttribute("auth-needed");
                }


            }
            transferRequestDTO = (TransferRequestDTO) request.getSession().getAttribute("transferRequest");


            transferRequestDTO = transferService.makeTransfer(transferRequestDTO);

            request.getSession().removeAttribute("transferRequest");


            if (request.getSession().getAttribute("Lbeneficiary") != null) {

                LocalBeneficiaryDTO l = (LocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                model.addAttribute("message", messages.getMessage("transaction.success", null, locale));
                model.addAttribute("beneficiary", l);
                return "/cust/transfer/transferbeneficiary";
            }

            redirectAttributes.addFlashAttribute("message", messages.getMessage("transaction.success", null, locale));
            return index(transferRequestDTO.getTransferType());
            //return "redirect:/retail/dashboard";
        } catch (InternetBankingTransferException e) {
            e.printStackTrace();
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
            String errorMessage = transferErrorService.getMessage(e, request);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/retail/dashboard";


        }
    }

    @GetMapping("/newbeneficiary")
    public String newbeneficiaary(HttpServletRequest request, Locale locale, Principal principal, RedirectAttributes attributes) throws Exception {


        try {

            if (request.getSession().getAttribute("Lbeneficiary") != null) {
                RetailUser user = retailUserService.getUserByName(principal.getName());
                LocalBeneficiaryDTO l = (LocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                localBeneficiaryService.addLocalBeneficiary(user, l);
                request.getSession().removeAttribute("Lbeneficiary");
            }


            attributes.addFlashAttribute("message", "New Beneficiary Added");

        } catch (InternetBankingException e) {

            if (e.getMessage().equalsIgnoreCase("beneficiary.exist")) {

                attributes.addFlashAttribute("failure", messages.getMessage("beneficiary.exist", null, locale));
            } else {
                messages.getMessage("beneficiary.add.failure", null, locale);
                attributes.addFlashAttribute("failure", e.getMessage());
            }


        }


        return "redirect:/retail/dashboard";
    }

    }

