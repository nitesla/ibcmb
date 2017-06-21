package longbridge.controllers.retail;

import longbridge.api.AccountDetails;
import longbridge.dtos.AccountDTO;
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
import longbridge.utils.statement.AccountBalance;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.PaginatedAccountStatement;
import longbridge.utils.statement.TransactionDetails;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
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
	private MessageSource messageSource;

	@Autowired
	private TransferService transferService;

	@Autowired
	AccountRepo accountRepo;

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
			model.addAttribute("failure", "Pls correct the errors");
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

		List<AccountDTO> accountList = accountService.getAccountsAndBalances(retailUser.getCustomerId());
		List<TransRequest> transRequestList = transferService
				.getLastTenTransactionsForAccount(account.getAccountNumber());
		if (transRequestList != null || !(transRequestList.equals("")) || !(transRequestList.isEmpty())) {
			model.addAttribute("transRequestList", transRequestList);
			model.addAttribute("accountList", accountList);
			System.out.println("what is the " + transRequestList);
			return "cust/account/accountstatement";
		}
		return "redirect:/retail/dashboard";
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
	public @ResponseBody DataTablesOutput<TransactionDetails> getStatementData(DataTablesInput input, String acctNumber,
			String fromDate, String toDate) {
		// Pageable pageable = DataTablesUtils.getPageable(input);

		Date from;
		Date to;
		DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
		try {
			from = dateFormat.parse(fromDate);
			to = dateFormat.parse(toDate);
			AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to);
			out.setDraw(input.getDraw());
			PaginatedAccountStatement paginatedAccountStatement = accountStatement.getPaginatedAccountStatement();
			List<TransactionDetails> list = new ArrayList<>();
			if (paginatedAccountStatement != null)
				list = paginatedAccountStatement.getTransactionDetails();
			out.setData(list);
			out.setRecordsFiltered(list.size());
			out.setRecordsTotal(list.size());
		} catch (ParseException e) {
			logger.warn("didn't parse date", e);
		}
		return out;

	}

	@GetMapping("/downloadstatement")
	public ModelAndView downloadStatementData(ModelMap modelMap, DataTablesInput input, String acctNumber,
			String fromDate, String toDate) {
		// Pageable pageable = DataTablesUtils.getPageable(input);

		Date from;
		Date to;
		DataTablesOutput<TransactionDetails> out = new DataTablesOutput<TransactionDetails>();
		try {
			from = dateFormat.parse(fromDate);
			to = dateFormat.parse(toDate);
			AccountStatement accountStatement = integrationService.getAccountStatements(acctNumber, from, to);
			out.setDraw(input.getDraw());
			PaginatedAccountStatement paginatedAccountStatement = accountStatement.getPaginatedAccountStatement();
			List<TransactionDetails> list = new ArrayList<>();
			if (paginatedAccountStatement != null)
				list = paginatedAccountStatement.getTransactionDetails();
			modelMap.put("datasource", list);
			modelMap.put("format", "pdf");
			modelMap.put("accountNum", acctNumber);
			modelMap.put("fromDate", fromDate);
			modelMap.put("toDate", toDate);
		} catch (ParseException e) {
			logger.warn("didn't parse date", e);
		}
		
		ModelAndView modelAndView = new ModelAndView("rpt_account-statement", modelMap);
		return modelAndView;

	}

	@GetMapping("/PrintReport/{account}")
	public ModelAndView getRpt2(ModelAndView modelAndView, @PathVariable Account account) {

		JasperReportsPdfView v = new JasperReportsPdfView();
		v.setUrl("classpath:pdf/accountStatement.jrxml");
		v.setApplicationContext(appContext);
		// v.setReportDataKey("datasource");

		AccountStatement accountStatement = integrationService.getAccountStatements(account.getAccountNumber(),
				new Date(), new Date());
		if (accountStatement != null || !(accountStatement.equals(""))) {
			List<TransactionDetails> transactionList = accountStatement.getPaginatedAccountStatement()
					.getTransactionDetails();
			AccountBalance accountBalance = accountStatement.getPaginatedAccountStatement().getAcctBal();
			// String sourceFileName ="classpath:pdf/accountStatement.jrxml";
			// JRBeanCollectionDataSource beanColDataSource = new
			// JRBeanCollectionDataSource(transactionList);

			Map<String, Object> parameterMap = new HashMap<>();
			/*
			 * parameterMap.put("AccountNum","10112332");
			 * parameterMap.put("date","01-08-2017");
			 */
			parameterMap.put("currencyCode", accountBalance.getCurrencyCode());
			parameterMap.put("accountNumber", accountBalance.getAccountNumber());
			parameterMap.put("accountNumber", accountBalance.getLedgerBalance());
			parameterMap.put("amount", accountBalance.getAvailableBalance().getAmountValue());
			for (TransactionDetails transaction : transactionList) {
				parameterMap.put("valueDate", transaction.getValueDate());
				parameterMap.put("valueDate", transaction.getValueDate());
				parameterMap.put("tranParticular", transaction.getTransactionSummary());
				parameterMap.put("tranParticular", transaction.getTransactionBalance());
				parameterMap.put("tranId", transaction.getTransactionId());
			}
			return new ModelAndView(v, parameterMap);
		}
		return null;
	}

}
