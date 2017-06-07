package longbridge.controllers.corporate;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.CorporateUser;
import longbridge.models.FinancialInstitutionType;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.validator.transfer.TransferValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
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
 * Created by SYLVESTER on 5/22/2017.
 */
@Controller
@RequestMapping("/corporate/transfer/local")
public class CorpLocalTransferController {

    private CorporateUserService corporateUserService;
    private CorpTransferService corpTransferService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private TransferErrorService transferErrorService;
    private String page ="corp/transfer/local/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public CorpLocalTransferController(CorporateUserService corporateUserService, CorpTransferService corpTransferService, MessageSource messages, LocaleResolver localeResolver, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferValidator validator, TransferErrorService transferErrorService) {
        this.corporateUserService = corporateUserService;
        this.corpTransferService = corpTransferService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.transferErrorService = transferErrorService;
    }




    @GetMapping("")
    public String index(Model model, Principal principal) throws Exception {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        model.addAttribute("localBen", corpLocalBeneficiaryService.getCorpLocalBeneficiaries(corporateUser.getCorporate()));

        return page + "pagei";
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO corpTransferRequestDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        validator.validate(corpTransferRequestDTO, result);
        if (result.hasErrors()) {
            return page + "pageii";
        }
        try {
            System.out.println(corpTransferRequestDTO);
            corpTransferService.validateTransfer(corpTransferRequestDTO);
            corpTransferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
            servletRequest.getSession().setAttribute("corpTransferRequest", corpTransferRequestDTO);

            return page + "pageiii";
        } catch (InternetBankingTransferException e) {

            String errorMessage = transferErrorService.getMessage(e, servletRequest);

            model.addAttribute("failure", errorMessage);
            return page + "pageii";

        }

    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model) throws Exception {
        CorpLocalBeneficiary beneficiary = corpLocalBeneficiaryService.getCorpLocalBeneficiary(id);
        CorpTransferRequestDTO corpTransferRequestDTO = new CorpTransferRequestDTO();
        corpTransferRequestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        corpTransferRequestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        corpTransferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
        corpTransferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        model.addAttribute("beneficiary", beneficiary);
        return page + "pageii";
    }


    @GetMapping("/new")
    public String addCoronationBeneficiary(Model model, CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
        model.addAttribute("corpLocalBeneficiaryDTO", corpLocalBeneficiaryDTO);
        return page + "pageiA";
    }

    @PostMapping("/new")
    public String newBeneficiary(@ModelAttribute("corpLocalBeneficiary") @Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result, Model model,HttpServletRequest request) throws Exception {
        if (result.hasErrors()) {
            return page + "pageiA";
        }

        CorpTransferRequestDTO corpTransferRequestDTO = new CorpTransferRequestDTO();
        corpTransferRequestDTO.setBeneficiaryAccountName(corpLocalBeneficiaryDTO.getAccountName());
        corpTransferRequestDTO.setBeneficiaryAccountNumber(corpLocalBeneficiaryDTO.getAccountNumber());
        corpTransferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
        corpTransferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        request.getSession().setAttribute("Lbeneficiary", corpLocalBeneficiaryDTO);
        model.addAttribute("beneficiary", corpLocalBeneficiaryDTO);


        return page + "pageii";
    }


    @PostMapping("/auth")
    public String processTransfer(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO corpTransferRequestDTO, Model model) throws Exception {


        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        return page + "pageiv";

    }

}

