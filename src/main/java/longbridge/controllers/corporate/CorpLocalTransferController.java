package longbridge.controllers.corporate;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.CorpLocalBeneficiary;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.FinancialInstitutionType;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@Controller
@RequestMapping("/corporate/transfer/local")
public class CorpLocalTransferController {

    private final CorporateUserService corporateUserService;
    private final CorpTransferService corpTransferService;
    private final CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private final FinancialInstitutionService financialInstitutionService;
    private final TransferValidator validator;
    private final TransferErrorService transferErrorService;
    private final TransferUtils transferUtils;
    private final String page = "corp/transfer/local/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public CorpLocalTransferController(CorporateUserService corporateUserService, CorpTransferService corpTransferService, MessageSource messages, LocaleResolver localeResolver, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferValidator validator, TransferErrorService transferErrorService
            ,CorporateService corporateService,TransferUtils transferUtils) {
        this.corporateUserService = corporateUserService;
        this.corpTransferService = corpTransferService;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.transferErrorService = transferErrorService;
        this.transferUtils=transferUtils;
    }


    @GetMapping("")
    public String index(Model model, Principal principal,HttpServletRequest request,RedirectAttributes redirectAttributes) throws Exception {
        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");
        try {
        transferUtils.validateTransferCriteria();

            return page + "pagei";
        }catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            model.addAttribute("failure", errorMessage);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/dashboard";


        }
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO corpTransferRequestDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        validator.validate(corpTransferRequestDTO, result);
        if (servletRequest.getSession().getAttribute("Lbeneficiary") != null) {
            CorpLocalBeneficiaryDTO beneficiary = (CorpLocalBeneficiaryDTO) servletRequest.getSession().getAttribute("Lbeneficiary");
            model.addAttribute("beneficiary", beneficiary);

        }
//        if (result.hasErrors()) {
//            return page + "pageii";
//        }
        try {
            System.out.println(corpTransferRequestDTO);
            corpTransferService.validateTransfer(corpTransferRequestDTO);
            corpTransferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
            servletRequest.getSession().setAttribute("corpTransferRequest", corpTransferRequestDTO);

            return page + "pageiii";
        } catch (InternetBankingTransferException e) {

            String errorMessage = transferErrorService.getMessage(e);

            model.addAttribute("failure", errorMessage);
            return page + "pageii";

        }

    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        CorpLocalBeneficiaryDTO beneficiary = corpLocalBeneficiaryService.convertEntityToDTO(corpLocalBeneficiaryService.getCorpLocalBeneficiary(id));
        CorpTransferRequestDTO corpTransferRequestDTO = new CorpTransferRequestDTO();
        corpTransferRequestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        corpTransferRequestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        corpTransferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
        corpTransferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        model.addAttribute("beneficiary", beneficiary);
        request.getSession().setAttribute("Lbeneficiary", beneficiary);
        return page + "pageii";
    }


    @GetMapping("/new")
    public String addWithinBankBeneficiary(Model model, CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, RedirectAttributes redirectAttributes) throws Exception {

        try {
            transferUtils.validateTransferCriteria();
            model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
            model.addAttribute("corpLocalBeneficiaryDTO", corpLocalBeneficiaryDTO);
            return page + "pageiA";
        }catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            model.addAttribute("failure", errorMessage);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/dashboard";
        }

    }

    @PostMapping("/new")
    public String newBeneficiary(@ModelAttribute("corpLocalBeneficiaryDTO") @Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {
        if (result.hasErrors()) {
            return page + "pageiA";
        }
        if (request.getSession().getAttribute("add") != null)
            request.getSession().removeAttribute("add");
        CorpTransferRequestDTO corpTransferRequestDTO = new CorpTransferRequestDTO();
        corpTransferRequestDTO.setBeneficiaryAccountName(corpLocalBeneficiaryDTO.getAccountName());
        corpTransferRequestDTO.setBeneficiaryAccountNumber(corpLocalBeneficiaryDTO.getAccountNumber());
        corpTransferRequestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
        corpTransferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        request.getSession().setAttribute("Lbeneficiary", corpLocalBeneficiaryDTO);
        model.addAttribute("beneficiary", corpLocalBeneficiaryDTO);
        if (request.getParameter("add") != null)
            request.getSession().setAttribute("add", "add");

        return page + "pageii";
    }


    @ModelAttribute
    public void getBankCode(Model model) {
        model.addAttribute("bankCode", bankCode);
        model.addAttribute("fee", transferUtils.getFee("CMB"));
    }

    @ResponseBody
    @GetMapping("{amount}/fee")
    public String getInterBankTransferFee(@PathVariable("amount") String amount) {
        String fee="";
        try {
            fee=transferUtils.getFee("CMB", amount);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return fee;

    }

    @PostMapping("/edit")
    public String editCorpsTransfer(@ModelAttribute("corpLocalBeneficiaryDTO") CorpTransferRequestDTO requestDTO, Model model, HttpServletRequest request) {

        requestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("corpTransferRequest", requestDTO);
        if (request.getSession().getAttribute("Lbeneficiary") != null)
            model.addAttribute("beneficiary", request.getSession().getAttribute("Lbeneficiary"));
        requestDTO.setTransferType(TransferType.WITHIN_BANK_TRANSFER);

        return page + "pageii";
    }



    @ModelAttribute
    public void getBankBeneficiaries(Model model, Principal principal) {
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Corporate corporate = user.getCorporate();
        List<CorpLocalBeneficiary> beneficiaries = StreamSupport.stream(corpLocalBeneficiaryService.getCorpLocalBeneficiaries().spliterator(), false)
                .filter(i -> i.getBeneficiaryBank().equalsIgnoreCase(bankCode))
                .collect(Collectors.toList());

        beneficiaries = beneficiaries
                .stream()
                .filter(Objects::nonNull)
                .filter(i -> bankCode.equalsIgnoreCase(i.getBeneficiaryBank()))
                 .collect(Collectors.toList());

        model.addAttribute("localBen", beneficiaries);

    }

    @ModelAttribute
    public void setNairaSourceAccount(Model model, Principal principal) {

        CorporateUser user = corporateUserService.getUserByName(principal.getName());


            model.addAttribute("accountList", transferUtils.getNairaAccounts(user.getCorporate().getId()));




    }

}

