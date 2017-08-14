package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.FinancialInstitutionService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import longbridge.validator.transfer.TransferValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Created by ayoade_farooq@yahoo.com on 5/4/2017.
 */
@Controller
@RequestMapping("/retail/transfer/ownaccount")
public class OwnTransferController {

    private TransferService transferService;
    private AccountService accountService;
    private TransferValidator validator;
    private FinancialInstitutionService financialInstitutionService;
    private ApplicationContext appContext;
    private TransferErrorService errorService;
    private RetailUserService retailUserService;
    private TransferUtils transferUtils;

    private String page = "cust/transfer/ownaccount/";
    @Value("${bank.code}")
    private String bankCode;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OwnTransferController(TransferService transferService, AccountService accountService, TransferValidator validator, FinancialInstitutionService financialInstitutionService, ApplicationContext appContext,
                                 TransferErrorService errorService, RetailUserService retailUserService,TransferUtils transferUtils) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.validator = validator;
        this.financialInstitutionService = financialInstitutionService;
        this.appContext = appContext;
        this.errorService = errorService;
        this.retailUserService = retailUserService;
        this.transferUtils=transferUtils;
    }


    @GetMapping("")
    public String index(Model model, HttpServletRequest request,RedirectAttributes redirectAttributes) throws Exception {

        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");
        try {
            transferUtils.validateBvn();
            TransferRequestDTO requestDTO = new TransferRequestDTO();
            requestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
            model.addAttribute("transferRequest", requestDTO);

            return (page + "pagei");
        } catch (InternetBankingTransferException e) {
            String errorMessage = errorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);

          return "redirect:/retail/dashboard";

        }

    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO request, Locale locale, BindingResult result, Model model, HttpServletRequest servletRequest) {
        try {
            request.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
            request.setBeneficiaryAccountName(accountService.getAccountByAccountNumber(request.getBeneficiaryAccountNumber()).getAccountName());
            model.addAttribute("transferRequest", request);
            validator.validate(request, result);
            if (result.hasErrors()) {
                return page + "pagei";
            }
            request.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
            transferService.validateTransfer(request);
            model.addAttribute("transferRequest", request);
            servletRequest.getSession().setAttribute("transferRequest", request);

            return page + "pageii";

        } catch (InternetBankingTransferException exception)

        {
            String errorMessage = errorService.getMessage(exception);
            model.addAttribute("failure", errorMessage);
            return page + "pagei";

        }

    }


    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("transferRequest") TransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {
        transferRequestDTO.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);
        return page + "pagei";
    }


    @ModelAttribute
    public void getDestAccounts(Model model, Principal principal) {

        if (principal != null && principal.getName() != null) {

            RetailUser user = retailUserService.getUserByName(principal.getName());
            if (user != null) {
                List<String> accountList = new ArrayList<>();

                Iterable<Account> accounts = accountService.getAccountsForCredit(user.getCustomerId());

                StreamSupport.stream(accounts.spliterator(), false)
                        .filter(Objects::nonNull)

                        .forEach(i -> accountList.add(i.getAccountNumber()));


                model.addAttribute("destAccounts", accountList);
            }


        }


    }


}
