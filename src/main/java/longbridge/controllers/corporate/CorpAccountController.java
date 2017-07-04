package longbridge.controllers.corporate;

import longbridge.api.AccountDetails;
import longbridge.dtos.AccountDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;

import longbridge.models.RetailUser;

import longbridge.services.AccountService;
import longbridge.services.CorporateUserService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionDetails;
import longbridge.repositories.AccountRepo;
import longbridge.services.*;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionDetails;
import longbridge.utils.statement.TransactionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private MessageSource messageSource;

    @Autowired
    private TransferService transferService;

    @Autowired
    AccountRepo accountRepo;

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
        model.addAttribute("account", accountDTO.getAccountName());
        return "corp/account/customize";
    }

    @PostMapping("/customize")
    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result,RedirectAttributes redirectAttributes, Model model)throws Exception{
        if (result.hasErrors()){
            model.addAttribute("message","Pls correct the errors");
            return "corp/account/customize";
        }
try {
    String message = accountService.customizeAccount(this.customizeAccountId, customizeAccount.getAccountName());
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
    public String getTransactionHistory(@PathVariable Long id, Model model, Principal principal) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Account account = accountRepo.findOne(id);
        String LAST_TEN_TRANSACTION="10";
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(corporateUser.getCorporate().getCustomerId());
        List<TransactionHistory> transRequestList=integrationService.getLastNTransactions(account.getAccountNumber(),LAST_TEN_TRANSACTION);
        if (transRequestList != null  && ! transRequestList.isEmpty()) {
            model.addAttribute("transRequestList", transRequestList);
            model.addAttribute("accountList", accountList);
            System.out.println("what is the " + transRequestList);
            return "corp/account/accountstatement";
        }
        return "redirect:/corporate/dashboard";
    }

    @PostMapping("/history")
    public String getAccountHistory(Model model, Principal principal) {

        return "cust/account/history";
    }


    @GetMapping("/viewstatement")
    public String getViewOnly(Model model, Principal principal) throws ParseException {

        return "corp/account/view";
    }

    @GetMapping("/viewstatement/display/data")
    public @ResponseBody
    DataTablesOutput<TransactionDetails> getStatementData(DataTablesInput input, String acctNumber,



                                                          String fromDate, String toDate, String tranType) {

        // Pageable pageable = DataTablesUtils.getPageable(input);

        Date from;
        Date to;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        try {
            from = dateFormat.parse(fromDate);
            to = dateFormat.parse(toDate);
            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to,tranType);
            logger.info("TransactionType {}",tranType);
            out.setDraw(input.getDraw());
            List<TransactionDetails> list = new ArrayList<>();
            if (list != null && !list.isEmpty()) {

                list=accountStatement.getTransactionDetails();
                System.out.println(accountStatement.toString());
                System.out.println("Whats in the list "+list);


                out.setData(list);
                out.setRecordsFiltered(list.size());
                out.setRecordsTotal(list.size());
            }
        } catch (ParseException e) {
            logger.warn("didn't parse date", e);
        }
        return out;

    }

    @GetMapping("/downloadstatement")
    public ModelAndView downloadStatementData(ModelMap modelMap, DataTablesInput input, String acctNumber,

                                              String fromDate, String toDate, String tranType, Principal principal) {
        // Pageable pageable = DataTablesUtils.getPageable(input);

        Date from;
        Date to;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        try {
            from = dateFormat.parse(fromDate);
            to = dateFormat.parse(toDate);

            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType);

            out.setDraw(input.getDraw());
            List<TransactionDetails> list = accountStatement.getTransactionDetails();
            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
            System.out.println("list = " + list);
            modelMap.put("datasource", list);
            modelMap.put("format", "pdf");
              modelMap.put("summary.accountNum", acctNumber);
            modelMap.put("summary.customerName",corporateUser.getFirstName()+" "+corporateUser.getLastName());
            modelMap.put("summary.customerNo", corporateUser.getCorporate().getCustomerId());
            modelMap.put("summary.openingBalance", accountStatement.getOpeningBalance());
            if(accountStatement.getDebitCount()!=null) {
                modelMap.put("summary.debitCount", accountStatement.getDebitCount());
            }
            else{modelMap.put("summary.debitCount", "");}
            if(accountStatement.getCreditCount()!=null) {
                modelMap.put("summary.creditCount", accountStatement.getCreditCount());
            }
            else{modelMap.put("summary.creditCount", "");}
            modelMap.put("summary.currencyCode", accountStatement.getCurrencyCode());
            if(accountStatement.getClosingBalance()!=null) {
                modelMap.put("summary.closingBalance", accountStatement.getClosingBalance());
            }else{modelMap.put("summary.closingBalance","" );}
            modelMap.put("summary.totalDebit", accountStatement.getTotalDebit());
            modelMap.put("summary.totalCredit", accountStatement.getTotalCredit());
            modelMap.put("summary.address", "");
            modelMap.put("fromDate", fromDate);
            modelMap.put("toDate", toDate);
            Date today=new Date();
            modelMap.put("today",today);

        } catch (ParseException e) {
            logger.warn("didn't parse date", e);
        }

        ModelAndView modelAndView = new ModelAndView("rpt_account-statement", modelMap);
        return modelAndView;


    }
}
