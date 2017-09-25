package longbridge.controllers.corporate;


import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/22/2017.
 */

@Controller
@RequestMapping("/corporate/transfer")
public class CorpTransferController {

    @Value("${jrxmlImage.path}")
    private String imagePath;

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
    private TransferUtils transferUtils;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CorpTransferService corpTransferService;

    @Autowired
    private ConfigurationService configService;


    @Autowired
    private ApplicationContext appContext;

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
        this.transferUtils = transferUtils;
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

    @PostMapping("/dest/accounts")
    public
    @ResponseBody
    List<String> getDestinationAccounts(WebRequest webRequest) {

        String accountId = webRequest.getParameter("acctId");

        logger.info("the account id {}", accountId);

        try {
            List<String> accountList = new ArrayList<>();
            Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());
            SettingDTO dto = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
            StreamSupport.stream(accounts.spliterator(), true)
                    .filter(Objects::nonNull)
                    .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                    .filter(l -> l.getCurrencyCode().equalsIgnoreCase(accountService.getAccountByAccountNumber(accountId).getCurrencyCode()))
                    .filter(i->{
                        if (dto != null && dto.isEnabled()){
                            String[]   list = StringUtils.split(dto.getValue(), ",");
                            return  ArrayUtils.contains(list, i.getSchemeType());

                        }
                        return false;
                    } )
                    .forEach(i -> accountList.add(i.getAccountNumber()))
            ;

            logger.info("ACCOUNT LIST {}", StreamSupport.stream(accounts.spliterator(), true).count());
            logger.info("second LIST {}", accountList.size());
            return accountList;

        } catch (Exception e) {
            logger.error("transfer error {}", e);
        }

        return null;
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

      try {
          return accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
      }
      catch (Exception e){
          logger.error("Error getting currency", e);
      }
      return "";
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

        return transferUtils.doInterBankNameLookup(bank, accountNo);

    }


    @PostMapping("/process")
    public String bankTransfer(Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, Principal principal) throws Exception {
        CorpTransferRequestDTO transferRequestDTO = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
        model.addAttribute("corpTransferRequest", transferRequestDTO);
        try {

            if (request.getSession().getAttribute("auth-needed") != null) {
                String token = request.getParameter("token");
                if (token == null || token.isEmpty()) {
                    model.addAttribute("failure", "Token is required");
                    return "corp/transfer/transferauth";
                }


                try {
                    CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                    securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);

                } catch (InternetBankingSecurityException ibse) {
                    ibse.printStackTrace();
                    model.addAttribute("failure", ibse.getMessage());
                    return "corp/transfer/transferauth";
                }
                request.getSession().removeAttribute("auth-needed");


            }


            if (request.getSession().getAttribute("add") != null) {
                //checkbox  checked
                if (request.getSession().getAttribute("Lbeneficiary") != null) {
                    CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                    try {
                        corpLocalBeneficiaryService.addCorpLocalBeneficiary(l);
                        request.getSession().removeAttribute("Lbeneficiary");
                    } catch (InternetBankingException de) {
                        logger.error("Error occurred processing transfer");

                    } finally {

                    }
                }
            }

            CorpTransferRequestDTO corpTransferRequestDTO = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
            String corporateId = "" + corporateUserService.getUserByName(principal.getName()).getCorporate().getId();
            corpTransferRequestDTO.setCorporateId(corporateId);
            Object object = transferService.addTransferRequest(corpTransferRequestDTO);

            if(object instanceof CorpTransferRequestDTO){

                corpTransferRequestDTO = (CorpTransferRequestDTO)object;

                logger.info("transRequest {}", corpTransferRequestDTO);
                model.addAttribute("transRequest", corpTransferRequestDTO);
                model.addAttribute("message", corpTransferRequestDTO.getStatusDescription());
            }

            else if (object instanceof String){
                redirectAttributes.addFlashAttribute("message", object);
                return "redirect:/corporate/transfer/requests";

            }

            return "corp/transfer/transferdetails";



        } catch (TransferAuthorizationException ae) {
            logger.error("Error initiating a transfer ", ae);
            redirectAttributes.addFlashAttribute("failure", ae.getMessage());
            return index(request);
        } catch (InternetBankingTransferException ex) {

            logger.error("Error initiating a transfer ", ex);
            String errorMessage = transferErrorService.getMessage(ex);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return index(request);
        } catch (TransferRuleException e) {
            logger.error("Error initiating a transfer ", e);
            String errorMessage = e.getMessage();
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return index(request);
        } finally {
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
    public String getAuthorizations(@PathVariable Long id, ModelMap modelMap) {

        CorpTransRequest corpTransRequest = corpTransferService.getTransfer(id);
        CorpTransferAuth corpTransferAuth = corpTransferService.getAuthorizations(corpTransRequest);
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(corpTransRequest);
        boolean userCanAuthorize = corpTransferService.userCanAuthorize(corpTransRequest);
        modelMap.addAttribute("authorizationMap", corpTransferAuth)
                .addAttribute("corpTransRequest", corpTransRequest)
                .addAttribute("corpTransReqEntry", new CorpTransReqEntry())
                .addAttribute("corpTransRule", corpTransRule)
                .addAttribute("userCanAuthorize", userCanAuthorize);

        List<CorporateRole> rolesNotInAuthList = new ArrayList<>();
        List<CorporateRole> rolesInAuth = new ArrayList<>();

        if (corpTransferAuth.getAuths() != null) {
            for (CorpTransReqEntry transReqEntry : corpTransferAuth.getAuths()) {
                rolesInAuth.add(transReqEntry.getRole());
            }
        }

        if (corpTransRule != null) {
            for (CorporateRole role : corpTransRule.getRoles()) {
                if (!rolesInAuth.contains(role)) {
                    rolesNotInAuthList.add(role);
                }
            }
        }
        logger.info("Roles not In Auth List..{}", rolesNotInAuthList.toString());
        modelMap.addAttribute("rolesNotAuth", rolesNotInAuthList);

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
        Page<CorpTransRequest> requests = corpTransferService.getTransferRequests(pageable);
        DataTablesOutput<CorpTransRequest> out = new DataTablesOutput<CorpTransRequest>();
        out.setDraw(input.getDraw());
        out.setData(requests.getContent());
        out.setRecordsFiltered(requests.getTotalElements());
        out.setRecordsTotal(requests.getTotalElements());
        return out;
    }


    @PostMapping("/authorize")
    public String addAuthorization(@ModelAttribute("corpTransReqEntry") CorpTransReqEntry corpTransReqEntry, @RequestParam("token") String tokenCode, RedirectAttributes redirectAttributes, Principal principal) {


        CorporateUser user = corporateUserService.getUserByName(principal.getName());

        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

        if(setting!=null&&setting.isEnabled()) {
            if (tokenCode != null && !tokenCode.isEmpty()) {
                try {
                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
                    if (!result) {
                        logger.error("Error authenticating token");
                        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
                        return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/authorizations";
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token");
                    redirectAttributes.addFlashAttribute("failure", se.getMessage());
                    return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/authorizations";
                }
            } else {
                redirectAttributes.addFlashAttribute("failure", "Token code is required");
                return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/authorizations";

            }
        }

        try {
            String message = corpTransferService.addAuthorization(corpTransReqEntry);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (TransferAuthorizationException te) {
            logger.error("Failed to authorize transfer", te);
            redirectAttributes.addFlashAttribute("failure", te.getMessage());
        } catch (InternetBankingTransferException te) {
            logger.error("Error making transfer", te);
            redirectAttributes.addFlashAttribute("failure", te.getMessage());
        } catch (InternetBankingException ibe) {
            logger.error("Failed to authorize transfer", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/corporate/transfer/requests";

    }


    @RequestMapping(value = "/balance/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getBalance(@PathVariable String accountNumber) throws Exception {


        try {
            return transferUtils.getBalance(accountNumber);
        }
        catch (Exception e){
            logger.error("Error getting balance",e);
        }
        return "";
    }

    @RequestMapping(value = "/limit/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getLimit(@PathVariable String accountNumber) throws Exception {

        try {
            return transferUtils.getLimit(accountNumber);
        }
        catch (Exception e){
            logger.error("Error getting limit",e);
        }
        return "";
    }

    //The receipt for multi corporate user
    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public ModelAndView report(@PathVariable Long id, HttpServletRequest servletRequest, Principal principal) throws
            Exception {
        //servletRequest.getSession().setAttribute("newId",id);
        try {
            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
            Corporate corporate = corporateUser.getCorporate();
            TransRequest transRequest = transferService.getTransfer(id);

//            logger.info("Trans Request {}", transRequest);
            JasperReportsPdfView view = new JasperReportsPdfView();
            view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
            view.setApplicationContext(appContext);

            Map<String, Object> modelMap = new HashMap<>();
            double amount = Double.parseDouble(transRequest.getAmount().toString());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("imagePath", imagePath);
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("customer", corporate.getName());
            modelMap.put("customerAcctNumber", transRequest.getCustomerAccountNumber());
            if(transRequest.getRemarks() != null) {
                modelMap.put("remarks", transRequest.getRemarks());
            }else{
                modelMap.put("remarks", "");
            }
            modelMap.put("beneficiary", transRequest.getBeneficiaryAccountName());
            modelMap.put("beneficiaryAcctNumber", transRequest.getBeneficiaryAccountNumber());
            modelMap.put("beneficiaryBank", transRequest.getFinancialInstitution().getInstitutionName());
            modelMap.put("refNUm", transRequest.getReferenceNumber());
            modelMap.put("tranDate", DateFormatter.format(transRequest.getTranDate()));
            modelMap.put("date", DateFormatter.format(new Date()));


            ModelAndView modelAndView = new ModelAndView(view, modelMap);
            return modelAndView;
        } catch (InternetBankingException e) {
            logger.info(" RECEIPT DOWNLOAD {} ", e.getMessage());
            ModelAndView modelAndView = new ModelAndView("redirect:/corporate/transfer/history");
            modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            //redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            return modelAndView;

        }
    }


    @GetMapping("/auth")
    public String authenticate(HttpServletRequest httpServletRequest, Model model) throws Exception {
        CorpTransferRequestDTO dto = (CorpTransferRequestDTO) httpServletRequest.getSession().getAttribute("corpTransferRequest");
        if (dto != null) model.addAttribute("corpTransferRequest", dto);
        return "corp/transfer/transferauth";
    }

    //Receipt for sole corporate user
    @RequestMapping(path = "/receipt", method = RequestMethod.GET)
    public ModelAndView getreport(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO
                                          corpTransferRequestDTO, Model model, HttpServletRequest servletRequest, Principal principal) throws
            Exception {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_receipt.jrxml");
        view.setApplicationContext(appContext);
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("datasource", new ArrayList<>());
        modelMap.put("amount", corpTransferRequestDTO.getAmount());
        modelMap.put("recipient", corpTransferRequestDTO.getBeneficiaryAccountName());
        modelMap.put("recipient", corpTransferRequestDTO.getBeneficiaryAccountName());
        modelMap.put("AccountNum", corpTransferRequestDTO.getCustomerAccountNumber());
        modelMap.put("sender", corporateUser.getFirstName() + " " + corporateUser.getLastName());
        modelMap.put("remarks", corpTransferRequestDTO.getRemarks());
        modelMap.put("recipientBank", corpTransferRequestDTO.getFinancialInstitution().getInstitutionName());
        modelMap.put("acctNo2", corpTransferRequestDTO.getBeneficiaryAccountNumber());
        modelMap.put("acctNo1", corpTransferRequestDTO.getCustomerAccountNumber());
        modelMap.put("refNUm", corpTransferRequestDTO.getReferenceNumber());
        modelMap.put("date", DateFormatter.format(new Date()));
        modelMap.put("tranDate", DateFormatter.format(new Date()));
        ModelAndView modelAndView = new ModelAndView(view, modelMap);
        return modelAndView;

    }

}
