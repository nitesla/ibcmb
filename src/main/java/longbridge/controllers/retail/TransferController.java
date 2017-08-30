package longbridge.controllers.retail;


import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.models.TransRequest;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Stream;
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
    private LocalBeneficiaryService localBeneficiaryService;
    private TransferErrorService transferErrorService;
    private SecurityService securityService;
    private ApplicationContext appContext;
    private TransferUtils transferUtils;

//    @Autowired
//    TransferRe

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private MessageSource messageSource;

    @Value("${bank.code}")
    private String bankCode;
    @Value("${jrxmlImage.path}")
    private String imagePath;


    @Autowired
    public TransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, LocalBeneficiaryService localBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService, SecurityService securityService
            , ApplicationContext appContext, TransferUtils transferUtils) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localBeneficiaryService = localBeneficiaryService;
        this.transferErrorService = transferErrorService;
        this.securityService = securityService;
        this.appContext = appContext;
        this.transferUtils = transferUtils;

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



    @PostMapping("/dest/accounts")
    public
    @ResponseBody
    List<String> getDestinationAccounts(WebRequest webRequest) {

        String accountId = webRequest.getParameter("acctId");

logger.info("the account id {}",accountId);

        try {
            List<String> accountList = new ArrayList<>();
            Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());

            StreamSupport.stream(accounts.spliterator(), true)
                    .filter(Objects::nonNull)
                    .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                    .filter(l -> l.getCurrencyCode().equalsIgnoreCase(accountService.getAccountByAccountNumber(accountId).getCurrencyCode()))
                    .forEach(i -> accountList.add(i.getAccountNumber()))
            ;

            logger.info("ACCOUNT LIST {}", StreamSupport.stream(accounts.spliterator(),true).count());
            logger.info("second LIST {}", accountList.size());
            return accountList;

        } catch (Exception e) {
            logger.error("transfer error {}",e);
        }

        return null;
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


    @GetMapping("{accountNo}/nameEnquiry")
    public
    @ResponseBody
    String getBankAccountName(@PathVariable String accountNo, Principal principal) {

        try {
            if (principal != null) {

                return transferUtils.doIntraBankkNameLookup(accountNo);
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


        return transferUtils.doInterBankNameLookup(bank, accountNo);

    }

    @PostMapping("/process")
    public String bankTransfer(Model model, RedirectAttributes redirectAttributes, Locale locale, HttpServletRequest request, Principal principal) throws Exception {
        TransferRequestDTO transferRequestDTO = (TransferRequestDTO) request.getSession().getAttribute("transferRequest");
        model.addAttribute("transferRequest", transferRequestDTO);
        try {
            String type = (String) request.getSession().getAttribute("NIP");
            if (type != null) {
                request.getSession().removeAttribute("NIP");
            }

            if (request.getSession().getAttribute("auth-needed") != null) {

                String token = request.getParameter("token");
                if (token == null || token.isEmpty()) {

                    return "/cust/transfer/transferauth";
                }


                try {
                    RetailUser retailUser = retailUserService.getUserByName(principal.getName());
                    securityService.performTokenValidation(retailUser.getEntrustId(), retailUser.getEntrustGroup(), token);

                } catch (InternetBankingSecurityException ibse) {
                    ibse.printStackTrace();
                    model.addAttribute("failure", ibse.getMessage());
                    return "/cust/transfer/transferauth";
                }


                request.getSession().removeAttribute("auth-needed");


            }


            if (request.getSession().getAttribute("add") != null) {
                //checkbox  checked
                if (request.getSession().getAttribute("Lbeneficiary") != null) {
                    LocalBeneficiaryDTO l = (LocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                    RetailUser user = retailUserService.getUserByName(principal.getName());
                    try {
                        localBeneficiaryService.addLocalBeneficiary(user, l);
                        request.getSession().removeAttribute("Lbeneficiary");
                        request.getSession().removeAttribute("add");
                        // model.addAttribute("beneficiary", l);
                    } catch (InternetBankingException de) {
                        de.printStackTrace();

                    }
                }
            }




            transferRequestDTO = transferService.makeTransfer(transferRequestDTO);
            model.addAttribute("transRequest", transferRequestDTO);
            model.addAttribute("message", messages.getMessage("transaction.success", null, locale));
            return "cust/transfer/transferdetails";

        } catch (InternetBankingTransferException e) {
            e.printStackTrace();
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
            String errorMessage = transferErrorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return index(request);


        }
    }



    @GetMapping("/auth")
    public String authenticate(HttpServletRequest httpServletRequest,Model model) throws Exception {
        TransferRequestDTO dto = (TransferRequestDTO) httpServletRequest.getSession().getAttribute("transferRequest");
        if (dto != null) model.addAttribute("transferRequest", dto);
        return "/cust/transfer/transferauth";
    }

    @GetMapping("/newbeneficiary")
    public String newbeneficiaary(HttpServletRequest request, Locale locale, Principal
            principal, RedirectAttributes attributes) throws Exception {


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


    /**
     * Returns the viewName to return for coming back to the sender url
     *
     * @param request Instance of {@link HttpServletRequest} or use an injected instance
     * @return Optional with the view name. Recomended to use an alternativa url with
     * {@link Optional#orElse(java.lang.Object)}
     */
    protected Optional<String> getPreviousPageByRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Referer")).map(requestUrl -> "redirect:" + requestUrl);
    }

    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id, HttpServletRequest servletRequest, Principal principal) {

        try {
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            TransRequest transRequest = transferService.getTransfer(id);

            JasperReportsPdfView view = new JasperReportsPdfView();
            view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
            view.setApplicationContext(appContext);

            Map<String, Object> modelMap = new HashMap<>();
            double amount = Double.parseDouble(transRequest.getAmount().toString());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("imagePath", imagePath);
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("customer",retailUser.getFirstName()+" "+retailUser.getLastName() );
            modelMap.put("customerAcctNumber", transRequest.getCustomerAccountNumber());
            modelMap.put("remarks", transRequest.getRemarks());
            modelMap.put("beneficiary", transRequest.getBeneficiaryAccountName());
            modelMap.put("beneficiaryAcctNumber", transRequest.getBeneficiaryAccountNumber());
            modelMap.put("beneficiaryBank", transRequest.getFinancialInstitution().getInstitutionName());
            modelMap.put("refNUm", transRequest.getReferenceNumber());
            modelMap.put("tranDate", DateFormatter.format(transRequest.getTranDate()));
            modelMap.put("date", DateFormatter.format(new Date()));


            ModelAndView modelAndView=new ModelAndView(view, modelMap);
            return modelAndView;
        }catch (Exception e){
            logger.info(" RECEIPT DOWNLOAD {} ", e.getMessage());
            ModelAndView modelAndView =  new ModelAndView("redirect:/retail/transfer/history");
            modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            //redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            return modelAndView;
        }
    }

    @RequestMapping(value = "/back", method = RequestMethod.POST)
    public
    @ResponseBody
    String testRedirection(HttpServletRequest request) {

        return getPreviousPageByRequest(request).orElse("/retail/dashboard"); //else go to home page
    }

    @RequestMapping(value = "/limit/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getLimit(@PathVariable String accountNumber) throws Exception {

        return transferUtils.getLimit(accountNumber);
    }

    @RequestMapping(value = "/balance/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getBalance(@PathVariable String accountNumber) {

        return transferUtils.getBalance(accountNumber);

    }



}

