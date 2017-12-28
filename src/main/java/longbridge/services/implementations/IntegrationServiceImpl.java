package longbridge.services.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.api.*;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.exception.TransferExceptions;
import longbridge.models.Account;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.models.TransRequest;
import longbridge.models.User;
import longbridge.repositories.AccountRepo;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.AccountService;
import longbridge.services.ConfigurationService;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import longbridge.utils.ResultType;
import longbridge.utils.StatusCode;
import longbridge.utils.TransferType;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Created by Fortune on 4/4/2017. Modified by Farooq
 */

@Service
public class IntegrationServiceImpl implements IntegrationService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private Locale locale = LocaleContextHolder.getLocale();

	@Value("${ebank.service.uri}")
	private String URI;
	@Value("${CMB.ALERT.URL}")
	private String cmbAlert;

	private RestTemplate template;
	private MailService mailService;
	private TemplateEngine templateEngine;
	private ConfigurationService configService;
	private TransferErrorService errorService;
	private MessageSource messageSource;
	private AccountRepo accountRepo;

	@Autowired
	public IntegrationServiceImpl(RestTemplate template, MailService mailService, TemplateEngine templateEngine,
			ConfigurationService configService, TransferErrorService errorService, MessageSource messageSource,
			AccountRepo accountRepo) {
		this.template = template;
		this.mailService = mailService;
		this.templateEngine = templateEngine;
		this.configService = configService;
		this.errorService = errorService;
		this.messageSource = messageSource;
		this.accountRepo = accountRepo;

	}

	@Override
	public AccountInfo fetchAccount(String accountNumber) {
		// TODO
		AccountInfo accountInfo = new AccountInfo();
		return accountInfo;
	}

	@Override
	public List<AccountInfo> fetchAccounts(String cifid) {
		try {

			String uri = URI + "/customer/{acctId}/accounts";
			return Arrays.stream(template.getForObject(uri, AccountInfo[].class, cifid.toUpperCase()))
					.collect(Collectors.toList());
			// List list= template.getForObject(uri, ArrayList.class,cifid);

		} catch (Exception e) {
			logger.error("Exception occurred ", e);
			return new ArrayList<>();
		}
	}

	@Override
	public List<ExchangeRate> getExchangeRate() {
		try {

			String uri = URI + "/forex";
			return Arrays.stream(template.getForObject(uri, ExchangeRate[].class)).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Exception occurred ", e);
			return new ArrayList<>();
		}
	}

	@Override
	public AccountStatement getAccountStatements(String accountNo, Date fromDate, Date toDate, String tranType,
			String numOfTxn, PaginationDetails paginationDetails) {
		AccountStatement statement = new AccountStatement();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			/*
			 * String dateInString = "7-Jun-2013"; Date date =
			 * formatter.parse(dateInString);
			 */
			String uri = URI + "/statement";
			Map<String, Object> params = new HashMap<>();
			params.put("accountNumber", accountNo);
			params.put("fromDate", formatter.format(fromDate));
			params.put("solId", viewAccountDetails(accountNo).getSolId());
			if (tranType != null)
				params.put("tranType", tranType);
			if (toDate != null)
				params.put("toDate", formatter.format(toDate));
			if (paginationDetails != null)
				params.put("paginationDetails", paginationDetails);

			params.put("numOfTxn", numOfTxn);
			statement = template.postForObject(uri, params, AccountStatement.class);

		} catch (Exception e) {
			logger.error("Error occurred getting account statements", e);
		}

		return statement;
	}

	@Override
	public AccountStatement getAccountStatements(String accountNo, Date fromDate, Date toDate, String tranType,
			String numOfTxn) {
		AccountStatement statement = new AccountStatement();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			/*
			 * String dateInString = "7-Jun-2013"; Date date =
			 * formatter.parse(dateInString);
			 */
			String uri = URI + "/statement";
			Map<String, Object> params = new HashMap<>();
			params.put("accountNumber", accountNo);
			params.put("fromDate", formatter.format(fromDate));
			params.put("solId", viewAccountDetails(accountNo).getSolId());
			if (tranType != null)
				params.put("tranType", tranType);
			if (toDate != null)
				params.put("toDate", formatter.format(toDate));
			params.put("numOfTxn", numOfTxn);

			logger.info("params {}", params);
			statement = template.postForObject(uri, params, AccountStatement.class);

		} catch (Exception e) {
			logger.error("Error occurred getting account statements", e);
		}

		return statement;
	}

	@Override
	public AccountStatement getFullAccountStatement(String accountNo, Date fromDate, Date toDate, String tranType) {
		AccountStatement statement = new AccountStatement();
		//TODO: Move to account service
		validate(accountNo);
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			/*
			 * String dateInString = "7-Jun-2013"; Date date =
			 * formatter.parse(dateInString);
			 */
			String uri = URI + "/fullstatement";
			Map<String, Object> params = new HashMap<>();
			params.put("accountNumber", accountNo);
			params.put("fromDate", formatter.format(fromDate));
			params.put("solId", viewAccountDetails(accountNo).getSolId());
			if (tranType != null)
				params.put("tranType", tranType);
			if (toDate != null)
				params.put("toDate", formatter.format(toDate));

			statement = template.postForObject(uri, params, AccountStatement.class);

		} catch (Exception e) {
			logger.error("Error occurred getting account statements", e);
		}

		return statement;
	}

	@Override
	public AccountStatement getTransactionHistory(String accountNo, Date fromDate, Date toDate, String tranType) {
		AccountStatement statement = new AccountStatement();
		//TODO: Move to account service
				validate(accountNo);
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			/*
			 * String dateInString = "7-Jun-2013"; Date date =
			 * formatter.parse(dateInString);
			 */
			String uri = URI + "/tranhistory";
			Map<String, Object> params = new HashMap<>();
			params.put("accountNumber", accountNo);
			params.put("fromDate", formatter.format(fromDate));
			params.put("solId", viewAccountDetails(accountNo).getSolId());
			if (tranType != null)
				params.put("tranType", tranType);
			if (toDate != null)
				params.put("toDate", formatter.format(toDate));

			statement = template.postForObject(uri, params, AccountStatement.class);

		} catch (Exception e) {
			logger.error("Error occurred getting account statements", e);
		}

		return statement;
	}

	@Override
	public List<TransactionHistory> getLastNTransactions(String accountNo, String numberOfRecords) {
		List<TransactionHistory> histories = new ArrayList<>();
		//TODO: To be moved to account service.
		validate(accountNo);
		try {

			String uri = URI + "/transactions";
			Map<String, String> params = new HashMap<>();
			params.put("accountNumber", accountNo);
			params.put("numberOfRecords", numberOfRecords);
			params.put("branchId", viewAccountDetails(accountNo).getSolId());

			TransactionHistory[] t = template.postForObject(uri, params, TransactionHistory[].class);
			histories.addAll(Arrays.asList(t));

		} catch (Exception e) {
			logger.error("Error occurred getting last transactions", e);
			throw e;
		}
		return histories;
	}

	@Override
	public Map<String, BigDecimal> getBalance(String accountId) {

		Map<String, BigDecimal> response = new HashMap<>();
		try {
			AccountDetails details = viewAccountDetails(accountId);

			if (details != null && details.getAvailableBalance() != null) {
				BigDecimal availBal = new BigDecimal(details.getAvailableBalance());
				BigDecimal ledgBal = new BigDecimal(details.getLedgerBalAmt());
				response.put("AvailableBalance", availBal);
				response.put("LedgerBalance", ledgBal);
				return response;
			}
		} catch (Exception e) {
			logger.error("Error occurred getting balance", e);
		}
		response.put("AvailableBalance", new BigDecimal(0));
		response.put("LedgerBalance", new BigDecimal(0));
		return response;

	}

	@Override
	public TransRequest makeTransfer(TransRequest transRequest) throws InternetBankingTransferException {

		TransferType type = transRequest.getTransferType();
		Account account = accountRepo.findFirstByAccountNumber(transRequest.getCustomerAccountNumber());
		validate(account);
		// switch (type) {
		switch (type) {
		case CORONATION_BANK_TRANSFER:

		{
			transRequest.setTransferType(TransferType.CORONATION_BANK_TRANSFER);
			TransferDetails response;
			String uri = URI + "/transfer/local";
			Map<String, String> params = new HashMap<>();
			params.put("debitAccountNumber", transRequest.getCustomerAccountNumber());
			params.put("debitAccountName", account.getAccountName());
			params.put("creditAccountNumber", transRequest.getBeneficiaryAccountNumber());
			params.put("creditAccountName", transRequest.getBeneficiaryAccountName());
			params.put("tranAmount", transRequest.getAmount().toString());
			params.put("remarks", transRequest.getRemarks());
			logger.info("Starting Transfer with Params: {}", params.toString());

			try {
				response = template.postForObject(uri, params, TransferDetails.class);
				transRequest.setStatus(response.getResponseCode());
				transRequest.setStatusDescription(response.getResponseDescription());
				transRequest.setReferenceNumber(response.getUniqueReferenceCode());
				transRequest.setNarration(response.getNarration());
				return transRequest;

			} catch (HttpStatusCodeException e) {
				logger.error("HTTP Error occurred", e);
				transRequest.setStatus(e.getStatusCode().toString());
				transRequest.setStatusDescription(e.getStatusCode().getReasonPhrase());
				return transRequest;
			} catch (Exception e) {
				logger.error("Error occurred making transfer", e);
				transRequest.setStatus(StatusCode.FAILED.toString());
				transRequest.setStatusDescription(messageSource.getMessage("status.code.failed", null, locale));
				return transRequest;
			}

		}
		case INTER_BANK_TRANSFER: {
			transRequest.setTransferType(TransferType.INTER_BANK_TRANSFER);
			TransferDetails response;
			String uri = URI + "/transfer/nip";
			Map<String, String> params = new HashMap<>();
			params.put("debitAccountNumber", transRequest.getCustomerAccountNumber());
			params.put("debitAccountName", account.getAccountName());
			params.put("creditAccountNumber", transRequest.getBeneficiaryAccountNumber());
			params.put("creditAccountName", transRequest.getBeneficiaryAccountName());
			params.put("tranAmount", transRequest.getAmount().toString());
			params.put("destinationInstitutionCode", transRequest.getFinancialInstitution().getInstitutionCode());
			params.put("tranType", "NIP");
			params.put("remarks", transRequest.getRemarks());

			logger.info("params for transfer {}", params.toString());
			try {
				response = template.postForObject(uri, params, TransferDetails.class);
				logger.info("response for transfer {}", response.toString());
				transRequest.setReferenceNumber(response.getUniqueReferenceCode());
				transRequest.setNarration(response.getNarration());
				transRequest.setStatus(response.getResponseCode());
				transRequest.setStatusDescription(response.getResponseDescription());

				return transRequest;
			} catch (HttpStatusCodeException e) {

				logger.error("HTTP Error occurred", e);
				transRequest.setStatus(e.getStatusCode().toString());
				transRequest.setStatusDescription(e.getStatusCode().getReasonPhrase());
				return transRequest;

			} catch (Exception e) {
				logger.error("Error occurred making transfer", e);
				transRequest.setStatus(StatusCode.FAILED.toString());
				transRequest.setStatusDescription(messageSource.getMessage("status.code.failed", null, locale));
				return transRequest;
			}

		}
		case INTERNATIONAL_TRANSFER: {

		}

		case OWN_ACCOUNT_TRANSFER: {
			transRequest.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
			TransferDetails response;
			String uri = URI + "/transfer/local";
			Map<String, String> params = new HashMap<>();
			params.put("debitAccountNumber", transRequest.getCustomerAccountNumber());
			params.put("debitAccountName", account.getAccountName());
			params.put("creditAccountNumber", transRequest.getBeneficiaryAccountNumber());
			params.put("creditAccountName", transRequest.getBeneficiaryAccountName());
			params.put("tranAmount", transRequest.getAmount().toString());
			params.put("remarks", transRequest.getRemarks());
			logger.info("params for transfer {}", params.toString());
			try {
				response = template.postForObject(uri, params, TransferDetails.class);
				transRequest.setNarration(response.getNarration());
				transRequest.setReferenceNumber(response.getUniqueReferenceCode());
				transRequest.setStatus(response.getResponseCode());
				transRequest.setStatusDescription(response.getResponseDescription());
				return transRequest;

			} catch (HttpStatusCodeException e) {

				logger.error("HTTP Error occurred", e);
				transRequest.setStatus(e.getStatusCode().toString());
				transRequest.setStatusDescription(e.getStatusCode().getReasonPhrase());
				return transRequest;

			} catch (Exception e) {
				logger.error("Error occurred making transfer", e);
				transRequest.setStatus(StatusCode.FAILED.toString());
				transRequest.setStatusDescription(messageSource.getMessage("status.code.failed", null, locale));
				return transRequest;
			}

		}

		case RTGS: {
			TransRequest request = sendTransfer(transRequest);

			return request;
		}
		}
		logger.trace("request did not match any type");
		transRequest.setStatus(ResultType.ERROR.toString());
		return transRequest;
	}

	@Override
	public TransferDetails makeNapsTransfer(Naps naps) throws InternetBankingTransferException {
		String uri = URI + "/transfer/naps";

		try {
			TransferDetails details = template.getForObject(uri, TransferDetails.class, naps);
			return details;
		} catch (Exception e) {
			logger.error("Error making NAPS transfer", e);
			return new TransferDetails();
		}

	}

	@Override
	public AccountDetails viewAccountDetails(String acctNo) {

		String uri = URI + "/account/{acctNo}";
		//validate(acctNo);
		Map<String, String> params = new HashMap<>();
		params.put("acctNo", acctNo);
		try {
			AccountDetails details = template.getForObject(uri, AccountDetails.class, params);
			return details;
		} catch (Exception e) {
			logger.error("Error getting account details for {}", acctNo);
			return new AccountDetails();
		}

	}

	@Override
	public CustomerDetails isAccountValid(String accNo, String email, String dob) {
		CustomerDetails result = new CustomerDetails();
		String uri = URI + "/account/verification";
		Map<String, String> params = new HashMap<>();
		params.put("accountNumber", accNo);
		params.put("email", email);
		params.put("dateOfBirth", dob);
		try {
			result = template.postForObject(uri, params, CustomerDetails.class);

		} catch (Exception e) {
			logger.error("Error occurred validating account", e);
		}

		return result;

	}

	@Override
	public CustomerDetails viewCustomerDetails(String accNo) {
		CustomerDetails result = new CustomerDetails();
		String uri = URI + "/customer/{cifId}";
		String cifId = viewAccountDetails(accNo).getCustId();
		Map<String, String> params = new HashMap<>();
		params.put("cifId", cifId);
		try {
			result = template.getForObject(uri, CustomerDetails.class, params);
			return result;
		} catch (Exception e) {
			logger.error("Error occurred getting customer details", e);
		}

		return result;
	}

	@Override
	public CustomerDetails viewCustomerDetailsByCif(String cifId) {
		CustomerDetails result = new CustomerDetails();
		String uri = URI + "/customer/{cifId}";
		Map<String, String> params = new HashMap<>();
		params.put("cifId", cifId);
		try {
			result = template.getForObject(uri, CustomerDetails.class, params);
			return result;
		} catch (Exception e) {
			logger.error("Error occurred getting customer details", e);
		}
		return result;
	}

	@Override
	public String getAccountName(String accountNumber) {
		logger.info(accountNumber + "account number");

		return viewAccountDetails(accountNumber).getAcctName();

	}

	@Override
	public BigDecimal getDailyDebitTransaction(String acctNo) {

		BigDecimal result = new BigDecimal(0);
		String uri = URI + "/transfer/dailyTransaction";
		Map<String, String> params = new HashMap<>();
		params.put("accountNumber", acctNo);

		try {
			String response = template.postForObject(uri, params, String.class);
			result = new BigDecimal(response);
		} catch (Exception e) {

			logger.error("Error occurred getting  daily transaction", e);
		}

		return result;

	}

	@Override
	public String getDailyAccountLimit(String accNo, String channel) {
		String result = "NAN";
		String uri = URI + "/transfer/limit";
		Map<String, String> params = new HashMap<>();
		params.put("accountNumber", accNo);
		params.put("transactionChannel", channel);
		try {
			String response = template.postForObject(uri, params, String.class);
			result = (response);
		} catch (Exception e) {

			logger.error("Error occurred getting  daily account limit", e);
		}

		return result;

	}

	@Override
	public NEnquiryDetails doNameEnquiry(String destinationInstitutionCode, String accountNumber) {
		NEnquiryDetails result = new NEnquiryDetails();
		String uri = URI + "/transfer/nameEnquiry";
		Map<String, String> params = new HashMap<>();
		params.put("destinationInstitutionCode", destinationInstitutionCode);
		params.put("accountNumber", accountNumber);
		logger.trace("params {}", params);
		try {

			result = template.postForObject(uri, params, NEnquiryDetails.class);

		} catch (Exception e) {
			logger.error("Error occurred doing name enquiry", e);
		}

		return result;
	}

	@Override
	public BigDecimal getAvailableBalance(String s) {
		try {
			Map<String, BigDecimal> getBalance = getBalance(s);
			BigDecimal balance = getBalance.get("AvailableBalance");
			if (balance != null) {
				return balance;
			}
		} catch (Exception e) {
			logger.error("Error occurred getting available balance", e);
		}
		return new BigDecimal(0);
	}

	@Override
	@Async
	public CompletableFuture<ObjectNode> sendSMS(String message, String contact, String subject) {
		List<String> contacts = new ArrayList<>();
		contacts.add(contact);
		ObjectNode result = null;
		String uri = cmbAlert;
		Map<String, Object> params = new HashMap<>();
		params.put("alertType", "SMS");
		params.put("message", message);
		params.put("subject", subject);
		params.put("contactList", contacts);
		logger.trace("params {}", params);

		try {

			result = template.postForObject(uri, params, ObjectNode.class);
		} catch (Exception e) {
			logger.error(uri, params, e);

		}

		return CompletableFuture.completedFuture(result);
	}

	@Override
	// @Async
	public Rate getFee(String channel) {

		String uri = URI + "/transfer/fee";
		Map<String, String> params = new HashMap<>();
		params.put("transactionChannel", channel);
		try {
			Rate details = template.postForObject(uri, params, Rate.class);
			return details;
		} catch (Exception e) {

			logger.error("Error occurred getting  fee", e);

			return new Rate("", "0", "");
		}

	}

	public TransRequest sendTransfer(TransRequest transRequest) {

		try {

			Context scontext = new Context();
			scontext.setVariable("transRequest", transRequest);
			String recipient = "";

			String mail = templateEngine.process("/cust/transfer/mailtemplate", scontext);
			SettingDTO setting = configService.getSettingByName("BACK_OFFICE_EMAIL");
			if ((setting.isEnabled())) {
				recipient = setting.getValue();
			}

			mailService.send(recipient, transRequest.getTransferType().toString(), mail);
			transRequest.setStatus("000");
			transRequest.setStatus("Approved or completed successfully");
		} catch (Exception e) {

			logger.error("Exception occurred {}", e);
			transRequest.setStatus("96");
			transRequest.setStatusDescription("TRANSACTION FAILED");
		}

		return transRequest;
	}

	private void validate(Account account) {
		validate(account.getAccountNumber());
	}

	private void validate(String account) {
		User currentUser = getCurrentUser();
		switch (currentUser.getUserType()) {
		case RETAIL: {
			RetailUser user = (RetailUser) currentUser;
			Account acct = accountRepo.findFirstByAccountNumber(account);
			if (acct == null || acct.getCustomerId() == null) {
				throw new InternetBankingException("Access Denied");
			} else if (!acct.getCustomerId().equals(user.getCustomerId())) {
				logger.warn("User " + user.toString() + "trying to access other accounts");
				throw new InternetBankingException("Access Denied");
			}
		}
			break;
		case CORPORATE: {
			CorporateUser user = (CorporateUser) currentUser;
			Account acct = accountRepo.findFirstByAccountNumber(account);
			boolean valid = user.getCorporate().getAccounts().contains(acct);
			if (!valid) {
				logger.warn("User " + user.toString() + "trying to access other accounts");
				throw new InternetBankingException("Access Denied");
			}
		}
			break;
		default: {
			logger.warn("Internal User " + currentUser.toString() + "trying to access accounts");
			throw new InternetBankingException("Access Denied");
		}
		}
	}

	private User getCurrentUser() {
		CustomUserPrincipal principal = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		return principal.getUser();
	}

}
