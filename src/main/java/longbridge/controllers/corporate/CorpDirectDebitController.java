package longbridge.controllers.corporate;

import longbridge.dtos.*;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.*;
import longbridge.repositories.CorpDirectDebitRepo;
import longbridge.repositories.CorpLocalBeneficiaryRepo;
import longbridge.services.*;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
 * Created by mac on 23/02/2018.
 */

@Controller
@RequestMapping("/corporate/directdebit")
public class CorpDirectDebitController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private MessageSource messageSource;

    @Autowired
    private DirectDebitService directDebitService;
    @Autowired
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;

    @Autowired
    private FinancialInstitutionService financialInstitutionService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private CorporateUserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    CorpDirectDebitRepo directDebitRepo ;

    @Autowired
    CorpLocalBeneficiaryRepo corpLocalBeneficiaryRepo ;

    @Autowired
    private CodeService codeService;

    @Autowired
    ConfigurationService configurationService;

    @Autowired
    private TransferUtils transferUtils;

    @Autowired
    private TransactionLimitService limitService;

    private final Locale locale = LocaleContextHolder.getLocale();



    private final String page = "corp/directdebit/";
    @Value("${bank.code}")
    private String bankCode;


   @GetMapping()
   public String getDirectDebitForUser(){
       return "corp/directdebit/view";
   }

    @GetMapping("/all")
    @ResponseBody
    public DataTablesOutput<DirectDebitDTO> getDirectDebits(DataTablesInput input, Principal principal) {


        Pageable pageable = DataTablesUtils.getPageable(input);
        CorporateUser corporateUser = userService.getUserByName(principal.getName());
        Page<DirectDebitDTO> page=null;
        try {
             page = directDebitService.getCorpUserDirectDebitDTOS(corporateUser, pageable);
        }catch(NullPointerException e){
            logger.info("There are no direct debits for customer");
        }
        for (DirectDebitDTO dto : page) {
            try {
                FinancialInstitution fi = financialInstitutionService.getFinancialInstitutionByCode(dto.getCorpLocalBeneficiary().getBeneficiaryBank());
                if (null != dto.getCorpLocalBeneficiary())
                    dto.getCorpLocalBeneficiary().setBeneficiaryBank(fi.getInstitutionName());
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

    @GetMapping("/payments/{dirId}")
    public String getPayments(Model model, @PathVariable Long dirId) {
        CorpDirectDebit directDebit = directDebitRepo.findOneById(dirId);
        Collection<PaymentDTO> payments = directDebitService.getPayments(directDebit);
        model.addAttribute("payments", payments);
        return "corp/directdebit/payments";
    }

    @GetMapping("/ben/new")
    public String newBeneficiary(Model model, CorpLocalBeneficiaryDTO localBeneficiaryDTO) throws Exception {

        model.addAttribute("localBeneficiaryDTO", localBeneficiaryDTO);
        return page + "pageiB";
    }

    @GetMapping(value = "/index")
    public String viewDebitBeneficiaries(HttpServletRequest request, Model model, Principal principal,RedirectAttributes redirectAttributes) {

        if(principal == null){
            return "redirect:/corporate/logout";
        }
        try {
            transferUtils.validateTransferCriteria();
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

                requestDTO.setTransferType(TransferType.INTER_BANK_TRANSFER);

                model.addAttribute("corpTransferRequest", requestDTO);
                return page + "pageiA";
            } else {
                request.getSession().setAttribute("NIP", "RTGS");

                requestDTO.setTransferType(TransferType.RTGS);

                model.addAttribute("corpTransferRequest", requestDTO);
                return page + "pageiAb";
            }
        }catch(InternetBankingTransferException e){
            redirectAttributes.addFlashAttribute("failure",messageSource.getMessage("transfer.initiate.disallowed",null,locale));
            return "redirect:/corporate/directdebit";

        }

    }

    @GetMapping("/{id}/new")
    public String addDirectDebit(@PathVariable Long id ,Model model, Principal principal, DirectDebitDTO directDebitDTO) {
        CorporateUser user = userService.getUserByName(principal.getName());

        List<CorpLocalBeneficiary> bens = new ArrayList<>();
        CorpLocalBeneficiary corpLocalBeneficiary =  corpLocalBeneficiaryService.getCorpLocalBeneficiary(id);
        List<Account> accounts = user.getCorporate().getAccounts();
        Iterable<Account>accountsForDebit=accountService.getAccountsForDebit(accounts);
        bens.add(corpLocalBeneficiary);
        model.addAttribute("beneficiaries", bens);
        model.addAttribute("accounts", accountsForDebit);
        model.addAttribute("directDebit", directDebitDTO);
        model.addAttribute("benefBank", corpLocalBeneficiary.getBeneficiaryBank());
        model.addAttribute("durations",codeService.getCodesByType("STANDING_ORDER_FREQ"));
        return "corp/directdebit/add";
    }

    @PostMapping("/ben/new")
    public String createBeneficiary(@ModelAttribute("localBeneficiaryDTO") @Valid CorpLocalBeneficiaryDTO localBeneficiaryDTO , HttpSession session , BindingResult result){
        if(result.hasErrors()){
            return page + "pageiAb";
        }

        if (session.getAttribute("authenticated") == null) {
            session.setAttribute("requestDTO", localBeneficiaryDTO);
            session.setAttribute("redirectURL", "/corporate/directdebit/ben/create");
            return "redirect:/corporate/token/authenticate";
        }

        return "redirect:/corporate/directdebit";

    }

    @GetMapping("/ben/create")
    public String createBeneficaryTokenAuthenticated(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale){
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        logger.info("about to create beneficiary");
        if (session.getAttribute("requestDTO") != null) {
            logger.info("found requestDTO");
            CorpLocalBeneficiaryDTO localBeneficiaryDTO = (CorpLocalBeneficiaryDTO) session.getAttribute("requestDTO");
            if (session.getAttribute("authenticated") != null) {
                logger.info("request has been authenticated !");
                try {
                    String message2 = corpLocalBeneficiaryService.addCorpLocalBeneficiary( localBeneficiaryDTO);
                    redirectAttributes.addFlashAttribute("message", message2);
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    CorpLocalBeneficiary beneficiary = corpLocalBeneficiaryRepo.findByCorporate_IdAndAccountNumber(user.getCorporate().getId() , localBeneficiaryDTO.getAccountNumber());
                    logger.info("beficary id {}", beneficiary.getId());

                    return "redirect:/corporate/directdebit/"+  beneficiary.getId() + "/new";

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

        return "redirect:/corporate/directdebit/ben/new";

    }


    @PostMapping("/summary")
    public String getDirectDebitSummary(@ModelAttribute("directDebit") @Valid  DirectDebitDTO directDebitDTO, Principal principal, BindingResult result, HttpSession session, RedirectAttributes redirectAttributes, Model model,Locale locale){
        if(result.hasErrors()){
            return "corp/directdebit/add";
        }
        logger.info("the beneficiary id {}",directDebitDTO.getBeneficiary());
        logger.info("the beneficiary id {}",directDebitDTO);
        model.addAttribute("directDebit",directDebitDTO);
        return "corp/directdebit/summary";
    }




    @PostMapping
    public String createDirectDebit(@ModelAttribute("directDebit") @Valid DirectDebitDTO directDebitDTO, Principal principal, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            return "corp/directdebit/add";
        }

        logger.info("direct debit request  {}", directDebitDTO);
        logger.info("auth {}", session.getAttribute("authenticated"));
        SettingDTO setting = configurationService.getSettingByName("ENABLE_CORPORATE_2FA");
        logger.info("setg {}",setting);
        if (setting != null && setting.isEnabled()) {
            session.removeAttribute("requestDTO");
            session.removeAttribute("redirectURL");
            session.setAttribute("requestDTO", directDebitDTO);
            session.setAttribute("redirectURL", "/corporate/directdebit/process");
            return "redirect:/corporate/token/authenticate";
        }


      /*  if (session.getAttribute("authenticated") == null) {
            session.setAttribute("requestDTO", directDebitDTO);
            session.setAttribute("redirectURL", "/corporate/directdebit/process");
            return "redirect:/corporate/token/authenticate";
        }*/

        return "redirect:/corporate/directdebit";
    }

    @GetMapping("/process")
    public String processRequest(Principal principal, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale) {


        CorporateUser user = userService.getUserByName(principal.getName());

        if (session.getAttribute("requestDTO") != null) {
            DirectDebitDTO directDebitDTO = (DirectDebitDTO) session.getAttribute("requestDTO");

            logger.info("direct debit is {} ",directDebitDTO.toString());

            if (session.getAttribute("authenticated") != null) {

                try {
                    String message = directDebitService.addCorpDirectDebit(user, directDebitDTO);
                    model.addAttribute("success", "Direct Debit added successfully");
                    session.removeAttribute("authenticated");
                    session.removeAttribute("requestDTO");
                    redirectAttributes.addFlashAttribute("message", message);
                } catch (InternetBankingException e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", e.getMessage());
                    return "redirect:/corporate/directdebit";
                } catch (Exception e) {
                    logger.error("Direct debit Error", e);
                    redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("req.add.failure", null, locale));
                    return "redirect:/corporate/directdebit";
                }
            }

        }
        return "redirect:/corporate/directdebit";
    }


    @GetMapping("/{directDebitId}/delete")
    public String deleteDirectDebit(@PathVariable Long directDebitId, Model model) {
        directDebitService.deleteDirectDebit(directDebitId);
        model.addAttribute("success", "Direct Debit deleted successfully");
        return "redirect:/corporate/directdebit";
    }


    /**
     * For Direct debit deletion

     */
    @PostMapping("/authenticate")
    public String deleteDirectDebitWithToken(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the ben tokeeen {}", token);
        Long directDebitId = Long.parseLong(webRequest.getParameter("id"));
        logger.info("this is the debit {}", directDebitId);
        if (StringUtils.isNotBlank(token) && directDebitId != 0) {
            try {
                CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
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
                return "redirect:/corporate/directdebit";
            } catch (InternetBankingException e) {
                logger.error("Direct debit Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/corporate/directdebit";
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token and direct debit to delete ! ");
            return "redirect:/corporate/directdebit";
        }
    }

    @PostMapping("/payment")
    public String deletePayment(WebRequest webRequest, Principal principal, RedirectAttributes redirectAttributes) {
        String token = webRequest.getParameter("token");
        logger.info("this is the ben tokeeen {}", token);
        Long paymentID = Long.parseLong(webRequest.getParameter("id"));
        logger.info("this is the payment to be deleted  {}", paymentID);

        if (StringUtils.isNotBlank(token) && paymentID != 0) {
            DirectDebit directDebit = directDebitService.getPaymentsDirectDebit(paymentID);
            try {
                CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                boolean result = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
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
                return "redirect:/corporate/directdebit/payments/" + directDebit.getId();
            } catch (InternetBankingException e) {
                logger.error("Direct debit Error", e);
                redirectAttributes.addFlashAttribute("failure", e.getMessage());
                return "redirect:/corporate/directdebit/payments/" + directDebit.getId();
            }
        } else {
            redirectAttributes.addFlashAttribute("failure", "Kindly provide token and direct debit to delete ! ");
            return "redirect:/corporate/directdebit";
        }
    }


    @ModelAttribute
    public void commonParams(Model model) {

        List<FinancialInstitutionDTO> sortedNames = financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL);
        sortedNames.sort(Comparator.comparing(FinancialInstitutionDTO::getInstitutionName));

        model.addAttribute("localBanks"
                , sortedNames


        );
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
            System.out.println(limitExceeded);
            logger.info("limit exceeded");
            return "Internet Banking limit exceeded";
        }


        return "";

    }


}
