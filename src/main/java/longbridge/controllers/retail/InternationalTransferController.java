package longbridge.controllers.retail;

import longbridge.dtos.InternationalBeneficiaryDTO;
import longbridge.dtos.InternationalTransferRequestDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.exception.TransferException;
import longbridge.models.Account;
import longbridge.models.FinancialInstitution;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import longbridge.validator.transfer.InternationalTransferValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 5/19/2017.
 */
@Controller
@RequestMapping(value = "/retail/transfer/international")
public class InternationalTransferController {

    private final RetailUserService retailUserService;
    private final TransferService transferService;
    private final CodeService codeService;
    private final InternationalBeneficiaryService beneficiaryService;
    private final InternationalTransferValidator validator;
    private final AccountService accountService;
    private final TransferErrorService transferErrorService;
    private final TransferUtils transferUtils;
    private final String page = "cust/transfer/international/";
    private final Logger logger= LoggerFactory.getLogger(this.getClass());
    private final MessageSource messageSource;
    public InternationalTransferController(RetailUserService retailUserService, TransferService transferService, CodeService codeService, InternationalBeneficiaryService beneficiaryService, InternationalTransferValidator validator, AccountService accountService, TransferErrorService transferErrorService, TransferUtils transferUtils,MessageSource messageSource) {
        this.retailUserService = retailUserService;
        this.transferService = transferService;
        this.codeService = codeService;
        this.beneficiaryService = beneficiaryService;
        this.validator = validator;
        this.accountService = accountService;
        this.transferErrorService = transferErrorService;
        this.transferUtils = transferUtils;
        this.messageSource = messageSource;
    }

    @GetMapping("")
    public String index(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");

        try {
           model.addAttribute("interBen",
                   StreamSupport.stream(beneficiaryService.getInternationalBeneficiaries().spliterator(), false).collect(Collectors.toList())

           );
           transferUtils.validateTransferCriteria();
           return page + "pagei";
       }
       catch (InternetBankingTransferException e) {
           String errorMessage = transferErrorService.getMessage(e);
           redirectAttributes.addFlashAttribute("failure", errorMessage);
           return "redirect:/retail/dashboard";


       }
    }


    @PostMapping(value = "/index")
    public String startTransfer(HttpServletRequest request, Model model) {

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);

        model.addAttribute("transferRequest", requestDTO);
        return page + "pageiA";
    }

    @ModelAttribute
    public void showCurrencyCodes(Model model){
        model.addAttribute("foreignCurrencyCodes", codeService.getCodesByType("CURRENCY"));
    }

    @GetMapping("/new")
    public String newBeneficiary(Model model, InternationalBeneficiaryDTO internationalBeneficiaryDTO) throws Exception {
        return page + "pageiA";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("internationalBeneficiary") @Valid InternationalBeneficiaryDTO internationalBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("internationalBeneficiaryDTO", internationalBeneficiaryDTO);
        if (servletRequest.getSession().getAttribute("add") != null)
            servletRequest.getSession().removeAttribute("add");
        if (result.hasErrors()) {
            return page + "pageiA";
        }

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(internationalBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(internationalBeneficiaryDTO.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
        FinancialInstitution financialInstitution = new FinancialInstitution();
        financialInstitution.setInstitutionCode(internationalBeneficiaryDTO.getBeneficiaryBank());
        transferRequestDTO.setFinancialInstitution(financialInstitution);
        transferRequestDTO.setBeneficiaryAccountName(internationalBeneficiaryDTO.getAccountName());
        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("benName", internationalBeneficiaryDTO.getAccountName());

        servletRequest.getSession().setAttribute("internationalBeneficiary", internationalBeneficiaryDTO);
        servletRequest.getSession().setAttribute("benName", internationalBeneficiaryDTO.getAccountName());

        if (servletRequest.getParameter("add") != null)
            servletRequest.getSession().setAttribute("add", "add");
        return page + "pageii";
    }


    @GetMapping("/process")
    public String processTransfer(HttpServletRequest request, RedirectAttributes redirectAttributes, Principal principal, Locale locale)throws TransferException{


            if (request.getSession().getAttribute("internationalBeneficiary") != null) {
                InternationalBeneficiaryDTO internationalBeneficiary = (InternationalBeneficiaryDTO) request.getSession().getAttribute("internationalBeneficiary");

                if (request.getSession().getAttribute("add") != null) {
                    //checkbox to add beneficiary is checked
                    try {
                        beneficiaryService.addInternationalBeneficiary(internationalBeneficiary);
                        request.getSession().removeAttribute("internationalBeneficiary");
                        request.getSession().removeAttribute("add");
                    } catch (InternetBankingException de) {
                        logger.error("Error adding beneficiary", de);
                    }
                }


                if (request.getSession().getAttribute("transferRequest") != null) {
                    TransferRequestDTO transferRequest = (TransferRequestDTO) request.getSession().getAttribute("transferRequest");
                    InternationalTransferRequestDTO internationalTransferRequest = new InternationalTransferRequestDTO();
                    internationalTransferRequest.setTransferType(transferRequest.getTransferType());
                    internationalTransferRequest.setCustomerAccountNumber(transferRequest.getCustomerAccountNumber());
                    internationalTransferRequest.setBeneficiaryAccountNumber(internationalBeneficiary.getAccountNumber());
                    internationalTransferRequest.setBeneficiaryAccountName(internationalBeneficiary.getAccountName());
                    internationalTransferRequest.setBeneficiaryBank(internationalBeneficiary.getBeneficiaryBank());
                    internationalTransferRequest.setBeneficiaryAddress(internationalBeneficiary.getBeneficiaryAddress());
                    internationalTransferRequest.setSwiftCode(internationalBeneficiary.getSwiftCode());
                    internationalTransferRequest.setSortCode(internationalBeneficiary.getSortCode());
                    internationalTransferRequest.setIntermediaryBankAcctNo(internationalBeneficiary.getIntermediaryBankAcctNo());
                    internationalTransferRequest.setIntermediaryBankName(internationalBeneficiary.getIntermediaryBankName());
                    internationalTransferRequest.setAmount(transferRequest.getAmount());
                    internationalTransferRequest.setCurrencyCode(internationalBeneficiary.getCurrencyCode());
                    internationalTransferRequest.setChargeFrom(internationalBeneficiary.getChargeFrom());
                    internationalTransferRequest.setRemarks(transferRequest.getRemarks());
                    internationalTransferRequest.setChannel(transferRequest.getChannel());

                    try {
                       TransferRequestDTO transferRequestDTO = transferService.makeTransfer(internationalTransferRequest);

                        redirectAttributes.addFlashAttribute("message",transferRequestDTO.getStatusDescription());

                    }catch (InternetBankingTransferException e){
                        logger.error("Error making transfer",e);
                        String errorMessage = transferErrorService.getMessage(e);
//                        redirectAttributes.addFlashAttribute("failure",messageSource.getMessage("transfer.api.international.failure", null, locale));
                        redirectAttributes.addFlashAttribute("failure", errorMessage);                    }

                }
            }
        return "redirect:/retail/transfer/international";

    }



    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest servletRequest) {
        model.addAttribute("transferRequest", transferRequestDTO);
        InternationalBeneficiaryDTO  internationalBeneficiaryDTO = (InternationalBeneficiaryDTO)servletRequest.getSession().getAttribute("internationalBeneficiary");
        String currencyCode = servletRequest.getParameter("currencyCode");
        internationalBeneficiaryDTO.setCurrencyCode(currencyCode);
        internationalBeneficiaryDTO.setChargeFrom(transferRequestDTO.getCharge());
        servletRequest.setAttribute("internationalBeneficiary",internationalBeneficiaryDTO);

        validator.validate(transferRequestDTO, result);
        if (servletRequest.getSession().getAttribute("benName") != null) {
            String benName = (String) servletRequest.getSession().getAttribute("benName");
            model.addAttribute("benName", benName);
        }
        if (servletRequest.getSession().getAttribute("internationalBeneficiary") != null) {
            InternationalBeneficiaryDTO beneficiary = (InternationalBeneficiaryDTO) servletRequest.getSession().getAttribute("internationalBeneficiary");
            beneficiary.setChargeFrom(transferRequestDTO.getCharge());
            servletRequest.getSession().setAttribute("internationalBeneficiary", beneficiary);
            model.addAttribute("beneficiary", beneficiary);
        }

        model.addAttribute("internationalBeneficiaryDTO",internationalBeneficiaryDTO);
        if (result.hasErrors()) {
            return page + "pageii";
        }
//            transferService.validateTransfer(transferRequestDTO);
        transferRequestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
        servletRequest.getSession().setAttribute("transferRequest", transferRequestDTO);
        servletRequest.getSession().removeAttribute("benName");
        return page + "pageiii";
    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        InternationalBeneficiaryDTO beneficiary = beneficiaryService.getInternationalBeneficiary(id);
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("internationalBeneficiaryDTO", beneficiary);
        model.addAttribute("benName",  beneficiary.getAccountName());
        request.getSession().setAttribute("internationalBeneficiary", beneficiary);
        request.getSession().setAttribute("benName", beneficiary.getAccountName());
        return page + "pageii";
    }

    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("transferRequest") TransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {


        model.addAttribute("transferRequest", transferRequestDTO);
        if (request.getSession().getAttribute("internationalBeneficiary") != null) {
            InternationalBeneficiaryDTO dto = (InternationalBeneficiaryDTO) request.getSession().getAttribute("internationalBeneficiary");
            model.addAttribute("internationalBeneficiaryDTO", dto);
            model.addAttribute("benName", dto.getAccountName());
        }


        return page + "pageii";
    }


    @ModelAttribute
    public void setNairaSourceAccount(Model model, Principal principal) {

        RetailUser user = retailUserService.getUserByName(principal.getName());
        if (user != null) {
            List<Account> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                    .forEach(accountList::add);
            model.addAttribute("accountList", accountList);




        }

    }

}
