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


    private  ApplicationContext appContext;


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
                        logger.error("Error occured processing transfer");

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
    public String newbeneficiaary(HttpServletRequest request, Principal principal, RedirectAttributes attributes) throws Exception {
        if (request.getSession().getAttribute("Lbeneficiary") != null) {
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
            corpLocalBeneficiaryService.addCorpLocalBeneficiary(user.getCorporate(), l);
        }

        attributes.addFlashAttribute("message", "New Beneficiary Added");
        return "redirect:/corporate/dashboard";

    }


    @GetMapping("/{id}/pending")
    public String getPendingAuth(@PathVariable Long id,  Model model){

        CorpTransRequest corpTransRequest = corpTransferService.getTransfer(id);
        CorpTransferAuth corpTransferAuth = corpTransferService.getAuthorizations(corpTransRequest);
        model.addAttribute("transferAuth",corpTransferAuth);
        model.addAttribute("transferRequest",corpTransRequest);
        return "corp/transfer/pendingtransfer/view";
    }

    @GetMapping("/requests")
    public String getTransfers(){
        return "";
    }


    @GetMapping("/requests/all")
    public @ResponseBody
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
    public String addAuthorization(@ModelAttribute CorpTransReqEntry corpTransReqEntry, CorpTransRequest corpTransRequest, RedirectAttributes redirectAttributes){

        try {
            String message = corpTransferService.addAuthorization(corpTransReqEntry,corpTransRequest);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            logger.error("Failed to authorize transfer", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

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

    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id, HttpServletRequest servletRequest, Principal principal) {
        CorporateUser corporateUser=corporateUserService.getUserByName(principal.getName());
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_receipt.jrxml");
        view.setApplicationContext(appContext);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("datasource",new ArrayList<>());
        modelMap.put("amount",transferService.getTransfer(id).getAmount());
        modelMap.put("recipient",transferService.getTransfer(id).getBeneficiaryAccountName());
        modelMap.put("AccountNum", transferService.getTransfer(id).getCustomerAccountNumber());
        modelMap.put("sender",corporateUser.getFirstName()+" "+corporateUser.getLastName() );
        modelMap.put("remarks", transferService.getTransfer(id).getRemarks());
        modelMap.put("recipientBank", transferService.getTransfer(id).getFinancialInstitution().getInstitutionName());
        modelMap.put("acctNo2", transferService.getTransfer(id).getBeneficiaryAccountNumber());
        modelMap.put("acctNo1", transferService.getTransfer(id).getCustomerAccountNumber());
        modelMap.put("refNUm", transferService.getTransfer(id).getReferenceNumber());
        modelMap.put("date", DateFormatter.format(transferService.getTransfer(id).getTranDate()));
        modelMap.put("tranDate", DateFormatter.format(transferService.getTransfer(id).getTranDate()));
        ModelAndView modelAndView=new ModelAndView(view, modelMap);
        return modelAndView;
    }

}
