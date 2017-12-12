package longbridge.controllers.corporate;

import longbridge.api.AccountDetails;
import longbridge.api.PaginationDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.Code;
import longbridge.models.CorporateUser;
import longbridge.repositories.AccountRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionDetails;
import longbridge.utils.statement.TransactionHistory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SYLVESTER on 4/3/2017.
 */
@Controller
@RequestMapping("/corporate/account")
public class CorpAccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TransferService transferService;

    @Autowired
    AccountRepo accountRepo;
    @Autowired
    CodeService codeService;

    @Autowired
    private ApplicationContext appContext;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Long customizeAccountId;
    private Locale locale = LocaleContextHolder.getLocale();
    @Value("${jrxmlImage.path}")
    private String imagePath;

    @GetMapping
    public String listAccounts() {
        return "corp/account/index";
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");

    @GetMapping("{id}/view")
    public String viewAccount(@PathVariable Long id, Model model) {
        //fetch account details from Account Service
        AccountDTO accountDTO = accountService.getAccount(id);
        AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());

        if (account == null) {
            return "redirect:/corporate/account";
        }
        //send account to frontend
        model.addAttribute("account", account);
        return "corp/account/details";

    }


    @GetMapping("/customize")
    public String CustomizeAccountHome(Model model, Principal principal) {
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<Account> accounts = accountService.filterUnrestrictedAccounts(user.getCorporate().getAccounts());

        for (Account account : accounts) {
            Code code = codeService.getByTypeAndCode("ACCOUNT_CLASS", account.getSchemeType());
            if (code != null && code.getDescription() != null) {
                account.setSchemeType(code.getDescription());
            }
        }
        model.addAttribute("accounts", accounts);
        return "corp/account/customizehome";
    }

    @GetMapping("/{id}/customize")
    public String CustomizeAccount(@PathVariable Long id, CustomizeAccount customizeAccount, Principal principal, Model model, RedirectAttributes redirectAttributes) {

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        AccountDTO accountDTO = accountService.getAccount(id);
        if (!corporateUser.getCorporate().getCustomerId().equals(accountDTO.getCustomerId())) {
            redirectAttributes.addFlashAttribute("message", "Access Denied");
            return "redirect:/corporate/logout";
        }

        this.customizeAccountId = accountDTO.getId();
        model.addAttribute("account", accountDTO.getPreferredName());
        return "corp/account/customize";
    }

    @PostMapping("/customize")
    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result, RedirectAttributes redirectAttributes, Model model) throws Exception {
        if (result.hasErrors()) {

            model.addAttribute("message", "Name field cannot be empty");
            return "corp/account/customize";
        }
        try {
            String message = accountService.customizeAccount(this.customizeAccountId, customizeAccount.getPreferredName());
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());

        }
        return "redirect:/corporate/account/customize";

    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal) {
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<Account> accounts = accountService.filterUnrestrictedAccounts(user.getCorporate().getAccounts());
        for (Account account : accounts) {
            Code code = codeService.getByTypeAndCode("ACCOUNT_CLASS", account.getSchemeType());
            if (code != null && code.getDescription() != null) {
                account.setSchemeType(code.getDescription());
            }
        }

        model.addAttribute("accounts", accounts);
        return "corp/account/setting";
    }

    @GetMapping("/{id}/hide")
    public String hide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        try {
            String message = accountService.hideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/{id}/unhide")
    public String unhide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {

        try {
            String message = accountService.unhideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/{id}/primary")
    public String makePrimary(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            String message = accountService.makePrimaryAccount(id, user.getCorporate().getCustomerId());
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException e) {
            logger.error("Account Primary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());

        }
        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/officer")
    public String getAccountOfficer() {
        return "corp/account/officer";
    }


    @GetMapping("/{id}/statement")
    public String getTransactionHistory(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Account account = accountRepo.findOne(id);
        String LAST_TEN_TRANSACTION = "10";
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(corporateUser.getCorporate().getAccounts());
        request.getSession().setAttribute("tranAccountNo", account.getAccountNumber());
        List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(account.getAccountNumber(), LAST_TEN_TRANSACTION);
        if (transRequestList != null && !transRequestList.isEmpty()) {
            model.addAttribute("acct", id);
            model.addAttribute("transRequestList", transRequestList);
            model.addAttribute("accountList", accountList);
//            logger.info("Last 10 Transaction {}", transRequestList);
            return "corp/account/accountstatement";
        }
        return "redirect:/corporate/dashboard";
    }

    @RequestMapping(path = "{id}/downloadhistory", method = RequestMethod.GET)
    public ModelAndView getTransPDF(@PathVariable String id, Model model, Principal principal, HttpServletRequest request) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Account account = accountService.getAccountByCustomerId(corporateUser.getCorporate().getCustomerId());
        logger.info("Corporate account {}", account);
        String LAST_TEN_TRANSACTION = "10";
        String acct = request.getSession().getAttribute("tranAccountNo").toString();
        logger.info("Getting the session account no {} ", acct);
        List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(acct,
                LAST_TEN_TRANSACTION);
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
        view.setApplicationContext(appContext);

        Map<String, Object> modelMap = new HashMap<>();
        for (TransactionHistory transactionHistory : transRequestList) {
            double amount = Double.parseDouble(transactionHistory.getBalance());
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("sender", corporateUser.getFirstName() + " " + corporateUser.getLastName());
            if (transactionHistory.getNarration() != null) {
                modelMap.put("remarks", transactionHistory.getNarration());
            } else {
                modelMap.put("remarks", "");
            }
            modelMap.put("recipientBank", "");
            modelMap.put("refNUm", transactionHistory.getTranType());
            modelMap.put("date", transactionHistory.getValueDate());
            modelMap.put("tranDate", DateFormatter.format(transactionHistory.getPostedDate()));
        }

        ModelAndView modelAndView = new ModelAndView(view, modelMap);
        return modelAndView;
    }

    @PostMapping("/history")
    public String getAccountHistory(Model model, Principal principal) {

        return "cust/account/history";
    }


    @GetMapping("/viewstatement")
    public String getViewOnly(Model model) throws ParseException {
        model.addAttribute("acctNum",null);

        return "corp/account/view";
    }

    @GetMapping("/viewstatement/{id}")
    public String getViewOnlyById(@PathVariable Long id,Model model) throws ParseException {
        AccountDTO accountDTO = accountService.getAccount(id);
        model.addAttribute("acctNum",accountDTO.getAccountNumber());
        return "corp/account/view";
    }

    @GetMapping("/{acct}/viewonlyhistory")
    public String getViewOnlyHist(@PathVariable String acct, Model model, Principal principal) throws ParseException {
        model.addAttribute("accountNumber", acct);
        SettingDTO setting = configurationService.getSettingByName("TRANS_HISTORY_RANGE");
        Date date = new Date();
        Date daysAgo = new DateTime(date).minusDays(Integer.parseInt(setting.getValue())).toDate();
//        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//        Date date = format.parse(format.format(date1));
//        Date daysAgo = format.parse(format.format(daysAgo1));
        AccountDTO account = accountService.getAccount(Long.parseLong(acct));
//        logger.info("the from date {} and the to date {} acctNUm {}",date,daysAgo,account.getAccountNumber());
        AccountStatement accountStatement = integrationService.getFullAccountStatement(account.getAccountNumber(), daysAgo, date, "B");
        List<TransactionDetails> list = accountStatement.getTransactionDetails();
        model.addAttribute("history", list);
        return "corp/account/tranhistory";
    }

    @GetMapping("/viewstatement/{acct}/display/data")
    public
    @ResponseBody
    DataTablesOutput<TransactionDetails> getStatementData(@PathVariable String acct, DataTablesInput input) {
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        try {
            Date date = new Date();
            Date daysAgo = new DateTime(date).minusDays(300).toDate();
            logger.info("Getting account statement from date {} to date {}", date.toString(), daysAgo.toString());

            AccountDTO account = accountService.getAccount(Long.parseLong(acct));

            AccountStatement accountStatement = integrationService.getAccountStatements(account.getAccountNumber(), date, daysAgo, "B", "5");

            out.setDraw(input.getDraw());

            List<TransactionDetails> list = accountStatement.getTransactionDetails();
            out.setData(list);
            int sz = 0;
            if (list != null) {
                sz = list.size();
            }
            logger.debug("Transaction history Size = {}",sz);
            out.setRecordsFiltered(sz);
            out.setRecordsTotal(sz);
        } catch (Exception e) {
            logger.error("Failed to get transaction history", e);
        }
        return out;

    }

    @GetMapping("/viewstatement/display/data/new")
    @ResponseBody
    public Map<String, Object> getStatementData(WebRequest webRequest, HttpSession session) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("details", null);
        objectMap.put("moreData", "");
        String acctNumber = webRequest.getParameter("acctNumber");
        String fromDate = webRequest.getParameter("fromDate");
        String toDate = webRequest.getParameter("toDate");
        String tranType = webRequest.getParameter("tranType");
        Date from = null;
        Date to = null;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            from = format.parse(fromDate);
            to = format.parse(toDate);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            logger.info("fromDate {}", formatter.format(from));
            logger.info("toDate {}", to);
            //int diffInDays = (int) ((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));

            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType, "5");
            List<TransactionDetails> list = accountStatement.getTransactionDetails();

//            out.setData(list);
            int sz = list == null ? 0 : list.size();
            out.setRecordsFiltered(sz);
            out.setRecordsTotal(sz);
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

            }
            session.removeAttribute("acctStmtLastDetails");
            if (list != null) {
                if (!list.isEmpty()) {
                    objectMap.replace("details", list);
                    objectMap.replace("moreData", accountStatement.getHasMoreData());
                    session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
                    session.setAttribute("retAcctStmtStateValue", 0);
                    session.setAttribute("acctStmtEntirePastDetails0", list);
                }
            }
        } catch (ParseException e) {
            logger.warn("didn't parse date", e);
        }
        return objectMap;

    }

    @GetMapping("/downloadstatement")
    public ModelAndView downloadStatementData(ModelMap modelMap, DataTablesInput input, String acctNumber,

                                              String fromDate, String toDate, String tranType, Principal principal, RedirectAttributes redirectAttributes) {
        Date from = null;
        Date to = null;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {
//            JasperReportsPdfView view = new JasperReportsPdfView();
//            view.setUrl("classpath:jasperreports/rpt_account-statement.jrxml");
//            view.setApplicationContext(appContext);
            from = format.parse(fromDate);
            to = format.parse(toDate);
            AccountStatement accountStatement = integrationService.getFullAccountStatement(acctNumber, from, to, tranType);
            out.setDraw(input.getDraw());
            List<TransactionDetails> list = accountStatement.getTransactionDetails();
            Account account = accountService.getAccountByAccountNumber(acctNumber);
//            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
//            logger.info("list {}", list);
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
            ModelAndView modelAndView = new ModelAndView("rpt_account-statement", modelMap);
//            ModelAndView modelAndView = new ModelAndView(view, modelMap);
            return modelAndView;
        } catch (ParseException e) {
            logger.warn("didn't parse date", e);
            ModelAndView modelAndView = new ModelAndView("redirect:/corporate/account/viewstatement");
            modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            //redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            return modelAndView;
        } catch (Exception e) {
            logger.info(" STATEMENT DOWNLOAD {} ", e.getMessage());
            ModelAndView modelAndView = new ModelAndView("redirect:/corporate/account/viewstatement");
            modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            //redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            return modelAndView;
        }
//        ModelAndView modelAndView = new ModelAndView("rpt_account-statement", modelMap);
//        return modelAndView;
    }

    @GetMapping("/viewstatement/corp/display/data/next")
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
        List<TransactionDetails> list = null;
        Date from = null;
        Date to = null;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            from = format.parse(fromDate);
            to = format.parse(toDate);
            TransactionDetails transactionDetails = null;
            if ((session.getAttribute("acctStmtLastDetails") != null) && (state.equalsIgnoreCase("forward"))) {
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
                }
                if (!list.isEmpty()) {
                    session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
                    Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
                    logger.info("acct statemnet state corp before{}", stateValue);
                    stateValue = stateValue + 1;
                    session.removeAttribute("acctStmtEntirePastDetails" + stateValue);
                    session.setAttribute("acctStmtEntirePastDetails" + stateValue, list);
                    session.removeAttribute("retAcctStmtStateValue");
                    session.setAttribute("retAcctStmtStateValue", stateValue);
                    objectMap.replace("details", list);
                    objectMap.replace("moreData", accountStatement.getHasMoreData());
                }
            }

        } catch (ParseException e) {
            logger.warn("didn't parse date {}", e);
        } catch (Exception e) {
            logger.warn("error cause by {}", e.getMessage());
        }
        return objectMap;

    }

    @GetMapping("/viewstatement/corp/display/data/back")
    @ResponseBody
    public Map<String, Object> getStatementDataForBack(WebRequest webRequest, HttpSession session) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("details", null);
        objectMap.put("previousData", "");
        String state = webRequest.getParameter("state");
        List<TransactionDetails> list = null;
        if ((state.equalsIgnoreCase("backward"))) {
            Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
            stateValue -= 1;
            if (session.getAttribute("acctStmtEntirePastDetails" + stateValue) != null) {
                session.removeAttribute("retAcctStmtStateValue");
                session.setAttribute("retAcctStmtStateValue", stateValue);
                list = (List<TransactionDetails>) session.getAttribute("acctStmtEntirePastDetails" + stateValue);
                session.removeAttribute("acctStmtLastDetails");
                session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
                session.setAttribute("hasMoreTransaction", "Y");
                objectMap.replace("details", list);
                objectMap.replace("previousData", stateValue);
            }
        }

        return objectMap;

    }

}
