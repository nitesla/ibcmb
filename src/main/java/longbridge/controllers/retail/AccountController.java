//package longbridge.controllers.retail;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import longbridge.api.AccountDetails;
//import longbridge.dtos.AccountDTO;
//import longbridge.dtos.CorporateDTO;
//import longbridge.dtos.CorporateUserDTO;
//import longbridge.exception.InternetBankingException;
//import longbridge.forms.CustomizeAccount;
//import longbridge.models.Account;
//import longbridge.models.RetailUser;
//import longbridge.models.TransRequest;
//import longbridge.repositories.AccountRepo;
//import longbridge.services.AccountService;
//import longbridge.services.IntegrationService;
//import longbridge.services.RetailUserService;
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.data.JsonDataSource;
//import net.sf.jasperreports.engine.design.JasperDesign;
//import net.sf.jasperreports.engine.export.JRXlsExporter;
//import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
//import net.sf.jasperreports.engine.xml.JRXmlLoader;
//import longbridge.services.TransferService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.MessageSource;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.core.io.Resource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
//import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
//import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;
//
//import javax.validation.Valid;
//import java.io.File;
//import java.io.IOException;
//import java.security.Principal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import static net.sf.jasperreports.engine.JasperExportManager.*;
//import static org.springframework.data.repository.init.ResourceReader.Type.JSON;
//
///**
// * Created by Fortune on 4/3/2017.
// */
//@Controller
//@RequestMapping("/retail/account")
//public class AccountController {
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private AccountService accountService;
//
//    @Autowired
//    private RetailUserService retailUserService;
//
//    @Autowired
//    private IntegrationService integrationService;
//
//    @Autowired
//    private MessageSource messageSource;
//
//    @Autowired private TransferService transferService;
//
//    @Autowired
//    AccountRepo accountRepo;
//
//    private Long customizeAccountId;
//
//    private ApplicationContext appContext;
//    @Value("${jsonFile.path}")
//    private String JOSNpath;
//    @Value("${jrxmlFile.path}")
//    private String jrxmlPath;
//    @Value("${savedDocFile.path}")
//    private String savedDoc;
//
//    @GetMapping
//    public String listAccounts(){
//        return "cust/account/index";
//    }
//
//    @GetMapping("{id}/view")
//    public String viewAccount(@PathVariable Long id, Model model){
//        //fetch account details from Account Service
//    	AccountDTO accountDTO = accountService.getAccount(id);
//        AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());
//
//        if(account == null){
//        	//account not found
//        	return "redirect:/retail/account";
//        }
//        //send account to frontend
//        model.addAttribute("account", account);
//        return "cust/account/details";
//    }
//
//
//    @GetMapping("/customize")
//    public String CustomizeAccountHome(Model model, Principal principal){
//        RetailUser user = retailUserService.getUserByName(principal.getName());
//        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
//        model.addAttribute("accounts", accounts);
//        return "cust/account/customizehome";
//    }
//
//    @GetMapping("/{id}/customize")
//    public String CustomizeAccount(@PathVariable Long id, CustomizeAccount customizeAccount, Principal principal, Model model, RedirectAttributes redirectAttributes){
//
//        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
//
//        AccountDTO accountDTO = accountService.getAccount(id);
//        if (!retailUser.getCustomerId().equals(accountDTO.getCustomerId())){
//            redirectAttributes.addFlashAttribute("message", "Access Denied");
//            return "redirect:/retail/logout";
//        }
//
//        this.customizeAccountId = accountDTO.getId();
//        model.addAttribute("account", accountDTO.getAccountName());
//        return "cust/account/customize";
//    }
//
//    @PostMapping("/customize")
//    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale)throws Exception{
//        if (result.hasErrors()){
//            model.addAttribute("failure","Pls correct the errors");
//            return "cust/account/customize";
//        }
//
//        try{
//            String message = accountService.customizeAccount(this.customizeAccountId, customizeAccount.getAccountName());
//            redirectAttributes.addFlashAttribute("message", message);
//        }catch (InternetBankingException e){
//            logger.error("Customization Error", e);
//            redirectAttributes.addFlashAttribute("failure", e.getMessage());
//        }
//
//        return "redirect:/retail/account/customize";
//    }
//
//    @GetMapping("/settings")
//    public String settingsPage(Model model, Principal principal){
//        RetailUser user = retailUserService.getUserByName(principal.getName());
//        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
//        model.addAttribute("accounts", accounts);
//        return "cust/account/setting";
//    }
//
//    @GetMapping("/{id}/hide")
//    public String hide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
//
//        try{
//            String message = accountService.hideAccount(id);
//            redirectAttributes.addFlashAttribute("message", message);
//        }catch (InternetBankingException e){
//            logger.error("Customization Error", e);
//            redirectAttributes.addFlashAttribute("failure", e.getMessage());
//        }
//
//        return "redirect:/retail/account/settings";
//    }
//
//    @GetMapping("/{id}/unhide")
//    public String unhide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
//
//        try{
//            String message = accountService.unhideAccount(id);
//            redirectAttributes.addFlashAttribute("message", message);
//        }catch (InternetBankingException e){
//            logger.error("Customization Error", e);
//            redirectAttributes.addFlashAttribute("failure", e.getMessage());
//        }
//        return "redirect:/retail/account/settings";
//    }
//
//    @GetMapping("/{id}/primary")
//    public String makePrimary(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale){
//
//        try{
//            RetailUser user = retailUserService.getUserByName(principal.getName());
//            String message = accountService.makePrimaryAccount(id, user.getCustomerId());
//            redirectAttributes.addFlashAttribute("message", message);
//        }catch (InternetBankingException e){
//            logger.error("Account Primary Error", e);
//            redirectAttributes.addFlashAttribute("failure", e.getMessage());
