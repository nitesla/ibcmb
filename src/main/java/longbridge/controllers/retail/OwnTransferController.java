package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.services.*;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
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

    private RetailUserService retailUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private LocalBeneficiaryService localBeneficiaryService;
    private TransferValidator validator;
    private FinancialInstitutionService financialInstitutionService;
    private ApplicationContext appContext;

    private String page = "cust/transfer/ownaccount/";

    @Autowired
    public OwnTransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, TransferValidator validator, FinancialInstitutionService financialInstitutionService, ApplicationContext appContext) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.validator = validator;
        this.financialInstitutionService = financialInstitutionService;
        this.appContext = appContext;
    }


    @GetMapping("")
    public ModelAndView index() throws Exception {

        ModelAndView view = new ModelAndView();

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode("06001"));
        view.addObject("transferRequest", transferRequestDTO);
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
                //  return "redirect:"
            }
            boolean ok = transferService.makeTransfer(transferRequestDTO);


//       redirectAttributes.addFlashAttribute("message", messages.getMessage("transac tion.success", null, locale));
            model.addAttribute("transferRequest", request);
            return page + "pageiv";

        } catch (InternetBankingTransferException exception)

        {
            String errorMessage = exception.getMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return page + "pagei";

        }

    }


    @PostMapping("/auth")
    public String processTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {


        model.addAttribute("transferRequest", transferRequestDTO);
        return page + "pageiii";

    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO request, Locale locale, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        try {


            validator.validate(request, result);
            if (result.hasErrors()) {

                return page + "pagei";
            }
            request.setBeneficiaryAccountName(integrationService.viewAccountDetails(request.getBeneficiaryAccountNumber()).getAcctName());

            model.addAttribute("transferRequest", request);

            return page + "pageii";

        } catch (InternetBankingTransferException exception)

        {
            String errorMessage = exception.getMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return page + "pagei";

        }

    }


    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id) {

        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:pdf/");
        view.setApplicationContext(appContext);

        Map<String, Object> params = new HashMap<>();
        params.put("datasource", transferService.getTransfer(id));

        return new ModelAndView(view, params);
    }
}
