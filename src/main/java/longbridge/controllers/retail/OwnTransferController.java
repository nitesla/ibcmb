package longbridge.controllers.retail;

import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.services.*;
import longbridge.utils.ResultType;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import java.util.ArrayList;
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
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
    public ModelAndView report(@PathVariable Long id,HttpServletRequest servletRequest, TransferRequestDTO transferRequestDTO) {
        /**
         * Created a stub to test transaction receiptpt
         */
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_receipt.jrxml");
        view.setApplicationContext(appContext);

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("datasource",new ArrayList<>());
//        modelMap.put("format", "pdf");
        modelMap.put("amount", "1,000,000.00");
        modelMap.put("recipient", "BANKOLE D. ONEY");
        modelMap.put("AccountNum", "10986433737332");
        modelMap.put("sender", "BAMDUPE ABIODUN");
        modelMap.put("bank", "CORONATION MERCHANT BANK");
        modelMap.put("remarks", "MY BUILDING PROJECT");
        modelMap.put("recipientBank", "CORONATION MERCHANT BANK");
        modelMap.put("acctNo2", "0986879765");
        modelMap.put("acctNo1", "4343758667");
        modelMap.put("refNUm", "65566586787");
        modelMap.put("date", "08-09-2017");
        modelMap.put("amountInWords", "1 MILLION NAIRA ");
        modelMap.put("tranDate", "08-09-2017");
        return new ModelAndView(view, modelMap);

//        logger.info("Transaction Receipt {}",modelMap);
//
//        ModelAndView modelAndView = new ModelAndView("rpt_receipt", modelMap);
//        return modelAndView;
    }
    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("transferRequest")  TransferRequestDTO transferRequestDTO,Model model,HttpServletRequest request){
        transferRequestDTO.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest",transferRequestDTO);
        return page + "pagei";
    }

}
