package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.services.*;
import longbridge.utils.ResultType;
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
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ayoade_farooq@yahoo.com on 5/4/2017.
 */
@Controller
@RequestMapping("/retail/transfer/ownaccount")
public class OwnTransferController {

    private TransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private LocalBeneficiaryService localBeneficiaryService;
    private TransferValidator validator;
    private FinancialInstitutionService financialInstitutionService;
    private ApplicationContext appContext;
    private TransferErrorService errorService;

    private String page = "cust/transfer/ownaccount/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public OwnTransferController(TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, TransferValidator validator, FinancialInstitutionService financialInstitutionService, ApplicationContext appContext, TransferErrorService errorService) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.validator = validator;
        this.financialInstitutionService = financialInstitutionService;
        this.appContext = appContext;
        this.errorService = errorService;
    }


    @GetMapping("")
    public ModelAndView index(ModelAndView view) throws Exception {

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        view.addObject("transferRequest", requestDTO);
        view.setViewName(page + "pagei");
        return view;
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
            String errorMessage = errorService.getMessage(exception, servletRequest);
            model.addAttribute("failure", errorMessage);
            return page + "pagei";

        }

    }

    @RequestMapping(value="/balance/{accountNumber}", method=RequestMethod.GET , produces="application/json")
    @ResponseBody
    public BigDecimal getBalance(@PathVariable String accountNumber) throws Exception {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        Map<String, BigDecimal> balance = accountService.getBalance(account);
        BigDecimal availBal = balance.get("AvailableBalance");
        return availBal;
    }


    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id) {

        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:pdf/receipt.jrxml");
        view.setApplicationContext(appContext);

        Map<String, Object> params = new HashMap<>();
        params.put("amount", transferService.getTransfer(id).getAmount());
        params.put("beneAccName", transferService.getTransfer(id).getBeneficiaryAccountName());
        params.put("beneAccNo", transferService.getTransfer(id).getBeneficiaryAccountNumber());
        params.put("custAcount", transferService.getTransfer(id).getCustomerAccountNumber());
        params.put("bank", transferService.getTransfer(id).getFinancialInstitution().getInstitutionName());
        params.put("narration", transferService.getTransfer(id).getNarration());
        params.put("remarks", transferService.getTransfer(id).getRemarks());
        params.put("status", transferService.getTransfer(id).getStatus());
        params.put("transType", transferService.getTransfer(id).getTransferType());
        params.put("transDate", transferService.getTransfer(id).getTranDate());
        params.put("refNo", transferService.getTransfer(id).getReferenceNumber());

        return new ModelAndView(view, params);
    }
}
