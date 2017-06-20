package longbridge.controllers.retail;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 5/4/2017.
 */
@Controller
@RequestMapping("/retail/transfer/local")
public class LocalTransferController {

    private RetailUserService retailUserService;
    private TransferService transferService;
    private MessageSource messages;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private TransferErrorService transferErrorService;
    private AccountService accountService;
    private String page = "cust/transfer/local/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public LocalTransferController(RetailUserService retailUserService, TransferValidator validator, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService) {
        this.retailUserService = retailUserService;
        this.transferService = transferService;
        this.messages = messages;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.transferErrorService = transferErrorService;
        this.accountService = accountService;
    }

    @GetMapping("")
    public String index(Model model, Principal principal) throws Exception {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Iterable<LocalBeneficiary> cmbBeneficiaries = localBeneficiaryService.getBankBeneficiaries(retailUser);
        model.addAttribute("localBen",
                StreamSupport.stream(cmbBeneficiaries.spliterator(), false)
                        .collect(Collectors.toList()));


        return page + "pagei";
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest servletRequest) {
        model.addAttribute("transferRequest", transferRequestDTO);
        validator.validate(transferRequestDTO, result);

        if (servletRequest.getSession().getAttribute("Lbeneficiary") != null) {
            LocalBeneficiaryDTO beneficiary = (LocalBeneficiaryDTO) servletRequest.getSession().getAttribute("Lbeneficiary");
            model.addAttribute("beneficiary", beneficiary);
        }
        if (result.hasErrors()) {
            return page + "pageii";
        }
        try {
            transferService.validateTransfer(transferRequestDTO);
            transferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
            servletRequest.getSession().setAttribute("transferRequest", transferRequestDTO);

            return page + "pageiii";
        } catch (InternetBankingTransferException e) {

            String errorMessage = transferErrorService.getMessage(e, servletRequest);

            model.addAttribute("failure", errorMessage);
            return page + "pageii";

        }

    }


    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        LocalBeneficiaryDTO beneficiary = localBeneficiaryService.convertEntityToDTO(localBeneficiaryService.getLocalBeneficiary(id));
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("beneficiary", beneficiary);
        model.addAttribute("beneficiaryName", beneficiary.getPreferredName());
        request.getSession().setAttribute("Lbeneficiary", beneficiary);
        return page + "pageii";
    }


    @GetMapping("/new")
    public String addCoronationBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));
        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        return page + "pageiA";
    }

    @PostMapping("/new")
    public String newBeneficiary(@ModelAttribute("localBeneficiary") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
            return page + "pageiA";
        }

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(localBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(localBeneficiaryDTO.getAccountNumber());
        transferRequestDTO.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bankCode));
        model.addAttribute("transferRequest", transferRequestDTO);
        request.getSession().setAttribute("Lbeneficiary", localBeneficiaryDTO);
        model.addAttribute("beneficiary", localBeneficiaryDTO);


        return page + "pageii";
    }

    @ModelAttribute
    public void getLocalFinancialInstitutions(Model model) {

        model.addAttribute("localBanks",
                financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL)
                        .stream()
                        .filter(i -> !i.getInstitutionCode().equals(bankCode))
                        .collect(Collectors.toList())
                        .stream()
                        .sorted(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName))
                        .collect(Collectors.toList())
        );


    }


    @ModelAttribute
    public void getBankCode(Model model) {
        model.addAttribute("bankCode", bankCode);
    }

    @RequestMapping(value="/balance/{accountNumber}", method=RequestMethod.GET , produces="application/json")
    @ResponseBody
    public BigDecimal getBalance(@PathVariable String accountNumber) throws Exception {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        Map<String, BigDecimal> balance = accountService.getBalance(account);
        BigDecimal availBal = balance.get("AvailableBalance");
        return availBal;
    }

}
