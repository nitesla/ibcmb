package longbridge.controllers.corporate;

import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.QuicktellerBankCodeDTO;
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
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping(value = "/corporate/transfer/interbank")
public class CorpInterBankTransferController {


    private final CorporateUserService corporateUserService;
    private final MessageSource messages;
    private final CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private final FinancialInstitutionService financialInstitutionService;
    private final CorpQuickBeneficiaryService corpQuickBeneficiaryService;
    private final QuicktellerBankCodeService quicktellerBankCodeService;
    private final TransferValidator validator;
    private final IntegrationService integrationService;
    private final AccountService accountService;
    private final TransferUtils transferUtils;
    private final TransferErrorService transferErrorService;
    private final String page = "corp/transfer/interbank/";
    @Value("${bank.code}")
    private String bankCode;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorpInterBankTransferController(CorporateUserService corporateUserService, CorpTransferService corpTransferService, MessageSource messages, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, QuicktellerBankCodeService quicktellerBankCodeService, CorpQuickBeneficiaryService corpQuickBeneficiaryService, TransferValidator validator, CorporateService corporateService, IntegrationService integrationService, AccountService accountService, TransferUtils transferUtils, TransferErrorService transferErrorService) {
        this.corporateUserService = corporateUserService;
        this.messages = messages;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.quicktellerBankCodeService = quicktellerBankCodeService;
        this.corpQuickBeneficiaryService = corpQuickBeneficiaryService;
        this.validator = validator;
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
        CorpTransferRequestDTO requestDTO = new CorpTransferRequestDTO();
        String type = request.getParameter("tranType");

        if ("QUICKTELLER".equalsIgnoreCase(type)) {

            List<CorpQuickBeneficiary> beneficiaries = StreamSupport.stream(corpQuickBeneficiaryService.getCorpQuickBeneficiaries().spliterator(), false)
//                    .filter(i -> !i.getBeneficiaryBank().equalsIgnoreCase(quicktellerBankCodeService.getQuicktellerBankCodeByBankCode(bankCode).getBankCode()))
                    .collect(Collectors.toList());

            beneficiaries
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(i ->
                            {
                                QuicktellerBankCode quicktellerBankCode = quicktellerBankCodeService.getQuicktellerBankCodeByBankCode(i.getBeneficiaryBank());

                                logger.info("Bank Code id {}", quicktellerBankCode);

                                if (quicktellerBankCode != null)
                                    i.setBeneficiaryBank(quicktellerBankCode.getBankName());


                            }

                    );

            model.addAttribute("quickBen", beneficiaries);

        }else {

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

        }



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
        } else if ("NEFT".equalsIgnoreCase(type)){
            request.getSession().setAttribute("NIP", "NEFT");
            requestDTO.setTransferType(TransferType.NEFT);
            model.addAttribute("transferRequest", requestDTO);
            return page + "pageiAc";
        } else if ("QUICKTELLER".equalsIgnoreCase(type))
            request.getSession().setAttribute("NIP", "QUICKTELLER");
            requestDTO.setTransferType(TransferType.QUICKTELLER);
            model.addAttribute("transferRequest", requestDTO);
            return page + "quickteller/pageiAd";

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

    @GetMapping("/alpha")
    public String newQuickBeneficiary(@ModelAttribute("corpLocalBeneficiary") CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, Model model, RedirectAttributes redirectAttributes) throws Exception {
            return page + "quickteller/pageiC";
    }

    @PostMapping("/alpha")
    public String getQuickBeneficiary(@ModelAttribute("corpLocalBeneficiary") @Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result,
                                 Model model, HttpServletRequest servletRequest) throws Exception {
        model.addAttribute("corpLocalBeneficiaryDTO", corpLocalBeneficiaryDTO);
        if (servletRequest.getSession().getAttribute("add") != null)
            servletRequest.getSession().removeAttribute("add");
        if (result.hasErrors()) {
            return page + "quickteller/pageiC";
        }

        CorpTransferRequestDTO corpTransferRequestDTO = new CorpTransferRequestDTO();
        corpTransferRequestDTO.setBeneficiaryAccountName(corpLocalBeneficiaryDTO.getAccountName());
        corpTransferRequestDTO.setBeneficiaryAccountNumber(corpLocalBeneficiaryDTO.getAccountNumber());
        corpTransferRequestDTO.setLastname(corpLocalBeneficiaryDTO.getLastname());
        corpTransferRequestDTO.setFirstname(corpLocalBeneficiaryDTO.getFirstname());

        QuicktellerBankCode quicktellerBankCode = quicktellerBankCodeService.getQuicktellerBankCodeByBankCode(corpLocalBeneficiaryDTO.getBeneficiaryBank());

        logger.info("Bank Code id {}", quicktellerBankCode);

        if (quicktellerBankCode != null)

            corpTransferRequestDTO.setBeneficiaryBank(quicktellerBankCode.getBankName());


        logger.info("Beneficiary is {}",corpTransferRequestDTO);

        corpTransferRequestDTO.setQuicktellerBankCode(quicktellerBankCodeService.getQuicktellerBankCodeByBankCode(corpLocalBeneficiaryDTO.getBeneficiaryBank()));
        model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
        model.addAttribute("benName", corpLocalBeneficiaryDTO.getPreferredName());



        servletRequest.getSession().setAttribute("Lbeneficiary", corpLocalBeneficiaryDTO);
        if (servletRequest.getParameter("add") != null)
            servletRequest.getSession().setAttribute("add", "add");
        return page + "quickteller/pageQ";
    }

    @PostMapping("/new")
    public String getBeneficiary(@ModelAttribute("corpLocalBeneficiary") @Valid CorpLocalBeneficiaryDTO corpLocalBeneficiaryDTO, BindingResult result,
                                 Model model, HttpServletRequest servletRequest) throws Exception {
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
//        String userAmountLimit = transferUtils.getLimitForAuthorization(corpTransferRequestDTO.getCustomerAccountNumber(), transferType);
//        BigDecimal amountLimit = new BigDecimal(userAmountLimit);
//        BigDecimal userAmount = corpTransferRequestDTO.getAmount();
//        if (userAmount == null){
//            String amounterrorMessage = "Please supply amount";
//            model.addAttribute("amounterrorMessage", amounterrorMessage);
//            model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
//            model.addAttribute("benName", newBenName);
//            return page + "pageii";
//        }
//        int a = amountLimit.intValue();
//        int b = userAmount.intValue();
//
//        if (b > a){
//            String errorMessage = "You can not transfer more than account limit";
//            model.addAttribute("errorMessage", errorMessage);
//            model.addAttribute("corpTransferRequest", corpTransferRequestDTO);
//            model.addAttribute("benName", newBenName);
//            return page + "pageii";
//        }

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

//        if (result.hasErrors()) {
//            return page + "pageii";
//        }

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
            } else if (type.equalsIgnoreCase("QUICKTELLER")){
                logger.info("Processing transfer using QUICKTELLER");
                charge = integrationService.getFee("QUICKTELLER",String.valueOf(corpTransferRequestDTO.getAmount())).getFeeValue();
                corpTransferRequestDTO.setTransferType(TransferType.QUICKTELLER);
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

    @GetMapping("/input/{id}")
    public String quickTransfer(@PathVariable Long id, Model model, HttpServletRequest request, Locale locale) throws Exception {
        CorpQuickBeneficiary beneficiary = corpQuickBeneficiaryService.getCorpQuickBeneficiary(id);
        CorpTransferRequestDTO requestDTO = new CorpTransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getAccountNumber());

        QuicktellerBankCode quicktellerBankCode = quicktellerBankCodeService.getQuicktellerBankCodeByBankCode(beneficiary.getBeneficiaryBank());
        requestDTO.setBeneficiaryBank(quicktellerBankCode.getBankName());

        requestDTO.setTransferType(TransferType.QUICKTELLER);
        requestDTO.setFirstname(beneficiary.getOthernames());
        requestDTO.setLastname(beneficiary.getLastname());
        QuicktellerBankCode bankCode = quicktellerBankCodeService.getQuicktellerBankCodeByBankCode(beneficiary.getBeneficiaryBank());
        if (bankCode == null) {

            model.addAttribute("failure", messages.getMessage("transfer.beneficiary.invalid", null, locale));
            return page + "quickteller/pageiAd";
        }
        requestDTO.setQuicktellerBankCode(bankCode);

        model.addAttribute("corpTransferRequest", requestDTO);
        model.addAttribute("beneficiary", corpQuickBeneficiaryService.convertEntityToDTO(beneficiary));
        String benName = beneficiary.getPreferredName()!=null?beneficiary.getPreferredName():beneficiary.getAccountName();
        model.addAttribute("benName", benName);
        request.getSession().setAttribute("benName", benName);
        request.getSession().setAttribute("Lbeneficiary", corpQuickBeneficiaryService.convertEntityToDTO(beneficiary));
        return page + "quickteller/pageQ";
    }


    @ModelAttribute
    public void getOtherBankBeneficiaries(Model model) {


        List<FinancialInstitutionDTO> sortedNames = financialInstitutionService.getOtherLocalBanks(bankCode);
        sortedNames.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));

        model.addAttribute("banks", sortedNames);



    }

    @ModelAttribute
    public void getQuicktellerBeneficiaries(Model model) {


        List<QuicktellerBankCodeDTO> sortedNames = quicktellerBankCodeService.getOtherLocalBanks();
//        sortedNames.sort(Comparator.comparing(QuicktellerBankCodeDTO::getBankName));

        logger.info("Account Banks are: {}", sortedNames);

        model.addAttribute("quickBanks", sortedNames);

    }

    @PostMapping("/edit")
    public String editTransfer(@ModelAttribute("corpTransferRequest") CorpTransferRequestDTO transferRequestDTO, Model model, HttpServletRequest request) {
        String type = (String) request.getSession().getAttribute("NIP");
        if (type.equalsIgnoreCase("RTGS")) {
            transferRequestDTO.setTransferType(TransferType.RTGS);

        } else if (type.equalsIgnoreCase("NEFT")){
            transferRequestDTO.setTransferType(TransferType.NEFT);

        } else {
            transferRequestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);

        }


        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            CorpLocalBeneficiaryDTO dto = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            String benName = dto.getPreferredName()!=null?dto.getPreferredName():dto.getAccountName();
            model.addAttribute("benName", benName);
           transferRequestDTO.setFinancialInstitution(financialInstitutionService.getFinancialInstitutionByName(dto.getBeneficiaryBank()));
           logger.info("DETAILS == {}", financialInstitutionService.getFinancialInstitutionByName(dto.getBeneficiaryBank()));

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

                    .forEach(accountList::add);


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