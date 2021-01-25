package longbridge.controllers.corporate;

import longbridge.dtos.*;
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
    private final CorpNeftBeneficiaryService corpNeftBeneficiaryService;
    private final CodeService codeService;
    private final NeftBankService neftBankService;
    private final String page = "corp/transfer/interbank/";
    @Value("${bank.code}")
    private String bankCode;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CorpInterBankTransferController(CorporateUserService corporateUserService, CorpTransferService corpTransferService, MessageSource messages, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, QuicktellerBankCodeService quicktellerBankCodeService, CorpQuickBeneficiaryService corpQuickBeneficiaryService, TransferValidator validator, CorporateService corporateService, IntegrationService integrationService, AccountService accountService, TransferUtils transferUtils, TransferErrorService transferErrorService, CorpNeftBeneficiaryService corpNeftBeneficiaryService, CodeService codeService, NeftBankService neftBankService) {
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
        this.corpNeftBeneficiaryService = corpNeftBeneficiaryService;
        this.codeService = codeService;
        this.neftBankService = neftBankService;
    }

    @ModelAttribute
    public void getNeftbanks(Model model) {
//        List<CodeDTO> bankNames = codeService.getCodesByType("NEFT_BANKS");
        List<NeftBankDTO> bankNames = neftBankService.getNeftBankList();
        Set<String> names = bankNames
                .stream()
                .map(NeftBankDTO::getBankName)
                .collect(Collectors.toSet());
        Collections.sort(new ArrayList<>(names));
        model.addAttribute("neftBanks"
                ,names);

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
        Iterable<CorpNeftBeneficiary> neftBeneficiaries = corpNeftBeneficiaryService.getCorpNeftBeneficiaries();



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
            request.getSession().setAttribute("beneficiaryType", CorpNeftBeneficiary.Type.CR.getType());
            model.addAttribute("neftBeneficiaries", neftBeneficiaries);
            return page + "/neft/pageiAc";
        } else if ("NEFT DEBIT".equalsIgnoreCase(type)){
            request.getSession().setAttribute("NIP", "NEFT");
            requestDTO.setTransferType(TransferType.NEFT);
            model.addAttribute("transferRequest", requestDTO);
            request.getSession().setAttribute("beneficiaryType", CorpNeftBeneficiary.Type.DB.getType());
            model.addAttribute("neftBeneficiaries", neftBeneficiaries);
            return page + "/neft/pageiAc";
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

    @GetMapping("/neft")
    public String newNeftBeneficiary(Model model, CorpNeftBeneficiaryDTO neftBeneficiaryDTO, HttpServletRequest request) throws Exception {
        String type = (String) request.getSession().getAttribute("beneficiaryType");
        logger.info("beneficiary Type : {} ", type);
        neftBeneficiaryDTO.setBeneficiaryType(type);
        model.addAttribute("neftBeneficiaryDTO", neftBeneficiaryDTO);
        return page + "neft/pageiBN";
    }

//    @PostMapping("/new/alpha")
    @RequestMapping(value = "/new/alpha", method = {RequestMethod.GET, RequestMethod.POST})
    public String getBeneficiary(@ModelAttribute("neftBeneficiaryDTO") @Valid CorpNeftBeneficiaryDTO neftBeneficiaryDTO, BindingResult result, Model model, HttpServletRequest servletRequest, Principal principal) throws Exception {
        model.addAttribute("neftBeneficiaryDTO", neftBeneficiaryDTO);

        if (servletRequest.getSession().getAttribute("add") != null)
            servletRequest.getSession().removeAttribute("add");
        if (result.hasErrors()) {
            return page + "neft/pageiBN";
        }
        System.out.println("This is the beneficiary : " + neftBeneficiaryDTO);

        CorpNeftRequestDTO transferRequestDTO = new CorpNeftRequestDTO();
        transferRequestDTO.setBeneficiaryAccountName(neftBeneficiaryDTO.getBeneficiaryAccountName());
        transferRequestDTO.setBeneficiaryAccountNumber(neftBeneficiaryDTO.getBeneficiaryAccountNumber());
        transferRequestDTO.setBeneficiaryBVN(neftBeneficiaryDTO.getBeneficiaryBVN());
        transferRequestDTO.setBeneficiaryBankName(neftBeneficiaryDTO.getBeneficiaryBankName());
        transferRequestDTO.setBeneficiarySortCode(neftBeneficiaryDTO.getBeneficiarySortCode());
        transferRequestDTO.setBeneficiaryType(neftBeneficiaryDTO.getBeneficiaryType());
        transferRequestDTO.setBeneficiaryCurrencyCode(neftBeneficiaryDTO.getBeneficiaryCurrencyCode());


        logger.info("Beneficiary is {}",transferRequestDTO.getBeneficiaryAccountName());
        logger.info("Neft Transfer Request is {} ", transferRequestDTO);
        model.addAttribute("accounts" ,getBanksForNeft(principal, neftBeneficiaryDTO.getBeneficiaryCurrencyCode()));
        model.addAttribute("corpTransferRequest", transferRequestDTO);
        model.addAttribute("benName", neftBeneficiaryDTO.getBeneficiaryAccountName());

        servletRequest.getSession().setAttribute("Nbeneficiary", neftBeneficiaryDTO);

        if (servletRequest.getParameter("add") != null){
            servletRequest.getSession().setAttribute("add", "add");
        }
        return page + "neft/pageiN2";
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

    @RequestMapping(value = "/summary/alpha", method = {RequestMethod.POST , RequestMethod.GET})
    public String neftTransferSummary(@ModelAttribute("corpTransferRequest") @Valid CorpNeftRequestDTO transferRequestDTO1, BindingResult result, Model model, HttpServletRequest request) throws Exception {

        String newbenName = (String) request.getSession().getAttribute("beneficiaryName");
        logger.info("I GOT HERE WITH ALL DETAILS {}", transferRequestDTO1);
        CorpTransferRequestDTO transferRequestDTO = convertToTransferRequest(transferRequestDTO1);
        model.addAttribute("corpTransferRequest", transferRequestDTO);
        String charge = "NAN";
        String benName = (String) request.getSession().getAttribute("benName");
        model.addAttribute("benName", benName);
        if (result.hasErrors()) {
            return page + "neft/pageiN2";
        }
        if (request.getSession().getAttribute("NIP") != null) {
            String type = (String) request.getSession().getAttribute("NIP");
            if("NEFT".equalsIgnoreCase(type)){
                transferRequestDTO.setTransferType(TransferType.NEFT);
                charge = transferUtils.getFee("NEFT",String.valueOf(transferRequestDTO.getAmount()));
                transferRequestDTO.setCharge(charge);
            }
        }

        request.getSession().setAttribute("corpTransferRequest", transferRequestDTO);
        logger.info("Neft Transfer Request summary {} ", transferRequestDTO);
        model.addAttribute("charge", charge);
        return page + "neft/neftsummary";
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

    @GetMapping("neft/{id}/transfer")
    public String neftTransfer(@PathVariable Long id, Model model, HttpServletRequest request, Principal principal) throws Exception {
        CorpNeftBeneficiary beneficiary = corpNeftBeneficiaryService.getCorpNeftBeneficiary(id);

        CorpTransferRequestDTO requestDTO = new CorpTransferRequestDTO();
        requestDTO.setBeneficiaryAccountName(beneficiary.getBeneficiaryAccountName());
        requestDTO.setBeneficiaryAccountNumber(beneficiary.getBeneficiaryAccountNumber());
        requestDTO.setBeneficiaryBVN(beneficiary.getBeneficiaryBVN());
        requestDTO.setBeneficiarySortCode(beneficiary.getBeneficiarySortCode());
        requestDTO.setBeneficiaryBankName(beneficiary.getBeneficiaryBankName());
        requestDTO.setTransferType(TransferType.NEFT);
        requestDTO.setBeneficiaryCurrencyCode(beneficiary.getBeneficiaryCurrencyCode());
        requestDTO.setBeneficiaryType(beneficiary.getBeneficiaryType());
//
        model.addAttribute("accounts" ,getBanksForNeft(principal, beneficiary.getBeneficiaryCurrencyCode()));
        model.addAttribute("corpTransferRequest", requestDTO);
        model.addAttribute("beneficiary", corpNeftBeneficiaryService.convertEntityToDTO(beneficiary));
        request.getSession().setAttribute("beneficiaryName", beneficiary.getBeneficiaryAccountName());
        model.addAttribute("benName", beneficiary.getBeneficiaryAccountName());
        request.getSession().setAttribute("Nbeneficiary", corpNeftBeneficiaryService.convertEntityToDTO(beneficiary));
        return page + "neft/pageiN2";
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


    @PostMapping("neft/edit")
    public String editNeftTransfer(@ModelAttribute("corpTransferRequest") CorpTransferRequestDTO transferRequestDTO, Model model, Principal principal) {
        model.addAttribute("corpTransferRequest", transferRequestDTO);
        model.addAttribute("accounts", getBanksForNeft(principal, transferRequestDTO.getBeneficiaryCurrencyCode()));
        model.addAttribute("benName", transferRequestDTO.getBeneficiaryAccountName());
        return page + "neft/pageiN2";
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



//    @ModelAttribute
//    public void getBanksForNeft(Model model, Principal principal, String currencyCode) {
    public List<Account> getBanksForNeft(Principal principal, String currencyCode) {
        List<Account> accountList = new ArrayList<>();
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        if (user != null) {
            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCorporate().getAccounts());
            accountList = StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
//                    .filter(account -> account.getCurrencyCode().equalsIgnoreCase("NGN") || account.getCurrencyCode().equalsIgnoreCase("USD"))
                    .filter(account -> account.getCurrencyCode().equalsIgnoreCase(currencyCode))
                    .collect(Collectors.toList());

//            model.addAttribute("accounts", accountList);
        }
        return accountList;
    }

    @ResponseBody
    @GetMapping("user/account/{accountNumber}")
    public AccountDetailsDTO getTransferDetails(@PathVariable("accountNumber") String accountNumber, Model model, HttpServletRequest request) {
        Object obj = model.getAttribute("accounts");
        AccountDetailsDTO dto = new AccountDetailsDTO();
        String type = (String)request.getSession().getAttribute("beneficiaryType");
        if(obj != null){
            List<Account> accountList = (List<Account>) obj;
            Account account = accountList.stream()
                    .filter(Objects::nonNull)
                    .filter(account1 -> account1.getAccountNumber().equalsIgnoreCase(accountNumber))
                    .findFirst().get();

            dto.setCurrencyCode(account.getCurrencyCode());
            if(type.equalsIgnoreCase(NeftBeneficiary.Type.CR.getType())){
                if(account.getCurrencyCode().equalsIgnoreCase("NGN")){
                    dto.setCollectionType("30");
                    dto.setInstrumentType("CR");
                }else if(account.getCurrencyCode().equalsIgnoreCase("USD")){
                    dto.setCollectionType("37");
                    dto.setInstrumentType("CR");
                }
            }else if(type.equalsIgnoreCase(NeftBeneficiary.Type.DB.getType())){
                if(account.getCurrencyCode().equalsIgnoreCase("NGN")){
                    dto.setCollectionType("71");
                    dto.setInstrumentType("DB");
                }else if(account.getCurrencyCode().equalsIgnoreCase("USD")){
                    dto.setCollectionType("38");
                    dto.setInstrumentType("DB");
                }
            }
        }
        return dto;
    }

    @ResponseBody
    @GetMapping("neft/{bankName}/branch")
    public List<NeftBankDTO> getNeftBankBranch(@PathVariable("bankName") String bankName) {
//        return codeService.getCodesByTypeAndDescription("NEFT_BANKS", bankName);
        return neftBankService.getNeftBranchesByBankName(bankName);
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

    private CorpTransferRequestDTO convertToTransferRequest(CorpNeftRequestDTO nft){
        CorpTransferRequestDTO trt = new CorpTransferRequestDTO();
        trt.setBeneficiaryBVN(nft.getBeneficiaryBVN());
        trt.setBeneficiaryAccountNumber(nft.getBeneficiaryAccountNumber());
        trt.setBeneficiaryAccountName(nft.getBeneficiaryAccountName());
        trt.setBeneficiaryBankName(nft.getBeneficiaryBankName());
        trt.setBeneficiaryBank(nft.getBeneficiaryBankName());
        trt.setBeneficiarySortCode(nft.getBeneficiarySortCode());
        trt.setInstrumentType(nft.getInstrumentType());
        trt.setCollectionType(nft.getCollectionType());
        trt.setCurrencyCode(nft.getCurrencyCode());
        trt.setCustomerAccountNumber(nft.getCustomerAccountNumber());
        trt.setTransferType(TransferType.NEFT);
        trt.setAmount(new BigDecimal(nft.getAmount()));
        trt.setNarration(nft.getNarration());
        trt.setRemarks(nft.getNarration());
        trt.setCharge(nft.getCharge());
        trt.setChannel(nft.getChannel());
        trt.setBeneficiaryCurrencyCode(nft.getBeneficiaryCurrencyCode());
        trt.setBeneficiaryType(nft.getBeneficiaryType());
        return trt;

    }
}