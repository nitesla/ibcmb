package longbridge.controllers.corporate;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.models.Account;
import longbridge.models.CorporateUser;
import longbridge.models.FinancialInstitutionType;
import longbridge.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@Controller
@RequestMapping("/corporate/transfer")
public class CorpLocalTransferController {

    private CorporateUserService corporateUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private CorpLocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;

    @Autowired

    public CorpLocalTransferController(CorporateUserService corporateUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, CorpLocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService) {
        this.corporateUserService = corporateUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
    }
    @GetMapping("/local/new")
    public String addCoronationBeneficiary(Model model, CorpLocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));

        return "corp/transfer/local/addbeneficiary";
    }

    @PostMapping("/local/new")
    public String createCoronationBeneficiary(@ModelAttribute("corpLocalBeneficiary") @Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, Principal principal, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            return "corp/transfer/local/addbeneficiary";
        }

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        localBeneficiaryService.addCorpLocalBeneficiary(user, corpLocalBeneficiaryDTO);
        model.addAttribute("success", "Beneficiary added successfully");

        return "redirect:/corporate/transfer/local";
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





}

