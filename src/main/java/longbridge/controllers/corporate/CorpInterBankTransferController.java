package longbridge.controllers.corporate;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.*;
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

@Controller
@RequestMapping(value = "/corporate/transfer/interbank")
public class CorpInterBankTransferController {


    private CorporateUserService corporateUserService;
    private CorpTransferService corpTransferService;
    private MessageSource messages;
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferValidator validator;
    private CorporateService corporateService;
    private IntegrationService integrationService;
    private AccountService accountService;
    private TransferUtils transferUtils;
    private TransferErrorService transferErrorService;
    private String page = "corp/transfer/interbank/";
    @Value("${bank.code}")
    private String bankCode;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorpInterBankTransferController(CorporateUserService corporateUserService, CorpTransferService corpTransferService, MessageSource messages, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferValidator validator, CorporateService corporateService, IntegrationService integrationService, AccountService accountService, TransferUtils transferUtils, TransferErrorService transferErrorService) {
        this.corporateUserService = corporateUserService;
        this.corpTransferService = corpTransferService;
        this.messages = messages;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.validator = validator;
        this.corporateService = corporateService;
        this.integrationService = integrationService;
        this.accountService = accountService;
        this.transferUtils = transferUtils;
        this.transferErrorService = transferErrorService;
    }

    @GetMapping(value = "")
    public String index(Model model, HttpServletRequest request, RedirectAttributes rd) {
        if (request.getSession().getAttribute("auth-needed") != null)
            request.getSession().removeAttribute("auth-needed");
        try {
            transferUtils.validateTransferCriteria();
            return page + "pagei";
        } catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            rd.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/dashboard";


        }
    }




    @RequestMapping(value = "/index", method = {RequestMethod.POST, RequestMethod.GET})
    public String startTransfer(HttpServletRequest request, Model model, Principal principal) {

        if(principal == null){
            return "redirect:/corporate/logout";
        }
        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        Corporate corporate = user.getCorporate();
        List<CorpLocalBeneficiary> beneficiaries = StreamSupport.stream(corpLocalBeneficiaryService.getCorpLocalBeneficiaries().spliterator(), false)
                .filter(i -> !i.getBeneficiaryBank().equalsIgnoreCase(financialInstitutionService.getFinancialInstitutionByCode(bankCode).getInstitutionCode()))
                .collect(Collectors.toList());

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

        CorpTransferRequestDTO requestDTO = new CorpTransferRequestDTO();
        String type = request.getParameter("tranType");

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
    public String newBeneficiary(@ModelAttribute("corpLocalBeneficiary") CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, Model model, RedirectAttributes redirectAttributes) throws Exception {

        try {
            transferUtils.validateTransferCriteria();
            model.addAttribute("localBanks",
                    financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL)
                            .stream()
                            .filter(i -> !i.getInstitutionCode().equals(bankCode))
                            .collect(Collectors.toList())
            );

            return page + "pageiB";
        } catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/dashboard";


        }


    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("corpLocalBeneficiary") @Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("corpLocalBeneficiaryDTO", corpLocalBeneficiaryDTO);
        if (servletRequest.getSession().getAttribute("add") != null)
            servletRequest.getSession().removeAttribute("add");
        if (result.hasErrors()) {
            return page + "pageiB";
        }

        CorpTransferRequestDTO corpTransferRequestDTO = new CorpTransferRequestDTO();
        corpTransferRequestDTO.setBeneficiaryAccountName(corpLocalBeneficiaryDTO.getAccountName());
        corpTransferRequestDTO.setBeneficiaryAccountNumber(corpLocalBeneficiaryDTO.getAccountNumber());

        corpTransferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByCode(corpLocalBeneficiaryDTO.getBeneficiaryBank()));
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        model.addAttribute("benName", corpLocalBeneficiaryDTO.getPreferredName());



        servletRequest.getSession().setAttribute("Lbeneficiary", corpLocalBeneficiaryDTO);
        if (servletRequest.getParameter("add") != null)
            servletRequest.getSession().setAttribute("add", "add");
        return page + "pageii";
    }


    @PostMapping("/summary")
    public String transferSummary(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO corpTransferRequestDTO, BindingResult result, Model model, HttpServletRequest request) throws Exception {
        String transferType = (String) request.getSession().getAttribute("NIP");

        String newBenName = (String) request.getSession().getAttribute("benName");
        String userAmountLimit = transferUtils.getLimitForAuthorization(corpTransferRequestDTO.getCustomerAccountNumber(), transferType);
        BigDecimal amountLimit = new BigDecimal(userAmountLimit);
        BigDecimal userAmount = corpTransferRequestDTO.getAmount();
        int a = amountLimit.intValue();
        int b = userAmount.intValue();
        if (b > a){
            String errorMessage = "You can not transfer more than account limit";
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
            model.addAttribute("benName", newBenName);
            return page + "pageii";
        }

        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        String benName = (String) request.getSession().getAttribute("benName");
        String charge = "NAN";

        model.addAttribute("benName", benName);

        validator.validate(corpTransferRequestDTO, result);

        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            CorpLocalBeneficiaryDTO beneficiary = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            model.addAttribute("beneficiary", beneficiary);
            if (beneficiary.getId() == null)
                model.addAttribute("newBen", "newBen");
        }

        if (result.hasErrors()) {

            return page + "pageii";
        }

        if (request.getSession().getAttribute("NIP") != null) {
            String type = (String) request.getSession().getAttribute("NIP");

            if (type.equalsIgnoreCase("RTGS")) {
                //TODO used RTGS

                corpTransferRequestDTO.setTransferType(TransferType.RTGS);
                charge = integrationService.getFee("RTGS",String.valueOf(corpTransferRequestDTO.getAmount())).getFeeValue();

            } else if (type.equalsIgnoreCase("NIP")){

                charge = integrationService.getFee("NIP",String.valueOf(corpTransferRequestDTO.getAmount())).getFeeValue();

                corpTransferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
            } else if (type.equalsIgnoreCase("NEFT")){
                logger.info("Processing transfer using NEFT");
                charge = integrationService.getFee("NEFT",String.valueOf(corpTransferRequestDTO.getAmount())).getFeeValue();
                corpTransferRequestDTO.setTransferType(TransferType.NEFT);
            }
//            request.getSession().removeAttribute("NIP");

        } else {
            corpTransferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
            charge = integrationService.getFee("NIP").getFeeValue();
        }
        request.getSession().setAttribute("corpTransferRequest", corpTransferRequestDTO);
        model.addAttribute("charge", charge);
        return page + "pageiii";
    }

    @GetMapping("/{id}")
    public String transfer(@PathVariable Long id, Model model, HttpServletRequest request, Locale locale) throws Exception {
        CorpLocalBeneficiary beneficiary = corpLocalBeneficiaryService.getCorpLocalBeneficiary(id);
        CorpTransferRequestDTO requestDTO = new CorpTransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());
        requestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);
        FinancialInstitution institution = financialInstitutionService.getFinancialInstitutionByCode(beneficiary.getBeneficiaryBank());
        if (institution == null) {

            model.addAttribute("failure", messages.getMessage("transfer.beneficiary.invalid", null, locale));
            return page + "pageiA";
        }
        requestDTO.setFinancialInstitution(institution);

        model.addAttribute("corpTransferRequest", requestDTO);
        model.addAttribute("beneficiary", corpLocalBeneficiaryService.convertEntityToDTO(beneficiary));
        String benName = beneficiary.getPreferredName()!=null?beneficiary.getPreferredName():beneficiary.getAccountName();
        model.addAttribute("benName", benName);
        request.getSession().setAttribute("benName", benName);
        request.getSession().setAttribute("Lbeneficiary", corpLocalBeneficiaryService.convertEntityToDTO(beneficiary));
        return page + "pageii";
    }


    @ModelAttribute
    public void getOtherBankBeneficiaries(Model model) {


        List<FinancialInstitutionDTO> sortedNames = financialInstitutionService.getOtherLocalBanks(bankCode);
        sortedNames.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));

        model.addAttribute("banks", sortedNames);



    }

    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("corpTransferRequest") CorpTransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {
        String type = (String) request.getSession().getAttribute("NIP");
        if (type.equalsIgnoreCase("RTGS")) {
            transferRequestDTO.setTransferType(TransferType.RTGS);

        } else {
            transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);

        }


        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            CorpLocalBeneficiaryDTO dto = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            String benName = dto.getPreferredName()!=null?dto.getPreferredName():dto.getAccountName();
            model.addAttribute("benName", benName);
            transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByName(dto.getBeneficiaryBank()));
        }
        model.addAttribute("corpTransferRequest", transferRequestDTO);
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

                    .forEach(i -> accountList.add(i));


            model.addAttribute("accountList", accountList);


        }

    }

    @ResponseBody
    @GetMapping("{amount}/fee")
    public String getInterBankTransferFee(@PathVariable("amount") String amount) {
        String fee="";
        try {
            fee=transferUtils.getFee("NIP", amount);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return fee;

    }
}