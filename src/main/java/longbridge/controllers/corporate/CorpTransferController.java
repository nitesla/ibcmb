package longbridge.controllers.corporate;


import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.exception.TransferRuleException;
import longbridge.models.*;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
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
    private ApplicationContext appContext;
    private TransferUtils transferUtils;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CorpTransferService corpTransferService;

    @Autowired
    public CorpTransferController(CorporateService corporateService, CorporateRepo corporateRepo, CorporateUserService corporateUserService, IntegrationService integrationService, CorpTransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, CorpLocalBeneficiaryService corpLocalBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService, SecurityService securityService, TransferUtils transferUtils) {
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
        this.transferUtils=transferUtils;
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
    String getBankAccountName(@PathVariable String accountNo) {

        return transferUtils.doIntraBankkNameLookup(accountNo);
    }


    @GetMapping("/{accountNo}/{bank}/nameEnquiry")
    public
    @ResponseBody
    String getInterBankAccountName(@PathVariable String accountNo, @PathVariable String bank) {

       return transferUtils.doInterBankNameLookup(bank,accountNo);

    }


    @PostMapping("/process")
    public String bankTransfer(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO corpTransferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {

        try {

            if (request.getSession().getAttribute("AUTH") != null) {
                String token = request.getParameter("token");
                CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                boolean ok = securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);

                if (!ok) {
                    model.addAttribute("failure", messages.getMessage("auth.token.failure", null, locale));
                    return "/corp/transfer/transferauth";
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
                        corpLocalBeneficiaryService.addCorpLocalBeneficiary(l);
                        request.getSession().removeAttribute("Lbeneficiary");
                        // model.addAttribute("beneficiary", l);
                    } catch (InternetBankingException de) {
                        logger.error("Error occurred processing transfer");

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

        } catch (InternetBankingTransferException ex) {
            ex.printStackTrace();

            String errorMessage = transferErrorService.getMessage(ex);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return index(request);
        }
        catch ( TransferRuleException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return index(request);
        }
        finally {
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
        }
    }

    @GetMapping("/newbeneficiaary")
    public String newbeneficiaary(HttpServletRequest request, Principal principal, RedirectAttributes attributes) throws Exception {
        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            corpLocalBeneficiaryService.addCorpLocalBeneficiary(l);
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

//        logger.info("Transfer requests ", requests.toString());
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
    public String getBalance(@PathVariable String accountNumber) throws Exception {

        return transferUtils.getBalance(accountNumber);
    }
    @RequestMapping(value = "/limit/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getLimit(@PathVariable String accountNumber) throws Exception {

        return transferUtils.getLimit(accountNumber);
    }


    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id, HttpServletRequest servletRequest, Principal principal) throws Exception {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_receipt.jrxml");
        view.setApplicationContext(appContext);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("datasource", new ArrayList<>());
        modelMap.put("amount", transferService.getTransfer(id).getAmount());
        modelMap.put("recipient", transferService.getTransfer(id).getBeneficiaryAccountName());
        modelMap.put("recipient",transferService.getTransfer(id).getBeneficiaryAccountName());
        modelMap.put("AccountNum", transferService.getTransfer(id).getCustomerAccountNumber());
        modelMap.put("sender", corporateUser.getFirstName() + " " + corporateUser.getLastName());
        modelMap.put("remarks", transferService.getTransfer(id).getRemarks());
        modelMap.put("remarks",transferService.getTransfer(id));
        modelMap.put("recipientBank", transferService.getTransfer(id).getFinancialInstitution().getInstitutionName());
        modelMap.put("acctNo2", transferService.getTransfer(id).getBeneficiaryAccountNumber());
        modelMap.put("acctNo1", transferService.getTransfer(id).getCustomerAccountNumber());
        modelMap.put("refNUm", transferService.getTransfer(id).getReferenceNumber());
        modelMap.put("date",DateFormatter.format(new Date()));
        modelMap.put("tranDate", DateFormatter.format(new Date()));
        ModelAndView modelAndView = new ModelAndView(view, modelMap);
        return modelAndView;
    }

}
