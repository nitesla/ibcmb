package longbridge.controllers.retail;


import longbridge.dtos.LocalBeneficiaryDTO;
import longbridge.dtos.SettingDTO;
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
import longbridge.utils.JasperReport.ReportHelper;
import longbridge.utils.StringUtil;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.StreamSupport;


/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {


    private final RetailUserService retailUserService;
    private final TransferService transferService;
    private final AccountService accountService;
    private final MessageSource messages;
    private final LocalBeneficiaryService localBeneficiaryService;
    private final TransferErrorService transferErrorService;
    private final SecurityService securityService;
    private final TransferUtils transferUtils;
    @Autowired
    private ConfigurationService configService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localBeneficiaryService = localBeneficiaryService;
        this.transferErrorService = transferErrorService;
        this.securityService = securityService;
        this.transferUtils = transferUtils;
    }


    @GetMapping(value = "")
    public String index(HttpServletRequest request) {

        TransferRequestDTO dto = (TransferRequestDTO) request.getSession().getAttribute("transferRequest");

        if (dto != null) {
            request.getSession().removeAttribute("transferRequest");
            TransferType tranType = dto.getTransferType();
            switch (tranType) {
                case CORONATION_BANK_TRANSFER: {
                    return "redirect:/retail/transfer/local";
                }
                case INTER_BANK_TRANSFER: {
                    return "redirect:/retail/transfer/interbank";
                }
                case INTERNATIONAL_TRANSFER: {
                    return "redirect:/retail/transfer/international";

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

        logger.info("the account id {}", accountId);

        try {
            List<String> accountList = new ArrayList<>();
            Iterable<Account> accounts = accountService.getAccountsForCredit(accountService.getAccountByAccountNumber(accountId).getCustomerId());
            logger.info("Accountttt {}", accounts);
            SettingDTO dto = configService.getSettingByName("TRANSACTIONAL_ACCOUNTS");
            logger.info("DTO {}", dto);


            StreamSupport.stream(accounts.spliterator(), true)
                    .filter(Objects::nonNull)
                    .filter(i -> !i.getAccountNumber().equalsIgnoreCase(accountId))
                    .filter(l -> l.getCurrencyCode().equalsIgnoreCase(accountService.getAccountByAccountNumber(accountId).getCurrencyCode()))
                    .filter(i -> {
                        if (dto != null && dto.isEnabled()) {
                            logger.info("Here {}", dto.getValue());
                            String[] list = StringUtils.split(dto.getValue(), ",");
                            return ArrayUtils.contains(list, i.getSchemeType());

                        }
                        return false;
                    })
                    .forEach(i -> accountList.add(i.getAccountNumber()));


            logger.info("ACCOUNT LIST {}", StreamSupport.stream(accounts.spliterator(), true).count());
            logger.info("second LIST {}", accountList.size());
            return accountList;

        } catch (Exception e) {
            logger.error("transfer error {}", e);
            return null;
        }

//        return null;
    }


    @GetMapping("/{accountId}/currency")
    public
    @ResponseBody
    String getAccountCurrency(@PathVariable String accountId) {

        try {
            return accountService.getAccountByAccountNumber(accountId).getCurrencyCode();
        } catch (Exception e) {
            logger.error("Error getting currency");
        }
        return "";
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
            logger.error("Error getting bank account name", e);
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
                    model.addAttribute("failure", "Token is required");
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
            if (TransferType.INTERNATIONAL_TRANSFER.equals(transferRequestDTO.getTransferType())) {
                return "redirect:/retail/transfer/international/process";
            }


            if (request.getSession().getAttribute("add") != null) {
                //checkbox  checked
                if (request.getSession().getAttribute("Lbeneficiary") != null) {
                    LocalBeneficiaryDTO l = (LocalBeneficiaryDTO) request.getSession().getAttribute("Lbeneficiary");
                    RetailUser user = retailUserService.getUserByName(principal.getName());
                    try {
                        localBeneficiaryService.addLocalBeneficiary(l);
                        request.getSession().removeAttribute("Lbeneficiary");
                        request.getSession().removeAttribute("add");
                        // model.addAttribute("beneficiary", l);
                    } catch (InternetBankingException de) {
                        logger.error("Error adding beneficiary", de);

                    }
                }
            }


            transferRequestDTO = transferService.makeTransfer(transferRequestDTO);
            model.addAttribute("transRequest", transferRequestDTO);
            logger.info("Transfer status {}", transferRequestDTO.getStatus());
            if (transferRequestDTO.getStatus().equalsIgnoreCase("00") || transferRequestDTO.getStatus().equalsIgnoreCase("000")) {
                model.addAttribute("message", messages.getMessage(transferErrorService.getMessage(transferRequestDTO.getStatus()), null, locale));
                logger.info("Transfer status1 {}", transferRequestDTO.getStatus());

                return "cust/transfer/transferdetails";

            }
            if (transferRequestDTO.getStatus().equalsIgnoreCase("34")) {
//                model.addAttribute("failure", messages.getMessage("transaction.pending", null, locale));
                model.addAttribute("failure", messages.getMessage(transferErrorService.getMessage(transferRequestDTO.getStatus()), null, locale));
                logger.info("Transfer status..antifraud {}", transferRequestDTO.getStatus());

                return "cust/transfer/pendingtransferdetails";

            }
            if (transferRequestDTO.getStatus().equalsIgnoreCase("09")) {
                model.addAttribute("failure", messages.getMessage(transferErrorService.getMessage(transferRequestDTO.getStatus()), null, locale));
                logger.info("Transfer status..pending {}", transferRequestDTO.getStatus());
                return "cust/transfer/pendingtransferdetails";

            } else {
                logger.info("Transfer status..others {}", transferRequestDTO.getStatus());
                redirectAttributes.addFlashAttribute("failure", transferErrorService.getMessage(transferRequestDTO.getStatus()));//GB
                return index(request);
            }
//            return "cust/transfer/transferdetails";


        } catch (InternetBankingTransferException e) {
            logger.error("Error making transfer", e);
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
//            redirectAttributes.addFlashAttribute("failure", messages.getMessage("transfer.failed", null, locale));
            redirectAttributes.addFlashAttribute("failure", transferErrorService.getMessage(transferRequestDTO.getStatus()));
            return index(request);

        } catch (Exception e) {
            logger.error("Error making transfer", e);
            if (request.getSession().getAttribute("Lbeneficiary") != null)
                request.getSession().removeAttribute("Lbeneficiary");
            redirectAttributes.addFlashAttribute("failure", messages.getMessage("transfer.failed", null, locale));
            return index(request);
        }
    }


    @GetMapping("/auth")
    public String authenticate(HttpServletRequest httpServletRequest, Model model) throws Exception {
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
                localBeneficiaryService.addLocalBeneficiary(l);
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
    public void report(@PathVariable Long id, HttpServletRequest servletRequest, HttpServletResponse response, Principal principal) throws Exception {

            RetailUser retailUser = retailUserService.getUserByName(principal.getName());
            TransRequest transRequest = transferService.getTransfer(id);


            Map<String, Object> modelMap = new HashMap<>();
            double amount = Double.parseDouble(transRequest.getAmount().toString());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("imagePath", imagePath);
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("customer", retailUser.getFirstName() + " " + retailUser.getLastName());
            modelMap.put("customerAcctNumber", StringUtil.maskAccountNumber(transRequest.getCustomerAccountNumber()));
            if (transRequest.getRemarks() != null) {
                modelMap.put("remarks", transRequest.getRemarks());
            } else {
                modelMap.put("remarks", "");
            }
            modelMap.put("beneficiary", transRequest.getBeneficiaryAccountName());
            modelMap.put("beneficiaryAcctNumber", transRequest.getBeneficiaryAccountNumber());
//            modelMap.put("beneficiaryBank", transRequest.getFinancialInstitution().getInstitutionName());
            modelMap.put("beneficiaryBank", transRequest.getBeneficiaryBank());
            modelMap.put("refNUm", transRequest.getReferenceNumber());
            modelMap.put("statusDescription", transRequest.getStatusDescription());
            modelMap.put("tranDate", DateFormatter.format(transRequest.getTranDate()));
            modelMap.put("date", DateFormatter.format(new Date()));

            JasperReport jasperReport = ReportHelper.getJasperReport("rpt_tran-hist");

            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=\"rpt_tran-hist.pdf\"");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap);
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());


    }

    @RequestMapping(value = "/back", method = RequestMethod.POST)
    public
    @ResponseBody
    String testRedirection(HttpServletRequest request) {

        return getPreviousPageByRequest(request).orElse("/retail/dashboard"); //else go to home page
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

    @RequestMapping(value = "/balance/{accountNumber}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getBalance(@PathVariable String accountNumber) {

        try {
            return transferUtils.getBalance(accountNumber);
        } catch (Exception e) {
            logger.error("Error getting balance", e);
        }
        return "";
    }


}

