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
//        model.addAttribute("account", accountDTO.getPreferredName());
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
//            String message = accountService.customizeAccount(this.customizeAccountId, customizeAccount.getPreferredName());
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
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.forms.CustomizeAccount;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.repositories.AccountRepo;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionDetails;
import longbridge.utils.statement.TransactionHistory;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private RetailUserService retailUserService;

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
	JavaMailSender mailSender;
	private Locale locale = LocaleContextHolder.getLocale();
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
	@Value("${jrxmlImage.path}")
	private String imagePath;
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
	public String getAccountHistory(@PathVariable Long id, Model model, Principal principal,HttpServletRequest request) {
		RetailUser retailUser = retailUserService.getUserByName(principal.getName());

		Account account = accountRepo.findOne(id);
		String LAST_TEN_TRANSACTION = "10";
		List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());
		request.getSession().setAttribute("tranAccountNo",account.getAccountNumber());
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
	public ModelAndView getTransPDF(@PathVariable String id, Model model, Principal principal,HttpServletRequest request) {
		RetailUser retailUser = retailUserService.getUserByName(principal.getName());

		Account account=accountService.getAccountByCustomerId(retailUser.getCustomerId());

		logger.info("Retail account {}",account);
		String LAST_TEN_TRANSACTION = "10";
		String acct=request.getSession().getAttribute("tranAccountNo").toString();
		logger.info("Getting the session account no {}",acct);
		List<TransactionHistory> transRequestList = integrationService.getLastNTransactions(acct, LAST_TEN_TRANSACTION);
		JasperReportsPdfView view = new JasperReportsPdfView();
		view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
		view.setApplicationContext(appContext);

		Map<String, Object> modelMap = new HashMap<>();
		for(TransactionHistory transactionHistory:transRequestList) {
			double amount = Double.parseDouble(transactionHistory.getBalance());
			DecimalFormat formatter = new DecimalFormat("#,###.00");
			modelMap.put("datasource", new ArrayList<>());
			modelMap.put("amount", formatter.format(amount));
			modelMap.put("sender",retailUser.getFirstName()+" "+retailUser.getLastName() );
			modelMap.put("remarks", transactionHistory.getNarration());
			modelMap.put("recipientBank", "");
			modelMap.put("refNUm", transactionHistory.getTranType());
			modelMap.put("date",DateFormatter.format(transactionHistory.getValueDate()));
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

		return "cust/account/view";
	}

	@GetMapping("/{acct}/viewonlyhistory")
	public String getViewOnlyHist(@PathVariable String acct, Model model, Principal principal) throws ParseException {
		model.addAttribute("accountNumber", acct);
		SettingDTO setting = configurationService.getSettingByName("TRANS_HISTORY_RANGE");
		Date date = new Date();
		Date daysAgo = new DateTime(date).minusDays(Integer.parseInt(setting.getValue())).toDate();
		logger.info("the from date {} and the to date {}",date,daysAgo);
		AccountDTO account = accountService.getAccount(Long.parseLong(acct));
		AccountStatement accountStatement = integrationService.getFullAccountStatement(account.getAccountNumber(), daysAgo , date, "B");
		List<TransactionDetails> list = accountStatement.getTransactionDetails();
		logger.info("The List {} ", list);
		model.addAttribute("history", list);
		return "cust/account/tranhistory";
	}

//	@GetMapping("/viewstatement/display/data")
//	public @ResponseBody
//	DataTablesOutput<TransactionDetails> getStatementData(DataTablesInput input, String acctNumber,
//														  String fromDate, String toDate, String tranType) {
//		// Pageable pageable = DataTablesUtils.getPageable(input);
//		logger.info("fromDate {}",fromDate);
//		logger.info("toDate {}",toDate);
////		Duration diffInDays= new Duration(new DateTime(fromDate),new DateTime(toDate));
////		logger.info("Day difference {}",diffInDays.getStandardDays());
//
//		Date from = null;
//		Date to = null;
//		DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
//		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//		try {
//			from = format.parse(fromDate);
//			to = format.parse(toDate);
//			logger.info("fromDate {}",from);
//			logger.info("toDate {}",to);
//			//int diffInDays = (int) ((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));
//
//			AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType);
//			logger.info("TransactionType {}", tranType);
//			out.setDraw(input.getDraw());
//			List<TransactionDetails> list = accountStatement.getTransactionDetails();
//
//			out.setData(list);
//			int sz = list==null?0:list.size();
//			out.setRecordsFiltered(sz);
//			out.setRecordsTotal(sz);
//		} catch (ParseException e) {
//			logger.warn("didn't parse date", e);
//		}
//		return out;
//
//	}

	@GetMapping("/viewstatement/display/data")
	@ResponseBody
	public Map<String,Object> getStatementData(WebRequest webRequest, HttpSession session) {
		// Pageable pageable = DataTablesUtils.getPageable(input);
		Map<String,Object> objectMap =  new HashMap<>();
		objectMap.put("details",null);
		objectMap.put("moreData","");
		String acctNumber = webRequest.getParameter("acctNumber");
		String fromDate = webRequest.getParameter("fromDate");
		String toDate = webRequest.getParameter("toDate");
		String tranType = webRequest.getParameter("tranType");
		logger.info("fromDate {}",fromDate);
		logger.info("toDate {}",toDate);
		List<TransactionDetails> list =  null;
		Date from = null;
		Date to = null;
		DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			from = format.parse(fromDate);
			to = format.parse(toDate);
			AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType,"5");
			session.removeAttribute("hasMoreTransaction");
			if(session.getAttribute("retAcctStmtStateValue") !=null) {
				Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
				for (int i = 0; i<=stateValue;i++) {
					session.removeAttribute("acctStmtEntirePastDetails"+stateValue);
				}
			}

			session.removeAttribute("retAcctStmtStateValue");

			if(accountStatement != null) {
				list = accountStatement.getTransactionDetails();
				session.setAttribute("hasMoreTransaction", accountStatement.getHasMoreData());
				logger.info("the current has more data  new {}",accountStatement.getHasMoreData());
			}
			session.removeAttribute("acctStmtLastDetails");
			if(!list.isEmpty()){
				logger.info("the current has more data  empty {}",accountStatement.getHasMoreData());
				session.setAttribute("acctStmtLastDetails",list.get(list.size()-1));
				session.setAttribute("retAcctStmtStateValue",0);
				session.setAttribute("acctStmtEntirePastDetails0",list);
				objectMap.replace("details",list);
				objectMap.replace("moreData",accountStatement.getHasMoreData());
			}

		} catch (ParseException e) {
			logger.warn("didn't parse date", e);
		}catch (Exception e){
			logger.warn("error cause by", e.getMessage());
		}
		return objectMap;

	}
	@GetMapping("/viewstatement/display/data/next")
	@ResponseBody
	public Map<String,Object> getStatementDataByState(WebRequest webRequest, HttpSession session) {
		// Pageable pageable = DataTablesUtils.getPageable(input);
		Map<String,Object> objectMap =  new HashMap<>();
		objectMap.put("details",null);
		objectMap.put("moreData","");
		String acctNumber = webRequest.getParameter("acctNumber");
		String fromDate = webRequest.getParameter("fromDate");
		String toDate = webRequest.getParameter("toDate");
		String tranType = webRequest.getParameter("tranType");
		String state = webRequest.getParameter("state");
		List<TransactionDetails> list =  null;
		Date from = null;
		Date to = null;
		DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
			from = format.parse(fromDate);
			to = format.parse(toDate);
			TransactionDetails transactionDetails = null;
			if((session.getAttribute("acctStmtLastDetails") != null)&&(state.equalsIgnoreCase("forward"))) {
				transactionDetails = (TransactionDetails) session.getAttribute("acctStmtLastDetails");
			}
			if(transactionDetails != null) {
				PaginationDetails paginationDetails = new PaginationDetails();
				paginationDetails.setLastAccountBalance(transactionDetails.getAccountBalance());
				paginationDetails.setLastAccountCurrency(transactionDetails.getCurrencyCode());
				paginationDetails.setLastPostDate(transactionDetails.getPostDate());
				paginationDetails.setLastTranId(transactionDetails.getTranId());
				paginationDetails.setLastTranDate(transactionDetails.getTranDate());
				paginationDetails.setLastTranSN(transactionDetails.getTranSN());
				AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to, tranType, "5",paginationDetails);
				list = accountStatement.getTransactionDetails();
				session.removeAttribute("acctStmtLastDetails");

				session.removeAttribute("hasMoreTransaction");
				if(accountStatement != null) {
					session.setAttribute("hasMoreTransaction", accountStatement.getHasMoreData());
					logger.info("the current has more data {}",accountStatement.getHasMoreData());
				}
				if (!list.isEmpty()) {
					session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
					Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
					stateValue= stateValue +1;
					session.removeAttribute("acctStmtEntirePastDetails"+stateValue);
					session.removeAttribute("retAcctStmtStateValue");
					session.setAttribute("acctStmtEntirePastDetails"+stateValue, list);
					session.setAttribute("retAcctStmtStateValue",stateValue);
					logger.info("acctStmtLastDetails {}", list.get(list.size() - 1));
					logger.info("acct statemnet state {}", stateValue);
					objectMap.replace("details",list);
					objectMap.replace("moreData",accountStatement.getHasMoreData());
				}
			}

		} catch (ParseException e) {
			logger.warn("didn't parse date {}", e);
		}catch (Exception e){
			logger.warn("error cause by {}", e.getMessage());
		}
		return objectMap;

	}
	@GetMapping("/viewstatement/display/data/back")
	@ResponseBody
	public Map<String,Object> getStatementDataForBack(WebRequest webRequest, HttpSession session) {
		String state = webRequest.getParameter("state");
//	logger.info("the state {}",state);
	Map<String,Object> objectMap =  new HashMap<>();
	objectMap.put("details",null);
	objectMap.put("previousData","");
		List<TransactionDetails> list =  null;
			if((state.equalsIgnoreCase("backward"))) {
				Integer stateValue = (Integer) session.getAttribute("retAcctStmtStateValue");
				stateValue -=1;
				if(session.getAttribute("acctStmtEntirePastDetails"+stateValue) != null) {
					session.removeAttribute("retAcctStmtStateValue");
					session.setAttribute("retAcctStmtStateValue", stateValue);
					logger.info("the state value back {}",stateValue);
					list = (List<TransactionDetails>) session.getAttribute("acctStmtEntirePastDetails"+stateValue);
					session.removeAttribute("acctStmtLastDetails");
					session.setAttribute("acctStmtLastDetails", list.get(list.size() - 1));
					session.setAttribute("hasMoreTransaction", "Y");
//					logger.info("acctStmtLastDetails  last record previous {}", list.get(list.size() - 1));
					objectMap.replace("details",list);
					objectMap.replace("previousData",stateValue);
				}
			}

		return objectMap;

	}


	@GetMapping("/downloadstatement")
	public ModelAndView downloadStatementData(ModelMap modelMap, DataTablesInput input, String acctNumber,
											  String fromDate, String toDate, String tranType, Principal principal) {
		Date from = null;
		Date to = null;
		DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		try {
//			JasperReportsPdfView view = new JasperReportsPdfView();
//			view.setUrl("classpath:jasperreports/rpt_account-statement.jrxml");
//			view.setApplicationContext(appContext);
			from = format.parse(fromDate);
			to = format.parse(toDate);
			AccountStatement accountStatement = integrationService.getFullAccountStatement(acctNumber, from, to, tranType);
			out.setDraw(input.getDraw());
			List<TransactionDetails> list = accountStatement.getTransactionDetails();
			if(!list.isEmpty()) {
//				logger.info("statemet list is {}", list);
			}else{
				logger.info("statement list is empty");
			}
			RetailUser retailUser = retailUserService.getUserByName(principal.getName());
			DecimalFormat formatter = new DecimalFormat("#,###.00");
			modelMap.put("datasource", list);
			modelMap.put("format", "pdf");
			modelMap.put("summary.accountNum",acctNumber);
			modelMap.put("summary.customerName",retailUser.getFirstName()+" "+retailUser.getLastName());
			modelMap.put("summary.customerNo", retailUser.getCustomerId());

			double amount = Double.parseDouble(accountStatement.getOpeningBalance());
			modelMap.put("summary.openingBalance", formatter.format(amount));
			// the total debit and credit is referred as total debit count and credit count
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
				double closingbal = Double.parseDouble(accountStatement.getClosingBalance());

				modelMap.put("summary.closingBalance", formatter.format(closingbal));
			}else{modelMap.put("summary.closingBalance","" );}

			// the total debit and credit is referred as total debit count and credit count
			if(accountStatement.getTotalDebit()!=null) {
				double totalDebit = Double.parseDouble(accountStatement.getTotalDebit());
				modelMap.put("summary.totalDebit", formatter.format(totalDebit));
			}else{
				modelMap.put("summary.totalDebit", "");
				logger.info("total debit is empty");
			}
			if(accountStatement.getTotalCredit()!=null) {
				double totalCredit = Double.parseDouble(accountStatement.getTotalCredit());
				modelMap.put("summary.totalCredit", formatter.format(totalCredit));
			}else{
				modelMap.put("summary.totalCredit", "");
				logger.info("total Credit is empty");
			}
			if(accountStatement.getAddress()!=null) {
				modelMap.put("summary.address", accountStatement.getAddress());
			}else{modelMap.put("summary.address", "");}
			modelMap.put("fromDate", fromDate);
			modelMap.put("toDate", toDate);
			Date today = new Date();
			modelMap.put("today", today);
			modelMap.put("imagePath", imagePath);
//			ModelAndView modelAndView = new ModelAndView(view, modelMap);
			ModelAndView modelAndView = new ModelAndView("rpt_account-statement4", modelMap);
			return modelAndView;
		} catch (ParseException e) {
			logger.warn("didn't parse date", e);
			ModelAndView modelAndView =  new ModelAndView("redirect:/retail/account/viewstatement");
			modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
			//redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("receipt.download.failed", null, locale));
			return modelAndView;
		}catch (Exception e){
			logger.info(" STATEMENT DOWNLOAD {} ", e.getMessage());
			ModelAndView modelAndView =  new ModelAndView("redirect:/retail/account/viewstatement");
			modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
			//redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("receipt.download.failed", null, locale));
			return modelAndView;
		}

//		ModelAndView modelAndView = new ModelAndView("rpt_account-statement", modelMap);
//		return modelAndView;

	}

	@PostMapping("sendEmail")
	public String sendEmail(ModelMap modelMap, DataTablesInput input, String acctNumber, String fromDate, String toDate,
							String tranType, Principal principal) throws MessagingException {

//		 JRDataSource ds = new JRBeanCollectionDataSource(modelMap);
//
//		 Resource report = new
//				 ClassPathResource("static/jasper/rpt_report.jasper");
//
//		try {
//			JasperPrint jasperPrint =
//            JasperFillManager.fillReport(report.getInputStream(),Collections.EMPTY_MAP,ds);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
//			DataSource aAttachment = new ByteArrayDataSource(baos.toByteArray(),
//            "application/pdf");
//			MimeMessage message = mailSender.createMimeMessage();
//			MimeMessageHelper helper = new MimeMessageHelper(message);
//
//			helper.setTo("xxxxxx");
//			helper.setFrom("xxxxx");
//			helper.setSubject("Testing Email");
//			String text = "Testing Email";
//			helper.setText(text, false);
//			helper.addAttachment("report.pdf",aAttachment);
//			mailSender.send(message);
//		} catch (JRException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return null;
	}

}

