package longbridge.controllers.retail;


import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {


    private RetailUserService retailUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;


    @Autowired
    public TransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
    }


    @GetMapping
    public String makeInternationalTransfer(Principal principal) throws Exception {
        return "cust/transfer/internationaltransfer/view";

    }

    @GetMapping("/local")
    public String bankTransfer(Model model, Principal principal) throws Exception {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        model.addAttribute("localBen", localBeneficiaryService.getLocalBeneficiaries(retailUser));

        return "cust/transfer/local/add";
    }

    @PostMapping("/local")
    public String bankTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {


        boolean ok = transferService.makeTransfer(transferRequestDTO);
        if (ok) {
            transferService.saveTransfer(transferRequestDTO);
        }
        redirectAttributes.addFlashAttribute("message", messages.getMessage("transaction.success", null, locale));


        return "redirect:/retail/transfer/local";
    }

    @PostMapping("/local/summary")
    public String bankTransferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model) throws Exception {
        model.addAttribute("transferRequest", transferRequestDTO);


        return "cust/transfer/local/summary";
    }


    @GetMapping("/{id}/local")
    public String makeLocaltransfer(@PathVariable Long id, Model model) throws Exception {
        LocalBeneficiary beneficiary = localBeneficiaryService.getLocalBeneficiary(id);
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());

        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("beneficiary", beneficiary);
        return "cust/transfer/local/transfer";
    }

    @GetMapping("/local/new")
    public String addCoronationBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));

        return "cust/transfer/local/addbeneficiary";
    }

    @PostMapping("/local/new")
    public String createCoronationBeneficiary(@ModelAttribute("localBeneficiary") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, Principal principal, BindingResult result, Model model) throws Exception {
        if (result.hasErrors()) {
            return "cust/transfer/local/addbeneficiary";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());
        localBeneficiaryService.addLocalBeneficiary(user, localBeneficiaryDTO);
        model.addAttribute("success", "Beneficiary added successfully");

        return "redirect:/retail/transfer/local";
    }

    @GetMapping("/interbanktransfer")
    public String getInterBank(Model model) throws Exception {
        return "cust/transfer/interbanktransfer/add";
    }

    @GetMapping("/ownaccount")
    public ModelAndView getOwnAccount() throws Exception {

        ModelAndView view = new ModelAndView();

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        view.addObject("transferRequest", transferRequestDTO);
        view.setViewName("cust/transfer/ownaccount/transfer");
        return view;


    }


    @PostMapping("/ownaccount")
    public String makeOwnAccountTransfer(@ModelAttribute("transferRequestDTO") @Valid TransferRequestDTO transferRequestDTO, RedirectAttributes redirectAttributes, Locale locale) throws Exception {


        // boolean ok =transferService.makeTransfer(transferRequestDTO);
        boolean ok = true;

        if (ok) {
            // transferService.saveTransfer(transferRequestDTO);
            //  view.addObject("msg",messages.getMessage("transaction.success", null, locale));


        }


        redirectAttributes.addFlashAttribute("message", messages.getMessage("transaction.success", null, locale));

        return "redirect:/retail/transfer/ownaccount";

    }

    @GetMapping("/settransferlimit")
    public String getTransferLimit(Model model) throws Exception {
        return "cust/transfer/settransferlimit/view";
    }

    @PostMapping("/own/summary")
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
