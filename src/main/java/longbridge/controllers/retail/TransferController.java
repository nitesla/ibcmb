package longbridge.controllers.retail;


import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.FinancialInstitutionType;
import longbridge.models.RetailUser;
import longbridge.repositories.RetailUserRepo;
import longbridge.services.*;
import longbridge.utils.ResultType;
import longbridge.utils.TransferType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {


    private RetailUserService retailUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private MessageSource messages;
    private LocaleResolver localeResolver;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;
    private TransferErrorService transferErrorService;
    private SecurityService securityService;
    private ApplicationContext appContext;

    @Value("${bank.code}")
    private String bankCode;


    @Autowired

    public TransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService, SecurityService securityService
    ,ApplicationContext appContext) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService = localBeneficiaryService;
        this.financialInstitutionService = financialInstitutionService;
        this.transferErrorService = transferErrorService;
        this.securityService = securityService;
        this.appContext=appContext;

    }


    @GetMapping(value = "")
    public String index(HttpServletRequest request) {

        TransferRequestDTO dto = (TransferRequestDTO) request.getSession().getAttribute("transferRequest");

        if (dto != null) {
            request.getSession().removeAttribute("transferRequest");
            TransferType tranType = dto.getTransferType();
            switch (tranType) {
                case CORONATION_BANK_TRANSFER:

                {
                    return "redirect:/retail/transfer/local";
                }
                case INTER_BANK_TRANSFER: {
                    return "redirect:/retail/transfer/interbank";
                }
                case INTERNATIONAL_TRANSFER: {

                }
                case NAPS: {

                }
                case OWN_ACCOUNT_TRANSFER: {

                    return "redirect:/retail/transfer/ownaccount";
                }

                case RTGS: {
                    return "redirect:/retail/transfer/interbank";
                }
            }
            return "redirect:/retail/transfer/ownaccount";

        }


        return "redirect:/retail/dashboard";

    }


    @GetMapping("/dest/{accountId}/accounts")
    public
    @ResponseBody
    List<String> getDestinationAccounts(@PathVariable String accountId) {


        List<String> accountList = new ArrayList<>();

        try {
            Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                    .forEach(i -> accountList.add(i.getAccountNumber()))
            ;

        } catch (Exception e) {

        }


        return accountList;


    }


    @GetMapping("/{accountId}/currency")
    public
    @ResponseBody
    String getAccountCurrency(@PathVariable String accountId) {

        try {
            return accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
        } catch (Exception e) {
            return "";
        }

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
            if (details == null )
                return createMessage("service down please try later", false);



            if (details.getResponseCode() != null  &&! details.getResponseCode().equalsIgnoreCase("00"))
                return createMessage(details.getResponseDescription(), false);




           if (details.getAccountName() != null && details.getResponseCode() != null && details.getResponseCode().equalsIgnoreCase("00"))
            return createMessage(details.getAccountName(), true);
        }


        return createMessage("session expired", false);


    }


    @PostMapping("/process")
    public String bankTransfer(@ModelAttribute("transferRequest") @Valid TransferRequestDTO transferRequestDTO, Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {

        try {

            if (request.getSession().getAttribute("auth-needed") != null) {
                String token = request.getParameter("token");

                boolean ok = false;
                try {
                    ok = securityService.performTokenValidation(principal.getName(), token);
                } catch (InternetBankingSecurityException ibse) {

                    model.addAttribute("failure", ibse.getMessage());
                    return "/cust/transfer/transferauth";
                }


                request.getSession().removeAttribute("auth-needed");


            }

            if (request.getParameter("add") != null) {
                //checkbox  checked
                if (request.getSession().getAttribute("Lbeneficiary") != null) {
                    LocalBeneficiaryDTO l = (LocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                    RetailUser user = retailUserService.getUserByName(principal.getName());
                    try {
                        localBeneficiaryService.addLocalBeneficiary(user, l);
                        request.getSession().removeAttribute("Lbeneficiary");
                        // model.addAttribute("beneficiary", l);
                    } catch (InternetBankingException de) {


                    }
                }
            }


            transferRequestDTO = (TransferRequestDTO) request.getSession().getAttribute("transferRequest");


            transferRequestDTO=   transferService.makeTransfer(transferRequestDTO);


            model.addAttribute("transRequest", transferRequestDTO);
            model.addAttribute("message", messages.getMessage("transaction.success", null, locale));
            return "cust/transfer/transferdetails";

        } catch (InternetBankingTransferException e) {
            e.printStackTrace();
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
            String errorMessage = transferErrorService.getMessage(e, request);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/retail/dashboard";


        }
    }

    @GetMapping("/newbeneficiary")
    public String newbeneficiaary(HttpServletRequest request, Locale locale, Principal principal, RedirectAttributes attributes) throws Exception {


        try {

            if (request.getSession().getAttribute("Lbeneficiary") != null) {
                RetailUser user = retailUserService.getUserByName(principal.getName());
                LocalBeneficiaryDTO l = (LocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                localBeneficiaryService.addLocalBeneficiary(user, l);
                request.getSession().removeAttribute("Lbeneficiary");
            }


            attributes.addFlashAttribute("message", "New Beneficiary Added");

        } catch (InternetBankingException e) {

            if (e.getMessage().equalsIgnoreCase("beneficiary.exist")) {

                attributes.addFlashAttribute("failure", messages.getMessage("beneficiary.exist", null, locale));
            } else {
                messages.getMessage("beneficiary.add.failure", null, locale);
                attributes.addFlashAttribute("failure", e.getMessage());
            }


        }


        return "redirect:/retail/dashboard";
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

    /**
     * Returns the viewName to return for coming back to the sender url
     *
     * @param request Instance of {@link HttpServletRequest} or use an injected instance
     * @return Optional with the view name. Recomended to use an alternativa url with
     * {@link Optional#orElse(java.lang.Object)}
     */
    protected Optional<String> getPreviousPageByRequest(HttpServletRequest request)
    {
        return Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
    }

    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id, HttpServletRequest servletRequest, TransferRequestDTO transferRequestDTO) {
        /**
         * Created a stub to test transaction receiptpt
         */
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_receipt.jrxml");
        view.setApplicationContext(appContext);

        Map<String, Object> modelMap = new HashMap<>();

        modelMap.put("datasource",new ArrayList<>());
//        modelMap.put("format", "pdf");
        modelMap.put("amount",transferService.getTransfer(id).getAmount());
        modelMap.put("recipient",transferService.getTransfer(id).getBeneficiaryAccountName());
        modelMap.put("AccountNum", transferService.getTransfer(id).getCustomerAccountNumber());
        modelMap.put("sender", "");
        modelMap.put("bank", transferService.getTransfer(id).getFinancialInstitution().getInstitutionName());
        modelMap.put("remarks", transferService.getTransfer(id).getRemarks());
        modelMap.put("recipientBank", "");
        modelMap.put("acctNo2", transferService.getTransfer(id).getCustomerAccountNumber());
        modelMap.put("acctNo1", transferService.getTransfer(id).getBeneficiaryAccountNumber());
        modelMap.put("refNUm", transferService.getTransfer(id).getReferenceNumber());
        modelMap.put("date", transferService.getTransfer(id).getTranDate());
        modelMap.put("tranDate",transferService.getTransfer(id).getTranDate());
        ModelAndView modelAndView=new ModelAndView(view, modelMap);
        return modelAndView;
//        logger.info("Transaction Receipt {}",modelMap);
//
//        ModelAndView modelAndView = new ModelAndView("rpt_receipt", modelMap);
//        return modelAndView;
    }

    @RequestMapping(value = "/back", method = RequestMethod.POST)
    public @ResponseBody
    String testRedirection(HttpServletRequest request)
    {

        return getPreviousPageByRequest(request).orElse("/retail/dashboard"); //else go to home page
    }


}

