package longbridge.controllers.corporate;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.exception.TransferException;
import longbridge.models.Account;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 */
@Controller
@RequestMapping("/corporate/transfer/ownaccount")
public class CorpOwnTransferController {

    private CorporateUserService corporateUserService;
    private IntegrationService integrationService;
    private CorpTransferService corpTransferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private TransferValidator validator;
    private FinancialInstitutionService financialInstitutionService;
    private ApplicationContext appContext;
    private TransferErrorService errorService;

    private String page = "corp/transfer/ownaccount/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public CorpOwnTransferController(CorporateUserService corporateUserService, IntegrationService integrationService,
                                     CorpTransferService corpTransferService, AccountService accountService, MessageSource messages,
                                     LocaleResolver localeResolver, CorpLocalBeneficiaryService corpLocalBeneficiaryService,
                                     TransferValidator validator, FinancialInstitutionService financialInstitutionService,
                                     ApplicationContext appContext, TransferErrorService errorService) {
        this.errorService = errorService;
        this.corporateUserService = corporateUserService;
        this.integrationService = integrationService;
        this.corpTransferService = corpTransferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.validator = validator;
        this.financialInstitutionService = financialInstitutionService;
        this.appContext = appContext;
    }

    @GetMapping("")
    public ModelAndView index() throws Exception {

        ModelAndView view = new ModelAndView();

        CorpTransferRequestDTO corptransferRequestDTO = new CorpTransferRequestDTO();
        corptransferRequestDTO
                .setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode("bankCode"));
        view.addObject("corpTransferRequest", corptransferRequestDTO);
        view.setViewName(page + "pagei");
        return view;

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
            corpTransferService.validateTransfer(request);
            model.addAttribute("corpTransferRequest", request);
            servletRequest.getSession().setAttribute("corpTransferRequest", request);

            return page + "pageii";

        } catch (InternetBankingTransferException exception)

        {
            exception.printStackTrace();
            String errorMessage = errorService.getExactMessage(exception.getMessage());
            model.addAttribute("failure", errorMessage);
            return page + "pagei";

        }

    }

    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id) {

        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:pdf/");
        view.setApplicationContext(appContext);

        Map<String, Object> params = new HashMap<>();
        params.put("datasource", corpTransferService.getTransfer(id));

        return new ModelAndView(view, params);
    }


    @ModelAttribute
    public void getDestAccounts(Model model, Principal principal) {

        if (principal != null || principal.getName() != null) {

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
    public String editTransfer(@ModelAttribute("corpTransferRequest")  TransferRequestDTO transferRequestDTO,Model model,HttpServletRequest request){
        transferRequestDTO.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("corpTransferRequest",transferRequestDTO);
        return page + "pagei";
    }


}
