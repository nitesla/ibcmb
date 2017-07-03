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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
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


    @GetMapping("/viewstatement")
    public String getViewOnly(Model model, Principal principal) throws ParseException {

        return "corp/account/view";
    }

    @GetMapping("/viewstatement/display/data")
    public @ResponseBody
    DataTablesOutput<TransactionDetails> getStatementData(DataTablesInput input, String acctNumber,
                                                          String fromDate, String toDate, String transType) {
        // Pageable pageable = DataTablesUtils.getPageable(input);

        Date from;
        Date to;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        try {
            from = dateFormat.parse(fromDate);
            to = dateFormat.parse(toDate);
            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to,transType);
            logger.info("TransactionType {}",transType);
            out.setDraw(input.getDraw());
            List<TransactionDetails> list = new ArrayList<>();
            if (list != null || !(list.equals("")) || !(list.isEmpty())) {
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
                                              String fromDate, String toDate, String transType, Principal principal) {
        // Pageable pageable = DataTablesUtils.getPageable(input);

        Date from;
        Date to;
        DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
        try {
            from = dateFormat.parse(fromDate);
            to = dateFormat.parse(toDate);
            AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to,transType);
            out.setDraw(input.getDraw());
            List<TransactionDetails> list = accountStatement.getTransactionDetails();
            CorporateUser corporateUser=corporateUserService.getUserByName(principal.getName());
            System.out.println("list = " + list);
            modelMap.put("datasource", list);
            modelMap.put("format", "pdf");
            modelMap.put("summary.accountNum", acctNumber);
            modelMap.put("customerName",corporateUser.getFirstName()+" "+corporateUser.getLastName());
            logger.info("Customer's Name {}"+corporateUser.getFirstName()+" "+corporateUser.getLastName());

            if(accountStatement.getAccountNumber()!=null) {
                modelMap.put("customerNo", acctNumber);
            }
            else if(accountStatement.getAccountNumber()==null ||accountStatement.getAccountNumber().isEmpty()){
                modelMap.put("customerNo","");
            }
            else{};
            modelMap.put("summary.openingBalance", accountStatement.getOpeningBalance());
            System.out.println("whats the openingBalance:"+ accountStatement.getOpeningBalance());
            if (accountStatement.getDebitCount() != null) {
                modelMap.put("debitCount", accountStatement.getDebitCount());
                System.out.println("whats the debit count:"+accountStatement.getDebitCount());
            }
            else{
                modelMap.put("debitCount","");
            }
            if (accountStatement.getCreditCount() != null) {
                modelMap.put("creditCount", accountStatement.getCreditCount());
                System.out.println("whats the credit count:" + accountStatement.getCreditCount());
            }
            else{
                modelMap.put("creditCount","");
            }
            modelMap.put("summary.currencyCode", accountStatement.getCurrencyCode());
            if(accountStatement.getClosingBalance()!=null) {
                modelMap.put("summary.closingBalance", accountStatement.getClosingBalance());
                System.out.println("whats the closingBalance:" + accountStatement.getClosingBalance());
            }
            else{
                modelMap.put("summary.closingBalance","0");
            }
            modelMap.put("summary.totalDebit", accountStatement.getTotalDebit());
            modelMap.put("summary.totalCredit", accountStatement.getTotalCredit());
            if(accountStatement.getAddress()!=null ) {
                modelMap.put("address", accountStatement.getAddress());
                System.out.println("whats the address:" + accountStatement.getAddress());
            }
            else if(accountStatement.getAddress()==null){
                modelMap.put("address","14 Bello owosho Street");
            }
            else{};

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
