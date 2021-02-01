package longbridge.controllers.retail;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.models.*;
import longbridge.repositories.LocalBeneficiaryRepo;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.TransferType;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Chigozirim Torti
 */
@Controller
@RequestMapping("/retail/directdebit")
public class DirectDebitController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageSource messageSource;
    private final DirectDebitService directDebitService;
    private final LocalBeneficiaryService localBeneficiaryService;
    private final FinancialInstitutionService financialInstitutionService;
    private final SecurityService securityService;

    private final AccountService accountService;

    final LocalBeneficiaryRepo localBeneficia;
    private final SettingsService configService;


    private final RetailUserService retailUserService;

    private final CodeService codeService;
    @Autowired
    private TransactionLimitService limitService;

    @Autowired
    private ModelMapper modelMapper;

    private final String page = "cust/directdebit/";
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public DirectDebitController(MessageSource messageSource, DirectDebitService directDebitService,
                                 LocalBeneficiaryService localBeneficiaryService,
                                 FinancialInstitutionService financialInstitutionService,
                                 SecurityService securityService,
                                 AccountService accountService, LocalBeneficiaryRepo localBeneficia,
                                 SettingsService configService,
                                 RetailUserService retailUserService,
                                 CodeService codeService
    ) {
        this.messageSource = messageSource;
        this.directDebitService = directDebitService;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.securityService = securityService;
        this.accountService = accountService;
        this.localBeneficia = localBeneficia;
        this.configService = configService;
        this.retailUserService = retailUserService;
        this.codeService = codeService;
    }

    /**
     * view existing standing order
     */

    @GetMapping()
    public String getDirectDebitForUser(){
        return "cust/directdebit/view";
    }

    @GetMapping("/all")
    @ResponseBody
    public DataTablesOutput<DirectDebitDTO> getDirectDebits(DataTablesInput input,Principal principal) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Page<DirectDebitDTO> page = directDebitService.getUserDirectDebitDTOs(retailUser,pageable);
        for (DirectDebitDTO dto : page) {
            try {
                FinancialInstitution fi = financialInstitutionService.getFinancialInstitutionByCode(dto.getBeneficiary().getBeneficiaryBank());
                if (null != dto.getBeneficiary())
                    dto.getBeneficiary().setBeneficiaryBank(fi.getInstitutionName());
            } catch (NullPointerException e) {
                logger.info("Local beneficiary does not exist");
            }
        }
        DataTablesOutput<DirectDebitDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(page.getContent());
        out.setRecordsFiltered(page.getTotalElements());
        out.setRecordsTotal(page.getTotalElements());
        return out;
    }


    /**
     * view all payments scheduled for a selected direct debit
     */

    @GetMapping("/payments/{dirId}")
    public String getPayments(Model model, @PathVariable Long dirId) {
        DirectDebit directDebit = directDebitService.getDirectDebit(dirId);
        Collection<PaymentDTO> payments = directDebitService.getPayments(directDebit);
        model.addAttribute("payments", payments);
        return "cust/directdebit/payments";
    }

    /**
     * displays beneficiary  form
     */

    @GetMapping("/ben/new")
    public String newBeneficiary(Model model, LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {
        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        return page + "pageiB";
    }

    /**
     * display of existing beneficiaries
     */

    @GetMapping(value = "/index")
    public String viewDebitBeneficiaries(HttpServletRequest request, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/retail/logout";
        }
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        List<LocalBeneficiary> beneficiaries = StreamSupport.stream(localBeneficiaryService.getLocalBeneficiaries().spliterator(), false)
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

        TransferRequestDTO requestDTO = new TransferRequestDTO();
        String type = request.getParameter("tranType");


        if ("NIP".equalsIgnoreCase(type)) {

            request.getSession().setAttribute("NIP", "NIP");
            requestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);

            model.addAttribute("transferRequest", requestDTO);
            return page + "pageiA";
        } else {
            request.getSession().setAttribute("NIP", "RTGS");
            requestDTO.setTransferType(TransferType.RTGS);

            model.addAttribute("transferRequest", requestDTO);
            return page + "pageiAb";
        }


    }

    /**
     * display of new standing order form
     */

    @GetMapping("/{id}/new")
    public String addDirectDebit(@PathVariable Long id, Model model, Principal principal, DirectDebitDTO directDebitDTO) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<LocalBeneficiary> bens = new ArrayList<>();

        LocalBeneficiary beneficiary = localBeneficiaryService.getLocalBeneficiary(id);
        List<Account> accounts = accountService.getCustomerAccounts(retailUser.getCustomerId());
        Iterable<Account> accountsForDebit = accountService.getAccountsForDebit(accounts);
        bens.add(beneficiary);
        model.addAttribute("beneficiaries", bens);
        model.addAttribute("accounts", accountsForDebit);
        model.addAttribute("directDebit", directDebitDTO);
        model.addAttribute("benefBank", beneficiary.getBeneficiaryBank());
        model.addAttribute("durations", codeService.getCodesByType("STANDING_ORDER_FREQ"));
        return "cust/directdebit/add";
    }

    /**
     * redirects beneficiary creation request for token authentication
     */

    @PostMapping("/ben/new")
    public String createBeneficiary(@ModelAttribute("localBeneficiaryDTO") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, HttpSession session, BindingResult result) {
        if (result.hasErrors()) {
            return page + "pageiAb";
        }

        if (session.getAttribute("authenticated") == null) {
            session.setAttribute("requestDTO", localBeneficiaryDTO);
            session.setAttribute("redirectURL", "/retail/directdebit/ben/create");
            return "redirect:/retail/token/authenticate";
        }

        return "redirect:/retail/directdebit";

    }

    /**
     * create beneficiary after token authentication
     */
    @GetMapping("/ben/create")
    public String createBeneficaryTokenAuthenticated(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        RetailUser user = retailUserService.getUserByName(principal.getName());
        logger.info("about to create beneficiary");
        if (session.getAttribute("requestDTO") != null) {
            logger.info("found requestDTO");
            LocalBeneficiaryDTO localBeneficiaryDTO = (LocalBeneficiaryDTO) session.getAttribute("requestDTO");
            if (session.getAttribute("authenticated") != null) {
                logger.info("request has been authenticated !");
                try {
                    String message2 = localBeneficiaryService.addLocalBeneficiary(localBeneficiaryDTO);
                    redirectAttributes.addFlashAttribute("message", message2);
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    LocalBeneficiary beneficiary = localBeneficia.findByUser_IdAndAccountNumber(user.getId(), localBeneficiaryDTO.getAccountNumber());
                    logger.info("beficary id {}", beneficiary.getId());

                    return "redirect:/retail/directdebit/" + beneficiary.getId() + "/new";

                } catch (InternetBankingException e) {
                    e.printStackTrace();
                    try {
                        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage(e.getMessage(), null, locale));
                    } catch (Exception ex) {
                        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("beneficiary.add.failure", null, locale));

                    }

                }
            }
        }

        return "redirect:/retail/directdebit/ben/new";

    }

    /**
     * save direct debit to database
     */

    @PostMapping
    public String createDirectDebit(@ModelAttribute("directDebit") @Valid DirectDebitDTO directDebitDTO, Principal principal, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        if (result.hasErrors()) {
            return "cust/directdebit/add";
        }
        logger.info("the beneficiary id {}", directDebitDTO.getId());
//        if (session.getAttribute("authenticated") == null) {
//            session.setAttribute("requestDTO", directDebitDTO);
//            session.setAttribute("redirectURL", "/retail/directdebit/process");
//            return "redirect:/retail/token/authenticate";
//        }
        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", directDebitDTO);
            session.setAttribute("redirectURL", "/retail/directdebit/process");
            return "redirect:/retail/token/authenticate";
        }
        RetailUser user = retailUserService.getUserByName(principal.getName());
        try {
            String message = directDebitService.addDirectDebit(user, directDebitDTO);
            model.addAttribute("success", message);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (Exception e) {
            logger.error("Direct debit Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/retail/directdebit/new";
        }
        return "redirect:/retail/directdebit";
    }

    /**
     * displays summary of direct debit request for review
     */
    @PostMapping("/summary")
    public String getDirectDebitSummary(@ModelAttribute("directDebit") @Valid DirectDebitDTO directDebitDTO, Principal principal, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        if (result.hasErrors()) {
            return "cust/directdebit/add";
        }
        logger.info("the beneficiary id {}", directDebitDTO.getId());
        model.addAttribute("directDebit", directDebitDTO);
        return "cust/directdebit/summary";
    }

    @GetMapping("/process")
    public String processRequest(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {


        RetailUser user = retailUserService.getUserByName(principal.getName());

        if (session.getAttribute("requestDTO") != null) {
            DirectDebitDTO directDebitDTO = (DirectDebitDTO) session.getAttribute("requestDTO");

            if (session.getAttribute("authenticated") != null) {

                try {
                    String message = directDebitService.addDirectDebit(user, directDebitDTO);
                    model.addAttribute("success", message);
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                } catch (InternetBankingException e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    return "redirect:/retail/directdebit/new";
                } catch (Exception e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/retail/directdebit/new";
                }
            }

        }
        return "redirect:/retail/directdebit";
    }
   /* @GetMapping("/{directDebitId}/delete")
    public String deleteDirectDebit(@PathVariable Long directDebitId, Model model){
        directDebitService.deleteDirectDebit(directDebitId);
        model.addAttribute("success","Direct Debit deleted successfully");
        return "redirect:/retail/directdebit";
    }*/

    /**
     * delete direct debit using token authentication
     */
    @PostMapping("/authenticate")
    public String deleteDirectDebitWithToken(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the ben tokeeen {}", token);
        Long directDebitId = Long.parseLong(webRequest.getParameter("id"));
        logger.info("this is the debit {}", directDebitId);
        if (StringUtils.isNotBlank(token) && directDebitId != 0) {
            try {
                RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
                if (result) {
                    try {
                        String message = directDebitService.deleteDirectDebit(directDebitId);
                        directDebitService.debitsPayments(directDebitService.getDirectDebit(directDebitId)).
                                stream().filter(Objects::nonNull).forEach(i->directDebitService.deletePayment(i.getId()));

                        redirectAttributes.addFlashAttribute("message", message);
                    } catch (InternetBankingException e) {
                        logger.error("Direct debit Error", e);
                        redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    }
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                }
                return "redirect:/retail/directdebit";
            } catch (InternetBankingException e) {
                logger.error("Direct debit Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/retail/directdebit";
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token and direct debit to delete ! ");
            return "redirect:/retail/directdebit";
        }
    }

    /**
     * for deleting a particular payment under a direct debit
     */
    @PostMapping("/payment")
    public String deletePayment(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the ben tokeeen {}", token);
        Long paymentID = Long.parseLong(webRequest.getParameter("id"));
        logger.info("this is the payment to be deleted  {}", paymentID);

        if (StringUtils.isNotBlank(token) && paymentID != 0) {
            DirectDebit directDebit = directDebitService.getPaymentsDirectDebit(paymentID);
            try {
                RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);
                if (result) {
                    try {
                        String message = directDebitService.deletePayment(paymentID);
                        redirectAttributes.addFlashAttribute("message", message);
                    } catch (InternetBankingException e) {
                        logger.error("Direct debit Error", e);
                        redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    }
                } else {
                    redirectAttributes.addFlashAttribute("failure", "Token Authentication Failed");
                }
                return "redirect:/retail/directdebit/payments/" + directDebit.getId();
            } catch (InternetBankingException e) {
                logger.error("Direct debit Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/retail/directdebit/payments/" + directDebit.getId();
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token and direct debit to delete ! ");
            return "redirect:/retail/directdebit";
        }
    }

    /**
     * retrieves all financial institutions
     */

    @ModelAttribute
    public void commonParams(Model model) {

        List<FinancialInstitutionDTO> sortedNames = financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL);
        sortedNames.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));

        model.addAttribute("localBanks", sortedNames);
        logger.info("bank details {}", sortedNames);

    }

    @GetMapping("/limit")
    @ResponseBody
    public String checkTransferLimit(TransferRequestDTO dto) {
        boolean limitExceeded = limitService.isAboveInternetBankingLimit(
                dto.getTransferType(),
                UserType.valueOf(dto.getRemarks()),
                dto.getCustomerAccountNumber(),
                dto.getAmount()

        );
        if (limitExceeded) {
        logger.info("limit exceeded");
            return "Internet Banking limit exceeded";
        }
        return "";
    }

}


