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

package longbridge.controllers.retail;

import longbridge.api.AccountDetails;
import longbridge.api.PaginationDetails;
import longbridge.dtos.AccountDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.models.TransRequest;
import longbridge.repositories.AccountRepo;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionDetails;
import longbridge.utils.statement.TransactionHistory;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.repo.Resource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
 import org.joda.time.Duration;
import java.util.*;

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

	@Autowired
	private TransferService transferService;

	@Autowired
	AccountRepo accountRepo;

	@Autowired
	JavaMailSender mailSender;

	/*
	 * @Autowired @Qualifier("accountReport2") private JasperReportsPdfView
	 * helloReport;
	 */

	private Long customizeAccountId;

	@Autowired
	private ApplicationContext appContext;

	@Value("${jsonFile.path}")
	private String JOSNpath;
	@Value("${jrxmlFile.path}")
	private String jrxmlPath;
	@Value("${savedDocFile.path}")
	private String savedDoc;
	@Value("${excel.path}")
	String PROPERTY_EXCEL_SOURCE_FILE_PATH;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");

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
		model.addAttribute("account", accountDTO.getAccountName());
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
					customizeAccount.getAccountName());
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
	public String getTransactionHistory(@PathVariable Long id, Model model, Principal principal) {
		RetailUser retailUser = retailUserService.getUserByName(principal.getName());

		Account account = accountRepo.findOne(id);
		String LAST_TEN_TRANSACTION = "10";
		List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());
		List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(account.getAccountNumber(),
				LAST_TEN_TRANSACTION);
		if (transRequestList != null && !transRequestList.isEmpty()) {
			model.addAttribute("transRequestList", transRequestList);
			model.addAttribute("accountList", accountList);
			logger.info("Last 10 Transaction {}", transRequestList);
			return "cust/account/accountstatement";
		}
		return "redirect:/retail/dashboard";
	}

		@RequestMapping(path = "{id}/downloadhistory", method = RequestMethod.GET)
		public ModelAndView getTransPDF(@PathVariable String id, Model model, Principal principal) {
			RetailUser retailUser = retailUserService.getUserByName(principal.getName());

			Account account=accountService.getAccountByCustomerId(retailUser.getCustomerId());
			logger.info("Retail account {}",account);
		String LAST_TEN_TRANSACTION = "10";
		List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(account.getAccountNumber(),
				LAST_TEN_TRANSACTION);
		JasperReportsPdfView view = new JasperReportsPdfView();
		view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
		view.setApplicationContext(appContext);

		Map<String, Object> modelMap = new HashMap<>();
		for(TransactionHistory transactionHistory:transRequestList) {
		modelMap.put("datasource", new ArrayList<>());
	modelMap.put("amount", transactionHistory.getBalance());
	modelMap.put("sender",retailUser.getFirstName()+" "+retailUser.getLastName() );
	modelMap.put("remarks", transactionHistory.getNarration());
	modelMap.put("recipientBank", "");
	modelMap.put("refNUm", transactionHistory.getTranType());
	modelMap.put("date",transactionHistory.getValueDate());
	modelMap.put("tranDate", transactionHistory.getPostedDate());
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

		return "cust/account/view";
	}

	@GetMapping("/viewstatement/display/data")

	public @ResponseBody
	DataTablesOutput<TransactionDetails> getStatementData(DataTablesInput input, String acctNumber,
														  String fromDate, String toDate, String tranType) {
		// Pageable pageable = DataTablesUtils.getPageable(input);
		logger.info("fromDate {}",fromDate);
		logger.info("toDate {}",toDate);
		Duration diffInDays= new Duration(new DateTime(fromDate),new DateTime(toDate));
		logger.info("Day difference {}",diffInDays.getStandardDays());

		Date from;
		Date to;
		DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
		try {
			from = dateFormat.parse(fromDate);
			to = dateFormat.parse(toDate);
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
			RetailUser retailUser = retailUserService.getUserByName(principal.getName());
			modelMap.put("datasource", list);
			modelMap.put("format", "pdf");
			modelMap.put("summary.accountNum",acctNumber);
			modelMap.put("summary.customerName",retailUser.getFirstName()+" "+retailUser.getLastName());
			modelMap.put("summary.customerNo", retailUser.getCustomerId());
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
			if (accountStatement.getClosingBalance() != null) {
				modelMap.put("summary.closingBalance", accountStatement.getClosingBalance());
			}else{modelMap.put("summary.closingBalance","" );}
			modelMap.put("summary.totalDebit", accountStatement.getTotalDebit());
			modelMap.put("summary.totalCredit", accountStatement.getTotalCredit());
			modelMap.put("summary.address", "");
			modelMap.put("fromDate", fromDate);
			modelMap.put("toDate", toDate);
			Date today = new Date();
			modelMap.put("today", today);

		} catch (ParseException e) {
			logger.warn("didn't parse date", e);
		}

		ModelAndView modelAndView = new ModelAndView("rpt_account-statement", modelMap);
		return modelAndView;

	}

	@PostMapping("sendEmail")
	public String sendEmail(ModelMap modelMap, DataTablesInput input, String acctNumber, String fromDate, String toDate,
			String tranType, Principal principal) throws MessagingException {
		/*
		 * JRDataSource ds = new JRBeanCollectionDataSource(reportList);
		 * 
		 * Resource report = new
		 * ClassPathResource("static/jasper/rpt_report.jasper");
		 * 
		 * JasperPrint jasperPrint =
		 * JasperFillManager.fillReport(report.getInputStream(),
		 * Collections.EMPTY_MAP,ds); ByteArrayOutputStream baos = new
		 * ByteArrayOutputStream();
		 * JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
		 * DataSource aAttachment = new ByteArrayDataSource(baos.toByteArray(),
		 * "application/pdf");
		 * 
		 * MimeMessage message = mailSender.createMimeMessage();
		 * MimeMessageHelper helper = new MimeMessageHelper(message);
		 * 
		 * helper.setTo("xxxxxx");
		 * 
		 * helper.setFrom("xxxxx"); helper.setSubject("Testing Email");
		 * 
		 * String text = "Testing Email";
		 * 
		 * helper.setText(text, false);
		 * 
		 * helper.addAttachment("report.pdf",aAttachment);
		 * 
		 * mailSender.send(message);
		 */
		return null;
	}

}

