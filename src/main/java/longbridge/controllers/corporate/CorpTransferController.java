package longbridge.controllers.corporate;


import longbridge.api.NEnquiryDetails;
import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.models.*;


import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.TransferType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

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

    @Autowired
    CorpTransferRequestRepo transferRequestRepo;
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

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CorpTransferService corpTransferService;

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

        CorpTransferRequestDTO dto = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
        if (dto != null) {
            request.getSession().removeAttribute("corpTransferRequest");
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
                        logger.error("Error occured processing transfer");

                    }
                }
            }


            corpTransferRequestDTO = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
            String corporateId = "" + corporateUserService.getUserByName(principal.getName()).getCorporate().getId();
            corpTransferRequestDTO.setCorporateId(corporateId);
            String response = transferService.addTransferRequest(corpTransferRequestDTO);


            model.addAttribute("transRequest", corpTransferRequestDTO);
            model.addAttribute("message", response);
            return "corp/transfer/transferdetails";

        } catch (InternetBankingTransferException | TransferRuleException e) {
            e.printStackTrace();
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
            String errorMessage = transferErrorService.getExactMessage(e.getMessage());
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return index(request);
        }
    }

    @GetMapping("/newbeneficiaary")
    public String newbeneficiaary(HttpServletRequest request, Principal principal, RedirectAttributes attributes) throws Exception {
        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            corpLocalBeneficiaryService.addCorpLocalBeneficiary(user.getCorporate(), l);
        }

        attributes.addFlashAttribute("message", "New Beneficiary Added");
        return "redirect:/corporate/dashboard";

    }


    @GetMapping("/{id}/authorizations")
    public String getAuthorizations(@PathVariable Long id, Model model) {

        CorpTransRequest corpTransRequest = corpTransferService.getTransfer(id);
        CorpTransferAuth corpTransferAuth = corpTransferService.getAuthorizations(corpTransRequest);
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(corpTransRequest);
        boolean userCanAuthorize = corpTransferService.userCanAuthorize(corpTransRequest);
        model.addAttribute("authorizationMap", corpTransferAuth);
        model.addAttribute("corpTransRequest", corpTransRequest);
        model.addAttribute("corpTransReqEntry", new CorpTransReqEntry());
        model.addAttribute("corpTransRule", corpTransRule);
        model.addAttribute("userCanAuthorize", userCanAuthorize);

        List<CorporateRole> rolesNotInAuthList = new ArrayList<>();
        List<CorporateRole> rolesInAuth = new ArrayList<>();

        if(corpTransferAuth.getAuths()!=null) {
            for (CorpTransReqEntry transReqEntry : corpTransferAuth.getAuths()) {
                rolesInAuth.add(transReqEntry.getRole());
            }
        }

            if(corpTransRule!=null) {
                for (CorporateRole role : corpTransRule.getRoles()) {
                    if (!rolesInAuth.contains(role)) {
                        rolesNotInAuthList.add(role);
                    }
                }
            }
        logger.info("Roles not In Auth List..{}", rolesNotInAuthList.toString());
        model.addAttribute("rolesNotAuth", rolesNotInAuthList);

        return "corp/transfer/request/summary";
    }

    @GetMapping("/requests")
    public String getTransfers() {
        return "corp/transfer/request/view";
    }


    @GetMapping("/requests/all")
    public
    @ResponseBody
    DataTablesOutput<CorpTransRequest> getTransferRequests(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpTransRequest> requests = corpTransferService.getTransfers(pageable);
        DataTablesOutput<CorpTransRequest> out = new DataTablesOutput<CorpTransRequest>();
        out.setDraw(input.getDraw());
        out.setData(requests.getContent());
        out.setRecordsFiltered(requests.getTotalElements());
        out.setRecordsTotal(requests.getTotalElements());
        return out;
    }


    @PostMapping("/authorize")
    public String addAuthorization(@ModelAttribute("corpTransReqEntry") CorpTransReqEntry corpTransReqEntry, RedirectAttributes redirectAttributes) {

        try {
            String message = corpTransferService.addAuthorization(corpTransReqEntry);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            logger.error("Failed to authorize transfer", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/corporate/transfer/requests";

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
