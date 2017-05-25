package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.services.*;
import longbridge.utils.ResultType;
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
    public OwnTransferController(TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, TransferValidator validator, FinancialInstitutionService financialInstitutionService, ApplicationContext appContext,TransferErrorService errorService) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.validator = validator;
        this.financialInstitutionService = financialInstitutionService;
        this.appContext = appContext;
        this.errorService=errorService;
    }


    @GetMapping("")
    public ModelAndView index(ModelAndView view) throws Exception {

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        view.addObject("transferRequest", requestDTO);
        view.setViewName(page + "pagei");
        return view;
    }


    @PostMapping("")
    public String makeTransfer(@ModelAttribute("transferRequestDTO") @Valid TransferRequestDTO transferRequestDTO, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal, Model model) {
        try {
            String token = request.getParameter("token");
            // boolean tokenOk = integrationService.performTokenValidation(principal.getName(), token);
            boolean tokenOk = !token.isEmpty();

            if (!tokenOk) {
                redirectAttributes.addFlashAttribute("message", messages.getMessage("auth.token.failure", null, locale));
                return "redirect:/auth";
            }
            System.out.println(transferRequestDTO);
            TransferRequestDTO requestDTO = transferService.makeTransfer(transferRequestDTO);

            try {
                if (requestDTO != null && requestDTO.getStatus().equals(ResultType.SUCCESS.toString())) {
                    model.addAttribute("transferRequest", requestDTO);
                    return page + "pageiv";

                }
            } catch (Exception e) {
                throw new InternetBankingTransferException();

            }

            throw new InternetBankingTransferException();

        } catch (InternetBankingTransferException exception)

        {

            String errorMessage = exception.getMessage();
            System.out.println(errorMessage);
//            redirectAttributes.addFlashAttribute("transferRequest", new TransferRequestDTO());
//            redirectAttributes.addFlashAttribute("error", errorMessage);
            TransferRequestDTO t = new TransferRequestDTO();
            transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode("06001"));

            model.addAttribute("transferRequest", t);
            return page + "pagei";

        }

    }


    @PostMapping("/auth")
    public String processTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {


        model.addAttribute("transferRequest", transferRequestDTO);
        return page + "pageiii";

    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO request, Locale locale, BindingResult result, Model model, HttpServletRequest servletRequest) {
        try {
            request.setBeneficiaryAccountName(accountService.getAccountByAccountNumber(request.getBeneficiaryAccountNumber()).getAccountName());
            model.addAttribute("transferRequest", request);
            validator.validate(request, result);
            if (result.hasErrors()) {
                return page + "pagei";
            }

            transferService.validateTransfer(request);
            model.addAttribute("transferRequest", request);

            return page + "pageii";

        } catch (InternetBankingTransferException exception)

        {
            String errorMessage =errorService.getMessage(exception,servletRequest);
           model.addAttribute("error", errorMessage);
            return page + "pagei";

        }

    }


    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id) {

        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:pdf/a");
        view.setApplicationContext(appContext);

        Map<String, Object> params = new HashMap<>();
        params.put("datasource", transferService.getTransfer(id));

        return new ModelAndView(view, params);
    }
}
