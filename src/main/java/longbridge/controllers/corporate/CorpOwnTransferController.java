package longbridge.controllers.corporate;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.CorporateUser;
import longbridge.services.AccountService;
import longbridge.services.CorpTransferService;
import longbridge.services.CorporateUserService;
import longbridge.services.FinancialInstitutionService;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
 *
 */
@Controller
@RequestMapping("/corporate/transfer/ownaccount")
public class CorpOwnTransferController {

    private final CorporateUserService corporateUserService;
    private final CorpTransferService corpTransferService;
    private final AccountService accountService;
    private final TransferValidator validator;
    private final FinancialInstitutionService financialInstitutionService;
    private final TransferErrorService errorService;
    private final TransferUtils transferUtils;

    private final String page = "corp/transfer/ownaccount/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public CorpOwnTransferController(CorporateUserService corporateUserService,
                                     CorpTransferService corpTransferService, AccountService accountService,
                                     TransferValidator validator, FinancialInstitutionService financialInstitutionService,
                                     ApplicationContext appContext, TransferErrorService errorService, TransferUtils transferUtils) {
        this.errorService = errorService;
        this.corporateUserService = corporateUserService;
        this.corpTransferService = corpTransferService;
        this.accountService = accountService;
        this.validator = validator;
        this.financialInstitutionService = financialInstitutionService;
        this.transferUtils = transferUtils;
    }

    @GetMapping("")
    public String index(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");
        try {
            transferUtils.validateTransferCriteria();
            CorpTransferRequestDTO corptransferRequestDTO = new CorpTransferRequestDTO();
            corptransferRequestDTO
                    .setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode("bankCode"));
            model.addAttribute("corpTransferRequest", corptransferRequestDTO);

            return (page + "pagei");
        } catch (InternetBankingTransferException e) {
            String errorMessage = errorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);

            return "redirect:/corporate/dashboard";

        }

    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO request,
                                  Locale locale, BindingResult result, Model model,
                                  HttpServletRequest servletRequest) {
        try {
            request.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
            request.setBeneficiaryAccountName(
                    accountService.getAccountByAccountNumber(request.getBeneficiaryAccountNumber()).getAccountName());
            model.addAttribute("corpTransferRequest", request);
            validator.validate(request, result);
            if (result.hasErrors()) {
                return page + "pagei";
            }
            request.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
            String currency = accountService.getAccountByAccountNumber(request.getCustomerAccountNumber()).getCurrencyCode();
            request.setCurrencyCode(currency);
            corpTransferService.validateTransfer(request);
            model.addAttribute("corpTransferRequest", request);
            servletRequest.getSession().setAttribute("corpTransferRequest", request);

            return page + "pageii";

        } catch (InternetBankingTransferException exception) {
            exception.printStackTrace();
            String errorMessage = errorService.getMessage(exception);
            model.addAttribute("failure", errorMessage);
            return page + "pagei";

        }

    }


    @ModelAttribute
    public void getDestAccounts(Model model, Principal principal) {

        if (principal != null && principal.getName() != null) {

            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            if (user != null) {
                List<String> accountList = new ArrayList<>();

                Iterable<Account> accounts = accountService.getAccountsForCredit(user.getCorporate().getCustomerId());

                StreamSupport.stream(accounts.spliterator(), false)
                        .filter(Objects::nonNull)

                        .forEach(i -> accountList.add(i.getAccountNumber()));


                model.addAttribute("destAccounts", accountList);
            }
        }
    }


    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("corpTransferRequest") TransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {
        transferRequestDTO.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("corpTransferRequest", transferRequestDTO);
        return page + "pagei";
    }


}
