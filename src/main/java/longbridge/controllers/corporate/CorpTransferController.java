package longbridge.controllers.corporate;


import longbridge.api.NEnquiryDetails;
import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.*;
import longbridge.models.*;


import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.*;
import longbridge.utils.TransferType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/22/2017.
 */

@Controller
@RequestMapping("/corporate/transfer")
public class CorpTransferController {

    private CorporateService corporateService;
    private CorporateRepo corporateRepo;
    private CorporateUserService corporateUserService;
    private IntegrationService integrationService;
    private CorpTransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferErrorService transferErrorService;
    private SecurityService securityService;



    @Autowired
    public CorpTransferController(CorporateService corporateService, CorporateRepo corporateRepo, CorporateUserService corporateUserService, IntegrationService integrationService, CorpTransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService, SecurityService securityService) {
        this.corporateService = corporateService;
        this.corporateRepo = corporateRepo;
        this.corporateUserService = corporateUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.transferErrorService = transferErrorService;
        this.securityService = securityService;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    CorpTransferRequestRepo transferRequestRepo;
    @Autowired
    private CorpTransferService corpTransferService;

    @GetMapping("/{corpId}/{amount}")
    public void getQualifiedRoles(@PathVariable Long corpId, @PathVariable String amount) {

        CorpTransRequest transferRequest = new CorpTransRequest();
        transferRequest.setAmount(new BigDecimal(amount));
        Corporate corporate = corporateRepo.findOne(corpId);
        transferRequest.setCorporate(corporate);
        List<CorporateRole> roles = corporateService.getQualifiedRoles(transferRequest);
        PendAuth pendAuth = new PendAuth();
        List<PendAuth> pendAuths = new ArrayList<>();
        for (CorporateRole role : roles) {
            pendAuth.setRole(role);
            pendAuths.add(pendAuth);
        }
//        transferRequest.setPendAuths(pendAuths);
        transferRequestRepo.save(transferRequest);

    }

    @GetMapping(value = "")
    public String index(HttpServletRequest request) {

        CorpTransferRequestDTO dto = (CorpTransferRequestDTO) request.getSession().getAttribute("transferRequest");
        if (dto != null) {
            request.getSession().removeAttribute("transferRequest");
            TransferType tranType = dto.getTransferType();
            switch (tranType) {
                case CORONATION_BANK_TRANSFER:

                {
                    return "redirect:/corporate/transfer/local";
                }
                case INTER_BANK_TRANSFER: {
                    return "redirect:/corporate/transfer/interbank";
                }
                case INTERNATIONAL_TRANSFER: {

                }
                case NAPS: {

                }
                case OWN_ACCOUNT_TRANSFER: {

                    return "redirect:/corporate/transfer/ownaccount";
                }

                case RTGS: {
                    return "redirect:/corporate/transfer/interbank";
                }
            }
            return "redirect:/corporate/transfer/ownaccount";
        }
        return "redirect:/corporate/dashboard";
    }


    @GetMapping("/dest/{accountId}/accounts")
    public
    @ResponseBody
    List<String> getDestinationAccounts(@PathVariable String accountId) {


        List<String> accountList = new ArrayList<>();


        Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());

        StreamSupport.stream(accounts.spliterator(), false)
                .filter(Objects::nonNull)
                .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                .forEach(i -> accountList.add(i.getAccountNumber()))
        ;
        return accountList;


    }


    @GetMapping("/{accountId}/currency")
    public
    @ResponseBody
    String getAccountCurrency(@PathVariable String accountId) {
        return accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
    }


    @GetMapping("/local/{accountNo}/nameEnquiry")
    public
    @ResponseBody
    String getBankAccountName(@PathVariable String accountNo, Principal principal) {
        try {
            if (principal != null) {
                String name = integrationService.viewAccountDetails(accountNo).getAcctName();
                return name;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }


    @GetMapping("/{accountNo}/{bank}/nameEnquiry")
    public
    @ResponseBody
    String getInterBankAccountName(@PathVariable String accountNo, @PathVariable String bank, Principal principal) {

        if (principal != null) {
            NEnquiryDetails details = integrationService.doNameEnquiry(bank, accountNo);
            if (details == null)
                return createMessage("service down please try later", false);


            if (details.getResponseCode() != null && !details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getResponseDescription(), false);


            if (details.getAccountName() != null && details.getResponseCode() != null && details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getAccountName(), true);
        }


        return createMessage("session expired", false);


    }


    @PostMapping("/process")
    public String bankTransfer(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO corpTransferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {

        try {

            if (request.getSession().getAttribute("AUTH") != null) {
                String token = request.getParameter("token");
                boolean ok = securityService.performTokenValidation(principal.getName(), token);

                if (!ok) {
                    model.addAttribute("failure", messages.getMessage("auth.token.failure", null, locale));
                    return "/corp/transfer/corptransferauth";
                } else {
                    request.getSession().removeAttribute("AUTH");
                }


            }

            // request.getSession().removeAttribute("corpTransferRequest");


            if (request.getParameter("add") != null) {
                //checkbox  checked
                if (request.getSession().getAttribute("Lbeneficiary") != null) {
                    CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                    CorporateUser user = corporateUserService.getUserByName(principal.getName());
                    try {
                        Corporate corporate = corporateService.getCorporateByCustomerId(user.getCorporate().getCustomerId());
                        corpLocalBeneficiaryService.addCorpLocalBeneficiary(corporate, l);
                        request.getSession().removeAttribute("Lbeneficiary");
                        // model.addAttribute("beneficiary", l);
                    } catch (InternetBankingException de) {


                    }
                }
            }


            corpTransferRequestDTO = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");


            corpTransferRequestDTO = transferService.makeTransfer(corpTransferRequestDTO);


            model.addAttribute("transRequest", corpTransferRequestDTO);
            model.addAttribute("message", messages.getMessage("transaction.success", null, locale));
            return "corp/transfer/transferdetails";

        } catch (InternetBankingTransferException e) {
            e.printStackTrace();
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
            String errorMessage = transferErrorService.getExactMessage(e.getMessage());
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return index(request);


        }
    }

    @GetMapping("/newbeneficiaary")
    public String newbeneficiaary(HttpServletRequest request, Model model, Principal principal, RedirectAttributes attributes) throws Exception {
        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            corpLocalBeneficiaryService.addCorpLocalBeneficiary(user.getCorporate(), l);
        }


        attributes.addFlashAttribute("message", "New Beneficiary Added");
        return "redirect:/corporate/dashboard";

    }
    @GetMapping("/pending")
    public String getPendingTransfer(Principal principal,Model model){

//        List<PendAuth> pendAuths = corpTransferService.getPendingAuthorizations();
//        model.addAttribute("pendAuths", pendAuths);
        return "corp/transfer/pendingtransfer/view";
    }

    @GetMapping("/{id}/authorize")
    public String authorizeTransfer(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {
        try {
            String message = corpTransferService.authorizeTransfer(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InvalidAuthorizationException iae) {
            logger.error("Failed to authorize transfer", iae);
            redirectAttributes.addFlashAttribute("failure", iae.getMessage());

        } catch (TransferRuleException tre) {
            logger.error("Failed to authorize transfer", tre);
            redirectAttributes.addFlashAttribute("failure", tre.getMessage());

        } catch (InternetBankingException e) {
            logger.error("Failed to authorize transfer", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());

        }
        return "redirect:/corporate/pending";
    }


    @RequestMapping(value = "/balance/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public BigDecimal getBalance(@PathVariable String accountNumber) throws Exception {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        Map<String, BigDecimal> balance = accountService.getBalance(account);
        BigDecimal availBal = balance.get("AvailableBalance");
        return availBal;
    }

    private String createMessage(String message, boolean successOrFailure) {
        JSONObject object = new JSONObject();
        //ObjectNode object = Json.newObject();
        try {
            object.put("message", message);
            object.put("success", successOrFailure);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
