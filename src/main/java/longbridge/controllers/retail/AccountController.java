package longbridge.controllers.retail;

import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.api.AccountDetails;
import longbridge.dtos.AccountDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.sf.jasperreports.engine.JasperExportManager.*;
import static org.springframework.data.repository.init.ResourceReader.Type.JSON;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/account")
public class AccountController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AccountService accountService;

    @Autowired
    private RetailUserService retailUserService;
    
    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private MessageSource messageSource;

    private Long customizeAccountId;

    private ApplicationContext appContext;
    @Value("${jsonFile.path}")
    private String JOSNpath;
    @Value("${jrxmlFile.path}")
    private String jrxmlPath;
    @Value("${savedDocFile.path}")
    private String savedDoc;

    @GetMapping
    public String listAccounts(){
        return "cust/account/index";
    }

    @GetMapping("{id}/view")
    public String viewAccount(@PathVariable Long id, Model model){
        //fetch account details from Account Service
    	AccountDTO accountDTO = accountService.getAccount(id);
        AccountDetails account = integrationService.viewAccountDetails(accountDTO.getAccountNumber());

        if(account == null){
        	//account not found
        	return "redirect:/retail/account";
        }
        //send account to frontend
        model.addAttribute("account", account);
        return "cust/account/details";
    }


    @GetMapping("/customize")
    public String CustomizeAccountHome(Model model, Principal principal){
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
        model.addAttribute("accounts", accounts);
        return "cust/account/customizehome";
    } 

    @GetMapping("/{id}/customize")
    public String CustomizeAccount(@PathVariable Long id, CustomizeAccount customizeAccount, Principal principal, Model model, RedirectAttributes redirectAttributes){

        RetailUser retailUser = retailUserService.getUserByName(principal.getName());

        AccountDTO accountDTO = accountService.getAccount(id);
        if (!retailUser.getCustomerId().equals(accountDTO.getCustomerId())){
            redirectAttributes.addFlashAttribute("message", "Access Denied");
            return "redirect:/retail/logout";
        }

        this.customizeAccountId = accountDTO.getId();
        model.addAttribute("account", accountDTO.getAccountName());
        return "cust/account/customize";
    }

    @PostMapping("/customize")
    public String updateCustom(@Valid CustomizeAccount customizeAccount, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale)throws Exception{
        if (result.hasErrors()){
            model.addAttribute("failure","Pls correct the errors");
            return "cust/account/customize";
        }

        try{
            String message = accountService.customizeAccount(this.customizeAccountId, customizeAccount.getAccountName());
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/retail/account/customize";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal){
        RetailUser user = retailUserService.getUserByName(principal.getName());
        Iterable<AccountDTO> accounts = accountService.getAccounts(user.getCustomerId());
        model.addAttribute("accounts", accounts);
        return "cust/account/setting";
    }

    @GetMapping("/{id}/hide")
    public String hide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale){

        try{
            String message = accountService.hideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/retail/account/settings";
    }

    @GetMapping("/{id}/unhide")
    public String unhide(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale){

        try{
            String message = accountService.unhideAccount(id);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("Customization Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }
        return "redirect:/retail/account/settings";
    }

    @GetMapping("/{id}/primary")
    public String makePrimary(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, Locale locale){

        try{
            RetailUser user = retailUserService.getUserByName(principal.getName());
            String message = accountService.makePrimaryAccount(id, user.getCustomerId());
            redirectAttributes.addFlashAttribute("message", message);
        }catch (InternetBankingException e){
            logger.error("Account Primary Error", e);
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
        }

        return "redirect:/retail/account/settings";
    }

    @GetMapping("/officer")
    public String getAccountOfficer(){
        return "cust/account/officer";
    }

    @GetMapping("/{id}/statement")
    public String getAccountStatement(@PathVariable Long id, Model model,Principal principal){
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());
        model.addAttribute("accountList", accountList);
        model.addAttribute("retailUser", retailUser);

        return "cust/account/accountstatement";
    }

    @PostMapping("/history")
    public String getAccountHistory( Model model,Principal principal){

        return "cust/account/history";
    }

    @GetMapping("/viewstatement")
    public String getViewStatement( Model model,Principal principal){

        return "cust/account/view";
    }
    @GetMapping("/print/statement")
    public String getAcctStmtPDF(){
        logger.info("account statement running");
        File file = new File("classpath:pdf");
        Map<String,Object> parameters = new HashMap<String, Object>();
        parameters.put("AccountNum","10112332");
        parameters.put("date","01-08-2017");
        String destination = "C:\\Users\\Longbridge\\Documents\\InternetBanking\\master\\src\\main\\resources\\pdf\\";
        Resource resource = new ClassPathXmlApplicationContext().getResource(jrxmlPath);
        try {
            JsonDataSource ds = new JsonDataSource(new File("C:\\Users\\Longbridge\\Documents\\InternetBanking\\master\\src\\main\\resources\\pdf\\MOCK_DATA2.json"));
            JasperDesign jasperDesign = JRXmlLoader.load(resource.getInputStream());
            JasperReport jasperReport  = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,  parameters,  ds);
            exportReportToPdfFile(jasperPrint, destination+"accountStatement"+".pdf");
            File destFile = new File(destination+"accountStatement"+".xlsx");
            try {
                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
                exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
                exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
                exporter.exportReport();
            } catch (JRException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            JRDataSource dataSource = new JREmptyDataSource();
//            File outDir = new File("classpath: pdf/outputFile");
//            outDir.mkdirs();
//            logger.info("conpilation successful");
//            JasperReport jasperReport = JasperCompileManager.compileReport("classpath:pdf/accountStatement.jrxml");
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, dataSource);
//
//            JasperExportManager.exportReportToPdfFile(jasperPrint,"pdf/outputFile/test.pdf");
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}
