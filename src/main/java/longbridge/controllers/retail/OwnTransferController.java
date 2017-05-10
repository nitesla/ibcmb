package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

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
     @Autowired
    public OwnTransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferValidator validator, FinancialInstitutionService financialInstitutionService1) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.financialInstitutionService = financialInstitutionService1;
    }

    @GetMapping("")
    public ModelAndView getOwnAccount() throws Exception {

        ModelAndView view = new ModelAndView();

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode("06001"));
        view.addObject("transferRequest", transferRequestDTO);
        view.setViewName("cust/transfer/ownaccount/transfer");
        return view;


    }


    @PostMapping("")
    public String makeTransfer(@ModelAttribute("transferRequestDTO") @Valid TransferRequestDTO transferRequestDTO, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {
        String token = request.getParameter("token");
        boolean tokenOk = integrationService.performTokenValidation(principal.getName(), token);

        boolean ok = transferService.makeTransfer(transferRequestDTO);
        if (!tokenOk) {
            redirectAttributes.addFlashAttribute("message", messages.getMessage("auth.token.failure", null, locale));

        }


        if (ok) {
            transferService.saveTransfer(transferRequestDTO);

            redirectAttributes.addFlashAttribute("message", messages.getMessage("transaction.success", null, locale));

        }


        return "redirect:/retail/transfer/ownaccount";

    }


    @PostMapping("/auth")
    public String processTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {


        model.addAttribute("transferRequest", transferRequestDTO);

        return "cust/transfer/ownaccount/process";

    }


    @PostMapping("/summary")
    public ModelAndView ownTransferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO request, Locale locale, BindingResult result) {
        ModelAndView view = new ModelAndView();
        //Validation code
        validator.validate(request, result);
        if (result.hasErrors()) {
            view.addObject("transferRequest", request);

            view.setViewName("cust/transfer/ownaccount/transfer");

            return view;
        }


        request.setBeneficiaryAccountName(integrationService.viewAccountDetails(request.getBeneficiaryAccountNumber()).getAcctName());
        request.setNarration("");//TODO A GENERIC WAY OF GENERATING THIS
        request.setReferenceNumber("");
        request.setDelFlag("N");
        request.setSessionId("");
        request.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);


        view.addObject("transferRequest", request);


        view.setViewName("cust/transfer/ownaccount/summary");


        return view;
    }


}
