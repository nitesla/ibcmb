package longbridge.controllers.retail;

import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.FinancialInstitution;
import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import longbridge.validator.transfer.TransferValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by ayoade_farooq@yahoo.com on 5/19/2017.
 */
@Controller
@RequestMapping(value = "/retail/transfer/interbank")
public class InterBankTransferController {
    private RetailUserService retailUserService;
    private TransferService transferService;
    private MessageSource messages;
    private TransferUtils transferUtils;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private IntegrationService integrationService;
    private AccountService accountService;
    private String page = "cust/transfer/interbank/";
    @Value("${bank.code}")
    private String bankCode;
    private TransferErrorService transferErrorService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

/*
    @Value("${geolocation.url}")
    private String geolocationUrl;

    @Value("${geolocation.key}")
    private String geolocationKey;*/

    @Autowired
    public InterBankTransferController(RetailUserService retailUserService, TransferService transferService, MessageSource messages, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, AccountService accountService, TransferValidator validator

            , IntegrationService integrationService, TransferUtils transferUtils, TransferErrorService transferErrorService
    ) {
        this.retailUserService = retailUserService;
        this.transferService = transferService;
        this.messages = messages;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.integrationService = integrationService;
        this.accountService = accountService;
        this.transferUtils = transferUtils;
        this.transferErrorService = transferErrorService;
    }


    @GetMapping(value = "")
    public String index(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");
        try {
            transferUtils.validateTransferCriteria();
            return page + "pagei";
        } catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/retail/dashboard";


        }
    }



    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})

    public String startTransfer(HttpServletRequest request, Model model, Principal principal) {
        if(principal == null){
            return "redirect:/retail/logout";
        }

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        List<LocalBeneficiary> beneficiaries = StreamSupport.stream(localBeneficiaryService.getLocalBeneficiaries().spliterator(), false)
                .filter(i -> !i.getBeneficiaryBank().equalsIgnoreCase(financialInstitutionService.getFinancialInstitutionByCode(bankCode).getInstitutionCode())).collect(Collectors.toList());

        beneficiaries
                .stream()
                .filter(Objects::nonNull)
                .forEach(i ->
                        {
                            FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByCode(i.getBeneficiaryBank());

                            if (financialInstitution != null)
                                i.setBeneficiaryBank(financialInstitution.getInstitutionName());


                        }

                );

        model.addAttribute("localBen", beneficiaries);

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        String type = request.getParameter("tranType");
        logger.info("type {} ", type);

        if ("NIP".equalsIgnoreCase(type)) {

            request.getSession().setAttribute("NIP", "NIP");
            requestDTO.setTransferType(TransferType.NIP);

            model.addAttribute("transferRequest", requestDTO);
            return page + "pageiA";
        } else if ("RTGS".equalsIgnoreCase(type)){
            request.getSession().setAttribute("NIP", "RTGS");
            requestDTO.setTransferType(TransferType.RTGS);

            model.addAttribute("transferRequest", requestDTO);
            return page + "pageiAb";
        } else if ("NEFT".equalsIgnoreCase(type))
            request.getSession().setAttribute("NIP", "NEFT");
        requestDTO.setTransferType(TransferType.NEFT);

        model.addAttribute("transferRequest", requestDTO);
        return page + "pageiAc";



    }


    @GetMapping("/new")
    public String newBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {

        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        return page + "pageiB";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("localBeneficiaryDTO") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        if (servletRequest.getSession().getAttribute("add") != null)
            servletRequest.getSession().removeAttribute("add");
        if (result.hasErrors()) {
            return page + "pageiB";
        }

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(localBeneficiaryDTO.getAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(localBeneficiaryDTO.getAccountNumber());

        logger.info("Beneficiary is {}",transferRequestDTO.getBeneficiaryAccountName());

        transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(localBeneficiaryDTO.getBeneficiaryBank()));
        model.addAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("benName", localBeneficiaryDTO.getPreferredName());

        servletRequest.getSession().setAttribute("Lbeneficiary", localBeneficiaryDTO);
//        servletRequest.getSession().setAttribute("benName", localBeneficiaryDTO.getPreferredName());

        if (servletRequest.getParameter("add") != null)
            servletRequest.getSession().setAttribute("add", "add");
        return page + "pageii";
    }


    @RequestMapping(value = "/summary", method = {RequestMethod.POST , RequestMethod.GET})
    public String transferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {

        String newbenName = (String) request.getSession().getAttribute("beneficiaryName");
        String userAmountLimit = transferUtils.getLimitForAuthorization(transferRequestDTO.getCustomerAccountNumber(), transferRequestDTO.getChannel());
        BigDecimal amountLimit = new BigDecimal(userAmountLimit);
        BigDecimal userAmount = transferRequestDTO.getAmount();
        int a = amountLimit.intValue();
        int b = userAmount.intValue();
        if (b > a){
            String errorMessage = "You can not transfer more than account limit";
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("benName", newbenName);
            model.addAttribute("transferRequest", transferRequestDTO);
             return page + "pageii";
        }

        model.addAttribute("transferRequest", transferRequestDTO);
        String charge = "NAN";

        String benName = (String) request.getSession().getAttribute("benName");

        model.addAttribute("benName", benName);

        validator.validate(transferRequestDTO, result);

        if (result.hasErrors()) {
            return page + "pageii";
        }

        if (request.getSession().getAttribute("NIP") != null) {
            String type = (String) request.getSession().getAttribute("NIP");
            if ("RTGS".equalsIgnoreCase(type)) {
                transferRequestDTO.setTransferType(TransferType.RTGS);
                charge = transferUtils.getFee("RTGS",String.valueOf(transferRequestDTO.getAmount()) );
                transferRequestDTO.setCharge(charge);


            } else {
                transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
                charge = transferUtils.getFee("NIP",String.valueOf(transferRequestDTO.getAmount()));
                transferRequestDTO.setCharge(charge);
            }
            // request.getSession().removeAttribute("NIP");

        } else {
            transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        }
        request.getSession().setAttribute("transferRequest", transferRequestDTO);
        model.addAttribute("charge", charge);
        return page + "pageiii";
    }

    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request, Locale locale, RedirectAttributes attributes) throws Exception {
        LocalBeneficiary beneficiary = localBeneficiaryService.getLocalBeneficiary(id);
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        FinancialInstitution institution = financialInstitutionService.getFinancialInstitutionByCode(beneficiary.getBeneficiaryBank());
        if (institution == null) {

            attributes.addFlashAttribute("failure", messages.getMessage("transfer.beneficiary.invalid", null, locale));
            return "redirect:/retail/transfer/interbank/index";
        }
        requestDTO.setFinancialInstitution(institution);

        model.addAttribute("transferRequest", requestDTO);
        model.addAttribute("beneficiary", localBeneficiaryService.convertEntityToDTO(beneficiary));
        String benName = beneficiary.getPreferredName()!=null?beneficiary.getPreferredName():beneficiary.getAccountName();
        request.getSession().setAttribute("beneficiaryName", benName);
        model.addAttribute("benName", benName);
        request.getSession().setAttribute("Lbeneficiary", localBeneficiaryService.convertEntityToDTO(beneficiary));
        return page + "pageii";
    }


    @ModelAttribute
    public void getOtherBankBeneficiaries(Model model) {


        List<FinancialInstitutionDTO> sortedNames = financialInstitutionService.getOtherLocalBanks(bankCode);
        sortedNames.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));

        model.addAttribute("localBanks"
                , sortedNames);


    }


    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("transferRequest") TransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {
        String type = (String) request.getSession().getAttribute("NIP");
        if ("RTGS".equalsIgnoreCase(type)) {
            transferRequestDTO.setTransferType(TransferType.RTGS);


        } else {
            transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        }
        model.addAttribute("transferRequest", transferRequestDTO);
        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            LocalBeneficiaryDTO dto = (LocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            String benName = dto.getPreferredName()!=null?dto.getPreferredName():dto.getAccountName();
            model.addAttribute("benName", benName);
            String bank = dto.getBeneficiaryBank();
            transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(bank));
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
                    .forEach(i -> accountList.add(i));
            model.addAttribute("accountList", accountList);




        }

    }
    @ResponseBody
    @GetMapping("{amount}/fee")
    public String getInterBankTransferFee(@PathVariable("amount") String amount) {
       String fee="";
        System.out.println("amount"+amount);
        try {
           fee=transferUtils.getFee("NIP", amount);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return fee;

    }
/*
    @ModelAttribute
    public void setLocationParams(Model model) {
        model.addAttribute("key", geolocationKey);
        model.addAttribute("url", geolocationUrl);

    }*/


}
