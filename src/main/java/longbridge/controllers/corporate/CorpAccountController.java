package longbridge.controllers.corporate;

import longbridge.api.AccountDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.CodeDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.text.DateFormat;
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


    @GetMapping
    public String listAccounts(){
        return "corp/account/index";
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");

    @GetMapping("{id}/view")
    public String viewAccount(@PathVariable Long id, Model model) {
        //fetch account details from Account Service
        AccountDTO accountDTO = accountService.getAccount(id);
        AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());

        if(account == null){
            //account not found
            return "redirect:/corporate/account";
        }
        //send account to frontend
        model.addAttribute("account", account);
        return "corp/account/details";

    }


    @GetMapping("/customize")
    public String CustomizeAccountHome(Model model, Principal principal){
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCorporate().getCustomerId());
        model.addAttribute("accounts", accounts);
        return "corp/account/customizehome";
    }

    @GetMapping("/{id}/customize")
    public String CustomizeAccount(@PathVariable Long id, CustomizeAccount customizeAccount, Principal principal, Model model, RedirectAttributes redirectAttributes){

       CorporateUser corporateUser= corporateUserService.getUserByName(principal.getName());

        AccountDTO accountDTO = accountService.getAccount(id);
        if (!corporateUser.getCorporate().getCustomerId().equals(accountDTO.getCustomerId())){
            redirectAttributes.addFlashAttribute("message", "Access Denied");
            return "redirect:/corporate/logout";
        }

        this.customizeAccountId = accountDTO.getId();
        model.addAttribute("account", accountDTO.getPreferredName());
        return "corp/account/customize";
    }

    @PostMapping("/customize")
    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result,RedirectAttributes redirectAttributes, Model model)throws Exception{
        if (result.hasErrors()){

            model.addAttribute("message","Name field cannot be empty");
            return "corp/account/customize";
        }
try {
    String message = accountService.customizeAccount(this.customizeAccountId, customizeAccount.getPreferredName());
    redirectAttributes.addFlashAttribute("message", message);
}catch(InternetBankingException e) {
    logger.error("Customization Error",e);
    redirectAttributes.addFlashAttribute("failure", e.getMessage());

}
        return "redirect:/corporate/account/customize";

    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal){
       CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCorporate().getCustomerId());
        model.addAttribute("accounts", accounts);
        return "corp/account/setting";
    }

    @GetMapping("/{id}/hide")
    public String hide(@PathVariable Long id, Model model, Principal principal,RedirectAttributes redirectAttributes){

        try{
            String message = accountService.hideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/{id}/unhide")
    public String unhide(@PathVariable Long id, Model model, Principal principal,RedirectAttributes redirectAttributes, Locale locale){

        try{
            String message = accountService.unhideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/{id}/primary")
    public String makePrimary(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
try {
    CorporateUser user = corporateUserService.getUserByName(principal.getName());
    String message=accountService.makePrimaryAccount(id,user.getCorporate().getCustomerId());
    redirectAttributes.addFlashAttribute("message", message);
    }
catch(InternetBankingException e){
    logger.error("Account Primary Error", e);
    redirectAttributes.addFlashAttribute("failure", e.getMessage());

}
        return "redirect:/corporate/account/settings";
    }

    @GetMapping("/officer")
    public String getAccountOfficer(){
        return "corp/account/officer";
    }


    @GetMapping("/{id}/statement")
    public String getTransactionHistory(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Account account = accountRepo.findOne(id);
        String LAST_TEN_TRANSACTION = "10";
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(corporateUser.getCorporate().getCustomerId());
        request.getSession().setAttribute("tranAccountNo",account.getAccountNumber());
        List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(account.getAccountNumber(), LAST_TEN_TRANSACTION);
        if (transRequestList != null && !transRequestList.isEmpty()) {
            model.addAttribute("acct", id);
            model.addAttribute("transRequestList", transRequestList);
            model.addAttribute("accountList", accountList);
            logger.info("Last 10 Transaction {}", transRequestList);
            return "corp/account/accountstatement";
        }
        return "redirect:/corporate/dashboard";
    }

    @RequestMapping(path = "{id}/downloadhistory", method = RequestMethod.GET)
    public ModelAndView getTransPDF(@PathVariable String id, Model model, Principal principal,HttpServletRequest request) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Account account=accountService.getAccountByCustomerId(corporateUser.getCorporate().getCustomerId());
        logger.info("Corporate account {}",account);
        String LAST_TEN_TRANSACTION = "10";
        String acct=request.getSession().getAttribute("tranAccountNo").toString();
        logger.info("Getting the session account no {} ",acct);
        List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(acct,
                LAST_TEN_TRANSACTION);
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
        view.setApplicationContext(appContext);

        Map<String, Object> modelMap = new HashMap<>();
        for(TransactionHistory transactionHistory:transRequestList) {
            double amount = Double.parseDouble(transactionHistory.getBalance());
            DecimalFormat formatter = new DecimalFormat("#,###.00");

            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("sender",corporateUser.getFirstName()+" "+corporateUser.getLastName() );
            modelMap.put("remarks", transactionHistory.getNarration());
            modelMap.put("recipientBank", "");
            modelMap.put("refNUm", transactionHistory.getTranType());
            modelMap.put("date",transactionHistory.getValueDate());
            modelMap.put("tranDate", DateFormatter.format(transactionHistory.getPostedDate()));
        }

        ModelAndView modelAndView=new ModelAndView(view, modelMap);
        return modelAndView;
    }
    @PostMapping("/history")
    public String getAccountHistory(Model model, Principal principal) {

        return "cust/account/history";
    }


    @GetMapping("/viewstatement")
    public String getViewOnly(Model model, Principal principal) throws ParseException {
logger.info("viewstatement");
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
        AccountStatement accountStatement = integrationService.getAccountStatements(account.getAccountNumber(),daysAgo, date, "B");
        List<TransactionDetails> list = accountStatement.getTransactionDetails();
        model.addAttribute("history", list);
        return "corp/account/tranhistory";
    }

    @GetMapping("/viewstatement/{acct}/display/data")
    public @ResponseBody
    DataTablesOutput<TransactionDetails> getStatementData(@PathVariable String acct, DataTablesInput input) {
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        try {
            Date date = new Date();
            Date daysAgo = new DateTime(date).minusDays(300).toDate();
            logger.info("the from date {} and the to date {}",date,daysAgo);

            AccountDTO account = accountService.getAccount(Long.parseLong(acct));

            AccountStatement accountStatement = integrationService.getAccountStatements(account.getAccountNumber(), date, daysAgo, "B");

            logger.info("TransactionType {}","B");
            out.setDraw(input.getDraw());

            List<TransactionDetails> list = accountStatement.getTransactionDetails();
            out.setData(list);
            int sz = 0;
            if(list!=null){
                sz = list.size();
            }
            logger.info("Size = " + sz);
            out.setRecordsFiltered(sz);
            out.setRecordsTotal(sz);
        }
         catch (Exception e) {
            logger.warn("failed to get history", e);
        }
        return out;

    }@GetMapping("/viewstatement/display/data/new")
    public @ResponseBody
    DataTablesOutput<TransactionDetails> getStatementData( DataTablesInput input,String acctNumber,
                                                          String fromDate, String toDate, String tranType) {

// Pageable pageable = DataTablesUtils.getPageable(input);
        logger.info("fromDate {}",fromDate);
        logger.info("toDate {}",toDate);
//		Duration diffInDays= new Duration(new DateTime(fromDate),new DateTime(toDate));
//		logger.info("Day difference {}",diffInDays.getStandardDays());

        Date from = null;
        Date to = null;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            from = format.parse(fromDate);
            to = format.parse(toDate);
            logger.info("fromDate {}",from);
            logger.info("toDate {}",to);
            //int diffInDays = (int) ((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));

            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType);
            logger.info("TransactionType {}", tranType);
            out.setDraw(input.getDraw());
            List<TransactionDetails> list = accountStatement.getTransactionDetails();

            out.setData(list);
            int sz = list==null?0:list.size();
            out.setRecordsFiltered(sz);
            out.setRecordsTotal(sz);
        } catch (ParseException e) {
            logger.warn("didn't parse date", e);
        }
        return out;

    }

    @GetMapping("/downloadstatement")
    public ModelAndView downloadStatementData(ModelMap modelMap, DataTablesInput input, String acctNumber,

                                              String fromDate, String toDate, String tranType, Principal principal,RedirectAttributes redirectAttributes)  {
        // Pageable pageable = DataTablesUtils.getPageable(input);
        logger.info("the acctNumber{} fromDate {} toDate {} tranType {} ",acctNumber,fromDate,toDate,tranType);
        Date from = null;
        Date to = null;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {
            from = format.parse(fromDate);
            to = format.parse(toDate);

            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType);

            out.setDraw(input.getDraw());
            List<TransactionDetails> list = accountStatement.getTransactionDetails();
            CorporateUser corporateUser=corporateUserService.getUserByName(principal.getName());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            logger.info("list {}", list);
            modelMap.put("datasource", list);
            modelMap.put("format", "pdf");
              modelMap.put("summary.accountNum", acctNumber);
            modelMap.put("summary.customerName",corporateUser.getFirstName()+" "+corporateUser.getLastName());
            modelMap.put("summary.customerNo", corporateUser.getCorporate().getCustomerId());
            double amount = Double.parseDouble(accountStatement.getOpeningBalance());

            modelMap.put("summary.openingBalance", formatter.format(amount));
            // the total debit and credit is referred as total debit count and credit count
            if(accountStatement.getTotalDebit()!=null) {
                modelMap.put("summary.debitCount", accountStatement.getTotalDebit());
            }
            else{modelMap.put("summary.debitCount", "");}
            if(accountStatement.getTotalCredit()!=null) {
                modelMap.put("summary.creditCount", accountStatement.getTotalCredit());
            }
            else{modelMap.put("summary.creditCount", "");}
            modelMap.put("summary.currencyCode", accountStatement.getCurrencyCode());
            if(accountStatement.getClosingBalance()!=null) {
                double closingbal = Double.parseDouble(accountStatement.getClosingBalance());

                modelMap.put("summary.closingBalance", formatter.format(closingbal));
            }else{modelMap.put("summary.closingBalance","" );}

            // the total debit and credit is referred as total debit count and credit count
            if(accountStatement.getDebitCount()!=null) {
                modelMap.put("summary.totalDebit", accountStatement.getDebitCount());
            }else{modelMap.put("summary.totalDebit", "");}
            if(accountStatement.getCreditCount()!=null) {
                modelMap.put("summary.totalCredit", accountStatement.getCreditCount());
            }else{ modelMap.put("summary.totalCredit", "");
            }

            if(accountStatement.getAddress()!=null) {
                modelMap.put("summary.address", accountStatement.getAddress());
            }else{modelMap.put("summary.address", "");}
            modelMap.put("fromDate", fromDate);
            modelMap.put("toDate", toDate);
            Date today=new Date();
            modelMap.put("today",today);

        } catch (ParseException e) {
            logger.warn("didn't parse date 2", e);
        }
        ModelAndView modelAndView = new ModelAndView("rpt_account-statement", modelMap);
        return modelAndView;


    }
}
