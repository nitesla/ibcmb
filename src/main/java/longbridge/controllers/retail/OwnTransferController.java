package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.services.*;
import longbridge.utils.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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
    private FinancialInstitutionService financialInstitutionService;

    @Autowired
    public OwnTransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
    }

    @GetMapping("")
    public ModelAndView getOwnAccount() throws Exception {

        ModelAndView view = new ModelAndView();

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        view.addObject("transferRequest", transferRequestDTO);
        view.setViewName("cust/transfer/ownaccount/transfer");
        return view;


    }


    @PostMapping("")
    public String makeTransfer(@ModelAttribute("transferRequestDTO") @Valid TransferRequestDTO transferRequestDTO, RedirectAttributes redirectAttributes, Locale locale) throws Exception {


        // boolean ok =transferService.makeTransfer(transferRequestDTO);
        boolean ok = true;

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
    public ModelAndView ownTransferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO request, Locale locale) {
        ModelAndView view = new ModelAndView();
        boolean balanceOk = transferService.validateBalance(request);
        if (!balanceOk) {
            view.addObject("transferRequest", request);
            view.addObject("error", messages.getMessage("insufficient.balance", null, locale));
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
