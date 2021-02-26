package longbridge.controllers.retail;


import longbridge.api.AccountDetails;
import longbridge.api.PaginationDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.GreetingDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.Code;
import longbridge.models.RetailUser;
import longbridge.servicerequests.client.RequestService;
import longbridge.servicerequests.config.RequestConfigService;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.JasperReport.ReportHelper;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionDetails;
import longbridge.utils.statement.TransactionHistory;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.OutputStream;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/account")
public class AccountController {

    @Autowired
    RequestService requestService;
    @Autowired
    RequestConfigService requestConfigService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AccountService accountService;
    @Autowired
    private RetailUserService retailUserService;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private SettingsService configurationService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CodeService codeService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private GreetingService greetingService;
    private final Locale locale = LocaleContextHolder.getLocale();

    private Long customizeAccountId;


    @Value("${report.logo.url}")
    private String imagePath;
    @Value("${account-statement.template.name}")
    private String accountStatementName;

    @GetMapping
    public String listAccounts() {
        return "cust/account/index";
    }

    @GetMapping("{id}/view")
    public String viewAccount(@PathVariable Long id, Model model) {
        // fetch account details from Account Service
        AccountDTO accountDTO = accountService.getAccount(id);
        AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());

        if (account == null) {
            // account not found
            return "redirect:/retail/account";
        }
        // send account to frontend
        model.addAttribute("account", account);
        return "cust/account/details";
    }

    @GetMapping("/customize")
    public String CustomizeAccountHome(Model model, Principal principal) {
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
        for (AccountDTO account : accounts) {

            Code code = codeService.getByTypeAndCode("ACCOUNT_CLASS", account.getSchemeType());
            if (code != null && code.getDescription() != null) {
                account.setSchemeType(code.getDescription());
            }
        }
        model.addAttribute("accounts", accounts);
        return "cust/account/customizehome";
    }

    @GetMapping("/{id}/customize")
    public String CustomizeAccount(@PathVariable Long id, CustomizeAccount customizeAccount, Principal principal,
                                   Model model, RedirectAttributes redirectAttributes) {

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        AccountDTO accountDTO = accountService.getAccount(id);
        if (!retailUser.getCustomerId().equals(accountDTO.getCustomerId())) {
            redirectAttributes.addFlashAttribute("message", "Access Denied");
            return "redirect:/retail/logout";
        }

        this.customizeAccountId = accountDTO.getId();
        model.addAttribute("account", accountDTO.getPreferredName());
        return "cust/account/customize";
    }

    @PostMapping("/customize")
    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result, Model model,
                               RedirectAttributes redirectAttributes, Locale locale) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("failure", "Name cannot be empty");
            return "cust/account/customize";
        }

        try {
            String message = accountService.customizeAccount(this.customizeAccountId,
                    customizeAccount.getPreferredName());
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/retail/account/customize";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal) {
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());

        for (AccountDTO account : accounts) {
            Code code = codeService.getByTypeAndCode("ACCOUNT_CLASS", account.getSchemeType());
            if (code != null && code.getDescription() != null) {
                account.setSchemeType(code.getDescription());
            }
        }
        model.addAttribute("accounts", accounts);
        return "cust/account/setting";
    }

    @GetMapping("/{id}/hide")
    public String hide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes,
                       Locale locale) {

        try {
            String message = accountService.hideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/retail/account/settings";
    }

    @GetMapping("/{id}/unhide")
    public String unhide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes,
                         Locale locale) {

        try {
            String message = accountService.unhideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/account/settings";
    }

    @GetMapping("/{id}/primary")
    public String makePrimary(@PathVariable Long id, Model model, Principal principal,
                              RedirectAttributes redirectAttributes, Locale locale) {

        try {
            RetailUser user = retailUserService.getUserByName(principal.getName());
            String message = accountService.makePrimaryAccount(id, user.getCustomerId());
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Account Primary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/retail/account/settings";
    }

    @GetMapping("/officer")
    public String getAccountOfficer() {
        return "cust/account/officer";
    }

    @GetMapping("/{id}/statement")
    public String getAccountHistory(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request) {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        AccountDTO account = accountService.getAccount(id);
        String LAST_TEN_TRANSACTION = "10";
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());
        request.getSession().setAttribute("tranAccountNo", account.getAccountNumber());
        List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(account.getAccountNumber(),
                LAST_TEN_TRANSACTION);
        if (transRequestList != null && !transRequestList.isEmpty()) {
            model.addAttribute("acct", id);
            model.addAttribute("transRequestList", transRequestList);
            model.addAttribute("accountList", accountList);
//			logger.info("Last 10 Transaction {}", transRequestList);
            return "cust/account/accountstatement";
        }
        return "redirect:/retail/dashboard";
    }

    @RequestMapping(path = "{id}/downloadhistory", method = RequestMethod.GET)
    public void getTransPDF(@PathVariable String id, Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        Account account = accountService.getAccountByCustomerId(retailUser.getCustomerId());

        logger.info("Retail account {}", account);
        String LAST_TEN_TRANSACTION = "10";
        String acct = request.getSession().getAttribute("tranAccountNo").toString();
        logger.info("Getting the session account no {}", acct);
        List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(acct, LAST_TEN_TRANSACTION);


        Map<String, Object> modelMap = new HashMap<>();
        for (TransactionHistory transactionHistory : transRequestList) {
            double amount = Double.parseDouble(transactionHistory.getBalance());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("sender", retailUser.getFirstName() + " " + retailUser.getLastName());
            modelMap.put("remarks", transactionHistory.getNarration());
            modelMap.put("recipientBank", "");
            modelMap.put("refNUm", transactionHistory.getTranType());
            modelMap.put("date", DateFormatter.format(transactionHistory.getValueDate()));
            modelMap.put("tranDate", DateFormatter.format(transactionHistory.getPostedDate()));
        }

        modelMap.put("imagePath", imagePath);

        JasperReport jasperReport = ReportHelper.getJasperReport("rpt_tran-hist");

        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"rpt_tran-hist.pdf\"");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(transRequestList));
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

    }

    @PostMapping("/history")
    public String getAccountHistory(Model model, Principal principal) {

        return "cust/account/history";
    }

    @GetMapping("/viewstatement")
    public String getViewOnly(Model model, Principal principal) throws ParseException {
        model.addAttribute("acctNum", null);
        return "cust/account/view";
    }


    @GetMapping("/viewstatement/{id}")
    public String getViewOnlyById(@PathVariable Long id, Model model, Principal principal) throws ParseException {
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccountsForDebitAndCredit(user.getCustomerId());
        AccountDTO accountDTO = accountService.getAccount(id);
        model.addAttribute("acctNum", accountDTO.getAccountNumber());
        model.addAttribute("accounts", accounts);
        return "cust/account/view";
    }

    @GetMapping("/{acct}/viewonlyhistory")
    public String getViewOnlyHist(@PathVariable String acct, Model model, Principal principal) throws ParseException {
        model.addAttribute("accountNumber", acct);
        SettingDTO setting = configurationService.getSettingByName("TRANS_HISTORY_RANGE");
        Date date = new Date();
        Date daysAgo = new DateTime(date).minusDays(Integer.parseInt(setting.getValue())).toDate();
//		logger.info("the from date {} and the to date {}",date,daysAgo);
        AccountDTO account = accountService.getAccount(Long.parseLong(acct));
        AccountStatement accountStatement = integrationService.getFullAccountStatement(account.getAccountNumber(), daysAgo, date, "B");
        List<TransactionDetails> list = accountStatement.getTransactionDetails();
        Collections.reverse(list);
//		logger.info("The List {} ", list.get(0));
        model.addAttribute("history", list);
        return "cust/account/tranhistory";
    }


    @GetMapping("/viewstatement/display/data")
    @ResponseBody
    public Map<String, Object> getStatementData(WebRequest webRequest, HttpSession session) {
        // Pageable pageable = DataTablesUtils.getPageable(input);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("details", null);
        objectMap.put("moreData", "");
        String acctNumber = webRequest.getParameter("acctNumber");
        String fromDate = webRequest.getParameter("fromDate");
        String toDate = webRequest.getParameter("toDate");
        String tranType = webRequest.getParameter("tranType");
        logger.info("fromDate {}", fromDate);
        logger.info("toDate {}", toDate);
        List<TransactionDetails> list = null;
        Date from;
        Date to;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            from = format.parse(fromDate);
            to = format.parse(toDate);
            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType, "5");
            session.removeAttribute("hasMoreTransaction");
            if (session.getAttribute("retAcctStmtStateValue") != null) {
                Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
                for (int i = 0; i <= stateValue; i++) {
                    session.removeAttribute("acctStmtEntirePastDetails" + stateValue);
                }
            }

            session.removeAttribute("retAcctStmtStateValue");

            if (accountStatement != null) {
                list = accountStatement.getTransactionDetails();
                session.setAttribute("hasMoreTransaction", accountStatement.getHasMoreData());
//				logger.info("the current has more data  new {}",accountStatement.getHasMoreData());

                session.removeAttribute("acctStmtLastDetails");
                if (list != null && !list.isEmpty()) {
//				logger.info("the current has more data  empty {}",accountStatement.getHasMoreData());
                    session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
                    session.setAttribute("retAcctStmtStateValue", 0);
                    session.setAttribute("acctStmtEntirePastDetails0", list);
                    objectMap.replace("details", list);
                    objectMap.replace("moreData", accountStatement.getHasMoreData());
                }
            }

        } catch (ParseException e) {
            logger.warn("didn't parse date", e);
        }
        return objectMap;

    }

    @GetMapping("/viewstatement/display/data/next")
    @ResponseBody
    public Map<String, Object> getStatementDataByState(WebRequest webRequest, HttpSession session) {
        // Pageable pageable = DataTablesUtils.getPageable(input);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("details", null);
        objectMap.put("moreData", "");
        String acctNumber = webRequest.getParameter("acctNumber");
        String fromDate = webRequest.getParameter("fromDate");
        String toDate = webRequest.getParameter("toDate");
        String tranType = webRequest.getParameter("tranType");
        String state = webRequest.getParameter("state");
        List<TransactionDetails> list;
        Date from;
        Date to;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            from = format.parse(fromDate);
            to = format.parse(toDate);
            TransactionDetails transactionDetails = null;
            if ((session.getAttribute("acctStmtLastDetails") != null) && ("forward".equalsIgnoreCase(state))) {
                transactionDetails = (TransactionDetails) session.getAttribute("acctStmtLastDetails");
            }
            if (transactionDetails != null) {
                PaginationDetails paginationDetails = new PaginationDetails();
                paginationDetails.setLastAccountBalance(transactionDetails.getAccountBalance());
                paginationDetails.setLastAccountCurrency(transactionDetails.getCurrencyCode());
                paginationDetails.setLastPostDate(transactionDetails.getPostDate());
                paginationDetails.setLastTranId(transactionDetails.getTranId());
                paginationDetails.setLastTranDate(transactionDetails.getTranDate());
                paginationDetails.setLastTranSN(transactionDetails.getTranSN());
                AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType, "5", paginationDetails);
                list = accountStatement.getTransactionDetails();
                session.removeAttribute("acctStmtLastDetails");

                session.removeAttribute("hasMoreTransaction");
                if (accountStatement != null) {
                    session.setAttribute("hasMoreTransaction", accountStatement.getHasMoreData());
                    logger.info("the current has more data {}", accountStatement.getHasMoreData());
                }
                if (!list.isEmpty()) {
                    session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
                    Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
                    stateValue = stateValue + 1;
                    session.removeAttribute("acctStmtEntirePastDetails" + stateValue);
                    session.removeAttribute("retAcctStmtStateValue");
                    session.setAttribute("acctStmtEntirePastDetails" + stateValue, list);
                    session.setAttribute("retAcctStmtStateValue", stateValue);
//					logger.info("acctStmtLastDetails {}", list.get(list.size() - 1));
//					logger.info("acct statemnet state {}", stateValue);
                    objectMap.replace("details", list);
                    objectMap.replace("moreData", accountStatement.getHasMoreData());
                }
            }

        } catch (ParseException e) {
            logger.warn("didn't parse date {}", e);
        } catch (Exception e) {
            logger.warn("error cause by {}", e.getMessage());
            throw e;
        }
        return objectMap;

    }

    @GetMapping("/viewstatement/display/data/back")
    @ResponseBody
    public Map<String, Object> getStatementDataForBack(WebRequest webRequest, HttpSession session) {
        String state = webRequest.getParameter("state");
//	logger.info("the state {}",state);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("details", null);
        objectMap.put("previousData", "");
        List<TransactionDetails> list;
        if (("backward".equalsIgnoreCase(state))) {
            Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
            stateValue -= 1;
            if (session.getAttribute("acctStmtEntirePastDetails" + stateValue) != null) {
                session.removeAttribute("retAcctStmtStateValue");
                session.setAttribute("retAcctStmtStateValue", stateValue);
//					logger.info("the state value back {}",stateValue);
                list = (List<TransactionDetails>) session.getAttribute("acctStmtEntirePastDetails" + stateValue);
                session.removeAttribute("acctStmtLastDetails");
                session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
                session.setAttribute("hasMoreTransaction", "Y");
//					logger.info("acctStmtLastDetails  last record previous {}", list.get(list.size() - 1));
                objectMap.replace("details", list);
                objectMap.replace("previousData", stateValue);
            }
        }

        return objectMap;

    }


    @GetMapping("/downloadstatement/{format}")
    public void downloadStatementData(@PathVariable("format") String format,ModelMap modelMap, DataTablesInput input, String acctNumber,
                                      String fromDate, String toDate, String tranType, HttpServletResponse response) throws Exception {
        Date from;
        Date to;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        logger.info("from date {} to date {} type {}", fromDate, toDate, tranType);
        from = sdf.parse(fromDate);
        to = sdf.parse(toDate);
        AccountStatement accountStatement = integrationService.getFullAccountStatement(acctNumber, from, to, tranType);
        List<TransactionDetails> list = accountStatement.getTransactionDetails();
        if (!list.isEmpty()) {
//				logger.info("statemet list is {}", list);
        } else {
            logger.info("statement list is empty");
        }
//			RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        Account account = accountService.getAccountByAccountNumber(acctNumber);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        modelMap.put("datasource", list);
        modelMap.put("format", "pdf");
        modelMap.put("summary.accountNum", acctNumber);
        modelMap.put("summary.customerName", account.getAccountName());
        modelMap.put("summary.customerNo", account.getCustomerId());

        double amount = Double.parseDouble(accountStatement.getOpeningBalance());
        modelMap.put("summary.openingBalance", formatter.format(amount));
        // the total debit and credit is referred as total debit count and credit count
        if (accountStatement.getDebitCount() != null) {
            modelMap.put("summary.debitCount", accountStatement.getDebitCount());
        } else {
            modelMap.put("summary.debitCount", "");
        }
        if (accountStatement.getCreditCount() != null) {
            modelMap.put("summary.creditCount", accountStatement.getCreditCount());
        } else {
            modelMap.put("summary.creditCount", "");
        }
        modelMap.put("summary.currencyCode", accountStatement.getCurrencyCode());
        if (accountStatement.getClosingBalance() != null) {
            double closingbal = Double.parseDouble(accountStatement.getClosingBalance());

            modelMap.put("summary.closingBalance", formatter.format(closingbal));
        } else {
            modelMap.put("summary.closingBalance", "");
        }

        // the total debit and credit is referred as total debit count and credit count
        if (accountStatement.getTotalDebit() != null) {
            double totalDebit = Double.parseDouble(accountStatement.getTotalDebit());
            modelMap.put("summary.totalDebit", formatter.format(totalDebit));
        } else {
            modelMap.put("summary.totalDebit", "");
            logger.info("total debit is empty");
        }
        if (accountStatement.getTotalCredit() != null) {
            double totalCredit = Double.parseDouble(accountStatement.getTotalCredit());
            modelMap.put("summary.totalCredit", formatter.format(totalCredit));
        } else {
            modelMap.put("summary.totalCredit", "");
            logger.info("total Credit is empty");
        }
        if (accountStatement.getAddress() != null) {
            modelMap.put("summary.address", accountStatement.getAddress());
        } else {
            modelMap.put("summary.address", "");
        }
        modelMap.put("fromDate", fromDate);
        modelMap.put("toDate", toDate);
        Date today = new Date();
        modelMap.put("today", today);
        modelMap.put("imagePath", imagePath);
        //stat
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(list);
        JasperPrint jasperPrint = null;
        JasperReport jasperReport = ReportHelper.getJasperReport(accountStatementName);
        OutputStream out = response.getOutputStream();
        jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, beanColDataSource);

        if (jasperPrint != null) {
            logger.info("generating statement ... {}", format);
            if ("PDF".equalsIgnoreCase(format)) {
                response.setContentType("application/x-downloadPdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"statement.pdf\"");
                JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            } else if ("EXCEL".equalsIgnoreCase(format)) {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=\"statement.xlsx\"");
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
                exporter.exportReport();
            } else if ("CSV".equalsIgnoreCase(format)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"statement.csv\"");
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(out));
                exporter.exportReport();
            }
        } else {
            logger.warn("unsupported report format {}", format);
            throw new InternetBankingException("unsupported report format " + format);
        }
//

    }



    @GetMapping("/validate/session")
    public @ResponseBody
    String getUserSession(Principal principal) {
        logger.info("session check");
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication ();
        // CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) {
            return "invalid";
        } else {
            logger.trace("user session valid");
            return "valid";
        }
    }

    @GetMapping("/greetings/current")
    @ResponseBody
    public List<GreetingDTO> getTodayGreetings() {

        List<GreetingDTO> specialGreetings = null;
        try {
            specialGreetings = greetingService.getCurrentGreetingsForUser();

        } catch (Exception e) {
            logger.info("special Greetings error {}", e);
        }
        return specialGreetings;
    }

    @GetMapping("/new")
    public String createNewAccount(Model model) {

        Iterable<CodeDTO> accountType = codeService.getCodesByType("ACCOUNT_CLASS");
        model.addAttribute("requestConfig", requestConfigService.getRequestConfigByName("CREATE-ACCOUNT"));
        model.addAttribute("accountType", accountType);
        model.addAttribute("TandC", messageSource.getMessage("account.new.terms.conditions", null, locale));

        return "cust/account/createNewAccount";
    }

}

