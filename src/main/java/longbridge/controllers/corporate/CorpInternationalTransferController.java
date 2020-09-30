package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.models.Account;
import longbridge.models.CorporateUser;
import longbridge.models.FinancialInstitution;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import longbridge.validator.transfer.InternationalTransferValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/corporate/transfer/international")
public class CorpInternationalTransferController {

    private final CorporateUserService corporateUserService;
    private final CorpTransferService transferService;
    private final CodeService codeService;
    private final CorpInternationalBeneficiaryService beneficiaryService;
    private final InternationalTransferValidator validator;
    private final AccountService accountService;
    private final TransferErrorService transferErrorService;
    private final TransferUtils transferUtils;
    private final MessageSource messageSource;
    private final String page = "corp/transfer/international/";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorpInternationalTransferController(CorporateUserService corporateUserService, CorpTransferService transferService, CodeService codeService, CorpInternationalBeneficiaryService beneficiaryService, InternationalTransferValidator validator, AccountService accountService, TransferErrorService transferErrorService, TransferUtils transferUtils, MessageSource messageSource) {
        this.corporateUserService = corporateUserService;
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
    public String index(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model) throws Exception {
        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");

        try {
            model.addAttribute("internationalBeneficiaries", StreamSupport.stream(beneficiaryService.getCorpInternationalBeneficiaries().spliterator(), false).collect(Collectors.toList()));
            transferUtils.validateTransferCriteria();
            return page + "pagei";
        } catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/dashboard";


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
    public void showCurrencyCodes(Model model) {
        model.addAttribute("foreignCurrencyCodes", codeService.getCodesByType("CURRENCY"));
    }

    @GetMapping("/new")
    public String newBeneficiary(@ModelAttribute("internationalBeneficiary") @Valid InternationalBeneficiaryDTO internationalBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        return page + "pageiA";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("internationalBeneficiary") @Valid CorpInternationalBeneficiaryDTO internationalBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
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
    public String processTransfer(HttpServletRequest request, Locale locale, RedirectAttributes redirectAttributes, Principal principal) {


        if (request.getSession().getAttribute("internationalBeneficiary") != null) {
            CorpInternationalBeneficiaryDTO internationalBeneficiary = (CorpInternationalBeneficiaryDTO) request.getSession().getAttribute("internationalBeneficiary");

            if (request.getSession().getAttribute("add") != null) {
                //checkbox to add beneficiary is checked
                try {
                    beneficiaryService.addCorpInternationalBeneficiary(internationalBeneficiary);
                    request.getSession().removeAttribute("internationalBeneficiary");
                    request.getSession().removeAttribute("add");
                } catch (InternetBankingException de) {
                    logger.error("Error adding beneficiary", de);
                }
            }


            if (request.getSession().getAttribute("corpTransferRequest") != null) {
                TransferRequestDTO transferRequest = (TransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
                CorpInternationalTransferRequestDTO internationalTransferRequest = new CorpInternationalTransferRequestDTO();
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

                    String corporateId = "" + corporateUserService.getUserByName(principal.getName()).getCorporate().getId();
                    internationalTransferRequest.setCorporateId(corporateId);
                    Object object = transferService.addTransferRequest(internationalTransferRequest);

                    if (object instanceof CorpTransferRequestDTO) {

                        transferRequest = (CorpTransferRequestDTO) object;
                        redirectAttributes.addFlashAttribute("message", transferRequest.getStatusDescription());
                    } else if (object instanceof String) {
                        redirectAttributes.addFlashAttribute("message", object);

                    }

                   return "redirect:/corporate/transfer/requests";

                } catch (TransferAuthorizationException ae) {
                    logger.error("Error initiating a transfer ", ae);
                    redirectAttributes.addFlashAttribute("failure", ae.getMessage());
                } catch (InternetBankingTransferException ex) {

                    logger.error("Error initiating a transfer ", ex);
                    String errorMessage = transferErrorService.getMessage(ex);
                    redirectAttributes.addFlashAttribute("failure", errorMessage);
                } catch (TransferRuleException e) {
                    logger.error("Error initiating a transfer ", e);
                    String errorMessage = e.getMessage();
                    redirectAttributes.addFlashAttribute("failure", errorMessage);
                } catch (Exception e) {
                    logger.error("Error initiating a transfer ", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("transfer.error", null, locale));
                }

            }
        }
        return "redirect:/corporate/transfer/international";

    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid CorpTransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest servletRequest) {
        model.addAttribute("transferRequest", transferRequestDTO);
        InternationalBeneficiaryDTO internationalBeneficiaryDTO = (InternationalBeneficiaryDTO) servletRequest.getSession().getAttribute("internationalBeneficiary");
        String currencyCode = servletRequest.getParameter("currencyCode");
        internationalBeneficiaryDTO.setCurrencyCode(currencyCode);
        internationalBeneficiaryDTO.setChargeFrom(transferRequestDTO.getCharge());
        servletRequest.setAttribute("internationalBeneficiary", internationalBeneficiaryDTO);

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

        model.addAttribute("internationalBeneficiaryDTO", internationalBeneficiaryDTO);
        if (result.hasErrors()) {
            return page + "pageii";
        }
//            transferService.validateTransfer(transferRequestDTO);
        transferRequestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
        servletRequest.getSession().setAttribute("corpTransferRequest", transferRequestDTO);
        servletRequest.getSession().removeAttribute("benName");
        return page + "pageiii";
    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        CorpInternationalBeneficiaryDTO beneficiary = beneficiaryService.getCorpInternationalBeneficiary(id);
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTERNATIONAL_TRANSFER);
        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("internationalBeneficiaryDTO", beneficiary);
        model.addAttribute("benName", beneficiary.getAccountName());
        request.getSession().setAttribute("internationalBeneficiary", beneficiary);
        request.getSession().setAttribute("benName", beneficiary.getAccountName());
        return page + "pageii";
    }

    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("corpTransferRequest") TransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {


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

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        if (user != null) {
            List<Account> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCorporate().getAccounts());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> "NGN".equalsIgnoreCase(i.getCurrencyCode()))
                    .forEach(accountList::add);
            model.addAttribute("accountList", accountList);


        }

    }

}
