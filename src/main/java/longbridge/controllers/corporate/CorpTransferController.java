package longbridge.controllers.corporate;


import longbridge.dtos.CorpLocalBeneficiaryDTO;
import longbridge.dtos.CorpNeftBeneficiaryDTO;
import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.repositories.CorpTransferRequestRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.services.*;
import longbridge.utils.*;
import longbridge.utils.JasperReport.ReportHelper;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.StreamSupport;

//import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

/**
 * Created by Fortune on 5/22/2017.
 */

@Controller
@RequestMapping("/corporate/transfer")
public class CorpTransferController {

    @Autowired
    CorpTransferRequestRepo transferRequestRepo;
    @Value("${jrxmlImage.path}")
    private String imagePath;
    private final CorporateService corporateService;
    private final CorporateUserService corporateUserService;
    private final CorpTransferService transferService;
    private final AccountService accountService;
    private final CorpLocalBeneficiaryService corpLocalBeneficiaryService;
    private final CorpQuickBeneficiaryService corpQuickBeneficiaryService;
    private final TransferErrorService transferErrorService;
    private final SecurityService securityService;
    private final TransferUtils transferUtils;
    private final CorpNeftBeneficiaryService corpNeftBeneficiaryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Locale locale = LocaleContextHolder.getLocale();
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CorpTransferService corpTransferService;

    @Autowired
    private ConfigurationService configService;


    @Autowired
    private ApplicationContext appContext;

    @Autowired
    public CorpTransferController(CorporateService corporateService, CorporateRepo corporateRepo, CorporateUserService corporateUserService, IntegrationService integrationService, CorpTransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver, CorpLocalBeneficiaryService corpLocalBeneficiaryService, CorpQuickBeneficiaryService corpQuickBeneficiaryService, FinancialInstitutionService financialInstitutionService, TransferErrorService transferErrorService, SecurityService securityService, TransferUtils transferUtils, CorpNeftBeneficiaryService corpNeftBeneficiaryService) {
        this.corporateService = corporateService;
        this.corporateUserService = corporateUserService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.corpLocalBeneficiaryService = corpLocalBeneficiaryService;
        this.transferErrorService = transferErrorService;
        this.securityService = securityService;
        this.transferUtils = transferUtils;
        this.corpQuickBeneficiaryService = corpQuickBeneficiaryService;
        this.corpNeftBeneficiaryService = corpNeftBeneficiaryService;
    }


    @GetMapping(value = "")
    public String index(HttpServletRequest request) {

        CorpTransferRequestDTO dto = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
        if (dto != null) {
            request.getSession().removeAttribute("corpTransferRequest");
            TransferType tranType = dto.getTransferType();
            switch (tranType) {
                case CORONATION_BANK_TRANSFER: {
                    return "redirect:/corporate/transfer/local";
                }
                case INTER_BANK_TRANSFER: {
                    return "redirect:/corporate/transfer/interbank";
                }
                case INTERNATIONAL_TRANSFER: {
                    return "redirect:/corporate/transfer/international";

                }
                case NAPS: {

                }
                case OWN_ACCOUNT_TRANSFER: {

                    return "redirect:/corporate/transfer/ownaccount";
                }
                case NEFT:{
                    return "redirect:/corporate/transfer/interbank";
                }

                case RTGS: {
                    return "redirect:/corporate/transfer/interbank";
                }

                case NEFT_BULK: {
                    return "redirect:/corporate/transfer/bulk/index";
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
                    .filter(i -> {
                        if (dto != null && dto.isEnabled()) {
                            String[] list = StringUtils.split(dto.getValue(), ",");
                            return ArrayUtils.contains(list, i.getSchemeType());

                        }
                        return false;
                    })
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
            Account account = accountService.getAccountByAccountNumber(accountId);
            if (account != null) {
                return account.getCurrencyCode();
            }
        } catch (Exception e) {
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

    @GetMapping("/{accountNo}/{bank}/nameEnquiryNeft")
    public
    @ResponseBody
    String getNeftBankAccountName(@PathVariable String accountNo, @PathVariable String bank) {
        return transferUtils.doNEFTBankNameLookup(bank, accountNo);
    }



    @GetMapping("/{accountNo}/{bank}/nameEnquiry")
    public
    @ResponseBody
    String getInterBankAccountName(@PathVariable String accountNo, @PathVariable String bank) {

        return transferUtils.doInterBankNameLookup(bank, accountNo);

    }

    @GetMapping("/{accountNo}/{bank}/nameEnquiryQuickteller")
    public
    @ResponseBody
    String getQuicktellerAccountName(@PathVariable String accountNo, @PathVariable String bank) {

        return transferUtils.doQuicktellerNameLookup(bank, accountNo);

    }


    @PostMapping("/process")
    public String bankTransfer(Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, Principal principal) throws Exception {
        CorpTransferRequestDTO transferRequestDTO = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
        model.addAttribute("corpTransferRequest", transferRequestDTO);
        try {
            transferUtils.validateTransferCriteria();

            if (request.getSession().getAttribute("auth-needed") != null) {
                String token = request.getParameter("token");
                if (token == null || token.isEmpty()) {
                    model.addAttribute("failure", "Token is required");
                    return "corp/transfer/transferauth";
                }


                try {
                    CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
                    securityService.performTokenValidation(corporateUser.getEntrustId(), corporateUser.getEntrustGroup(), token);
                    transferRequestDTO.setPayerName(corporateUser.getFirstName() + " " + corporateUser.getLastName());
                } catch (InternetBankingSecurityException ibse) {
                    ibse.printStackTrace();
                    model.addAttribute("failure", ibse.getMessage());
                    return "corp/transfer/transferauth";
                }
                request.getSession().removeAttribute("auth-needed");
            }
            if (TransferType.INTERNATIONAL_TRANSFER.equals(transferRequestDTO.getTransferType())) {
                return "redirect:/corporate/transfer/international/process";
            }

            if (request.getSession().getAttribute("add") != null) {
                if (TransferType.QUICKTELLER.equals(transferRequestDTO.getTransferType())) {

                    if (request.getSession().getAttribute("Lbeneficiary") != null) {
                        CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                        try {
                            corpQuickBeneficiaryService.addCorpQuickBeneficiary(l);
                            request.getSession().removeAttribute("Lbeneficiary");
                        } catch (InternetBankingException de) {
                            logger.error("Error occurred processing transfer");

                        }
                    }


                } else if (TransferType.NEFT.equals(transferRequestDTO.getTransferType()) || TransferType.NEFT_BULK.equals(transferRequestDTO.getTransferType())) {
                    if (request.getSession().getAttribute("Nbeneficiary") != null) {
                        CorpNeftBeneficiaryDTO l = (CorpNeftBeneficiaryDTO) request.getSession().getAttribute("Nbeneficiary");
                        try {
                            logger.info("Saving beneficiary now ======= {} ", l);
                            corpNeftBeneficiaryService.addCorpNeftBeneficiary(l);
                            request.getSession().removeAttribute("Nbeneficiary");
                            request.getSession().removeAttribute("add");
                        } catch (InternetBankingException de) {
                            logger.error("Error occurred processing transfer");
                        }
                    }
                } else {
                    //checkbox  checked
                    if (request.getSession().getAttribute("Lbeneficiary") != null) {
                        CorpLocalBeneficiaryDTO l = (CorpLocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                        try {
                            corpLocalBeneficiaryService.addCorpLocalBeneficiary(l);
                            request.getSession().removeAttribute("Lbeneficiary");
                        } catch (InternetBankingException de) {
                            logger.error("Error occurred processing transfer");

                        }
                    }
                }
            }

            CorpTransferRequestDTO corpTransferRequestDTO = (CorpTransferRequestDTO) request.getSession().getAttribute("corpTransferRequest");
            String corporateId = "" + corporateUserService.getUserByName(principal.getName()).getCorporate().getId();
            corpTransferRequestDTO.setCorporateId(corporateId);

            if (corpTransferRequestDTO.getTransferType() == TransferType.NEFT_BULK) {

            corpTransferRequestDTO = transferService.makeNeftBulkTransfer(corpTransferRequestDTO);

                if (corpTransferRequestDTO.getStatus().equalsIgnoreCase("PENDING")) {
//                model.addAttribute("failure", messages.getMessage("transaction.pending", null, locale));
                    model.addAttribute("message", messageSource.getMessage(transferErrorService.getMessage(corpTransferRequestDTO.getStatus()), null, locale));
                    logger.info("NEFT Transfer Status{}", transferRequestDTO.getStatus());

                    return "corp/transfer/bulktransfer/neft/pendingNeftTransfer";

                }

            }else {
                Object object = transferService.addTransferRequest(corpTransferRequestDTO);
                if (object instanceof CorpTransferRequestDTO) {

                    corpTransferRequestDTO = (CorpTransferRequestDTO) object;

                    logger.info("Transfer Request processed", corpTransferRequestDTO);
                    model.addAttribute("transRequest", corpTransferRequestDTO);
                    model.addAttribute("message", corpTransferRequestDTO.getStatusDescription());
                } else if (object instanceof String) {
                    model.addAttribute("transRequest", corpTransferRequestDTO);
                    redirectAttributes.addFlashAttribute("message", object);
                    redirectAttributes.addFlashAttribute("refNumber", corpTransferRequestDTO.getReferenceNumber());
                    redirectAttributes.addFlashAttribute("transferType", corpTransferRequestDTO.getTransferType());

                    return "redirect:/corporate/transfer/requests";

                }
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
        } catch (Exception e) {
            logger.error("Error initiating a transfer ", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("transfer.error", null, locale));
            return index(request);
        } finally {
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
            if (request.getSession().getAttribute("Nbeneficiary") != null)
                request.getSession().removeAttribute("Nbeneficiary");
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

//    @GetMapping("/test/authorizations")
//    public String getAuthorizationPage(){
//        return "corp/transfer/request/Test";
//    }

    @GetMapping("/{id}/authorizations")
    public String getAuthorizations(@PathVariable Long id, ModelMap modelMap) {
        CorpTransRequest corpTransRequest = corpTransferService.getTransfer(id);
        CorpTransferAuth corpTransferAuth = corpTransferService.getAuthorizations(corpTransRequest);
        CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(corpTransRequest);
        boolean userCanAuthorize = corpTransferService.userCanAuthorize(corpTransRequest);
        modelMap.addAttribute("authorizationMap", corpTransferAuth)
                .addAttribute("corpTransRequest", corpTransferService.entityToDTO(corpTransRequest))
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
    DataTablesOutput<CorpTransferRequestDTO> getTransferRequests(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CorpTransferRequestDTO> requests = corpTransferService.getTransferRequest(pageable);
        logger.info("REQUESTS ============================ {}", requests);
        DataTablesOutput<CorpTransferRequestDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(requests.getContent());
        out.setRecordsFiltered(requests.getTotalElements());
        out.setRecordsTotal(requests.getTotalElements());
        return out;
    }


    @PostMapping("/authorize")
    public String addAuthorization(WebRequest webRequest, @ModelAttribute("corpTransReqEntry") CorpTransReqEntry corpTransReqEntry, @RequestParam("token") String tokenCode, RedirectAttributes redirectAttributes, Principal principal) {

        corpTransReqEntry.setChannel("web");
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        String refNumber = webRequest.getParameter("tranReqRef");
        String transferType = webRequest.getParameter("tranReqType");

        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

        if (setting != null && setting.isEnabled()) {
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
            logger.info("corpmessage {}", message);
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("refNumber", refNumber);
            redirectAttributes.addFlashAttribute("transferType", transferType);


        } catch (InternetBankingTransferException te) {
            logger.error("Error making transfer", te);
            redirectAttributes.addFlashAttribute("failure", te.getMessage());
        } catch (InternetBankingException te) {
            logger.error("Failed to authorize transfer", te);
            redirectAttributes.addFlashAttribute("failure", te.getMessage());
        }
        return "redirect:/corporate/transfer/requests";

    }


    @RequestMapping(value = "/balance/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getBalance(@PathVariable String accountNumber) throws Exception {


        try {
            return transferUtils.getBalance(accountNumber);
        } catch (Exception e) {
            logger.error("Error getting balance", e);
        }
        return "";
    }

    @RequestMapping(value = "/limit/{accountNumber}/{channel}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getLimit(@PathVariable String accountNumber, @PathVariable String channel) throws Exception {

        try {
            return transferUtils.getLimit(accountNumber, channel);
        } catch (Exception e) {
            logger.error("Error getting limit", e);
        }
        return "";
    }

    //The receipt for multi corporate user
    @RequestMapping(path = "{id}/receipt", method = RequestMethod.GET)
    public void report(@PathVariable Long id, HttpServletRequest servletRequest, HttpServletResponse response, Principal principal) throws
            Exception {
        //servletRequest.getSession().setAttribute("newId",id);

            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
            Corporate corporate = corporateUser.getCorporate();
            TransRequest transRequest = transferService.getTransfer(id);

            logger.info("Trans Request for customer");

            Map<String, Object> modelMap = new HashMap<>();
            double amount = Double.parseDouble(transRequest.getAmount().toString());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("imagePath", imagePath);
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("customer", corporate.getName());
            modelMap.put("customerAcctNumber", StringUtil.maskAccountNumber(transRequest.getCustomerAccountNumber()));
            if (transRequest.getRemarks() != null) {
                modelMap.put("remarks", transRequest.getRemarks());
            } else {
                modelMap.put("remarks", "");
            }
            modelMap.put("beneficiary", transRequest.getBeneficiaryAccountName());
            modelMap.put("beneficiaryAcctNumber", transRequest.getBeneficiaryAccountNumber());

        if (transRequest.getBeneficiaryBank() != null) {
            modelMap.put("beneficiaryBank", transRequest.getBeneficiaryBank());
        }else{
            modelMap.put("beneficiaryBank", transRequest.getFinancialInstitution().getInstitutionName());
        }

            modelMap.put("refNUm", transRequest.getReferenceNumber());
            modelMap.put("tranDate", DateFormatter.format(transRequest.getTranDate()));
            modelMap.put("date", DateFormatter.format(new Date()));

        JasperReport jasperReport = ReportHelper.getJasperReport("rpt_tran-hist");

            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=\"rpt_tran-hist.pdf\"");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap);
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

    }


    @GetMapping("/auth")
    public String authenticate(HttpServletRequest httpServletRequest, HttpServletResponse response, Model model) throws Exception {
        CorpTransferRequestDTO dto = (CorpTransferRequestDTO) httpServletRequest.getSession().getAttribute("corpTransferRequest");
        if (dto != null) model.addAttribute("corpTransferRequest", dto);
        return "corp/transfer/transferauth";
    }

    //Receipt for sole corporate user
    @RequestMapping(path = "/receipt", method = RequestMethod.GET)
    public void getreport(@ModelAttribute("corpTransferRequest") @Valid CorpTransferRequestDTO
                                          corpTransferRequestDTO, Model model, HttpServletResponse response, Principal principal) throws
            Exception {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("datasource", new ArrayList<>());
        modelMap.put("amount", corpTransferRequestDTO.getAmount());
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
        modelMap.put("statusDescription", corpTransferRequestDTO.getStatusDescription());

        JasperReport jasperReport = ReportHelper.getJasperReport("rpt_receipt");

        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"receipt.pdf\"");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

    }

}
