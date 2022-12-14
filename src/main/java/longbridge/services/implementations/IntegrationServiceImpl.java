package longbridge.services.implementations;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.api.*;
import longbridge.dtos.*;
import longbridge.dtos.apidtos.NeftResponseDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.exception.TransferErrorService;
import longbridge.models.*;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.AntiFraudRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.NeftTransferRepo;
import longbridge.response.*;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.IntegrationService;
import longbridge.services.MailService;
import longbridge.services.SettingsService;
import longbridge.utils.*;
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

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Locale locale = LocaleContextHolder.getLocale();

	@Value("${ebank.service.uri}")
	private String URI;

	@Value("${alert.url}")
	private String alertsSystem;

    @Value("${neft.settlement}")
    private String NEFTURI;

	@Value("${quickteller.service.uri}")
	private String QUICKTELLER_URI;

	@Value("${custom.duty.remark")
	private String paymentRemark;

	@Value("${limit.charge.uri")
	private String limitChargeUrl;

	@Value("${get.billers.quickteller}")
	private String quicktellerBillers;

	@Value("${get.categories.quickteller}")
	private String quicktellerCategories;

	@Value("${get.paymentitem.quickteller}")
	private String quicktellerPaymentItems;

	@Value("${billpayment.advice}")
	private String quicktellerBillpaymentAdvice;

	@Value("${quickteller.querytransaction}")
	private String quicktellerQueryTransaction;

	@Value("${quickteller.fundtransfer}")
	private String quicktellerFundtransfer;

	@Value("${quickteller.nameEnquiry}")
	private String quicktellerNameEnquiry;

	@Value("${quickteller.bankcodes}")
	private String getBankCodes;

	@Value("${neft.appId}")
	private String appId;

	@Value("${quick.initiatingEntityCode}")
	private String initiatingEntityCode;

    @Value("${quickteller.appId}")
	private String appIdQuickteller;

    @Value("${quickteller.secretKey}")
    private String secretKeyQuickteller;

	@Value("${billPayment.terminalId}")
	private String terminalId;

	@Value("${custom.secretKey}")
	private String secretKey;
/*
	@Value("${custom.beneficiaryAcct}")
	private String beneficiaryAcct;*/

	@Value("${customDuty.baseUrl}")
	private String CustomDutyUrl;

	@Value("${bank.code}")
	private String bankcode;

//	@Value("${custom.access.beneficiaryAcct}")
//	private String accessBeneficiaryAcct;

	@Value("${antifraud.status.check}")
	private String antiFraudStatusCheckUrl;

	private final RestTemplate template;
	private final MailService mailService;
	private final TemplateEngine templateEngine;
	private final SettingsService configService;
	private final TransferErrorService errorService;
	private final MessageSource messageSource;
	private final AccountRepo accountRepo;
	private final CorporateRepo corporateRepo;
	private final AntiFraudRepo antiFraudRepo;
    private final NeftTransferRepo neftTransferRepo;


	@Autowired
	public IntegrationServiceImpl(RestTemplate template, MailService mailService, TemplateEngine templateEngine,
								  SettingsService configService, TransferErrorService errorService, MessageSource messageSource,
								  AccountRepo accountRepo, CorporateRepo corporateRepo, AntiFraudRepo antiFraudRepo,
								  NeftTransferRepo neftTransferRepo) {
		this.template = template;
		this.mailService = mailService;
		this.templateEngine = templateEngine;
		this.configService = configService;
		this.errorService = errorService;
		this.messageSource = messageSource;
		this.accountRepo = accountRepo;
		this.corporateRepo = corporateRepo;
		this.antiFraudRepo=antiFraudRepo;
        this.neftTransferRepo = neftTransferRepo;
	}


	@Override
	public List<AccountInfo> fetchAccounts(String cifid) {

		List<AccountInfo> accounts;
		try {
			logger.info("Fetching accounts with CIFID {}",cifid);
			String uri = URI + "/customer/"+cifid+"/accounts";
			accounts = Arrays.stream(template.getForObject(uri, AccountInfo[].class, cifid.toUpperCase()))
					.collect(Collectors.toList());
			logger.debug("Accounts fetched {}", accounts.toString());
			logger.info("Successfully fetched accounts with CIFID {}",cifid);

			return accounts;

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
		validate(accountNo);
		try {
			logger.info("Fetching account statement with account number {}",accountNo);

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

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
		logger.info("Successfully fetched account statement with account number {}",accountNo);

		return statement;
	}



	@Override
	public AccountStatement getAccountStatements(String accountNo, Date fromDate, Date toDate, String tranType,
												 String numOfTxn) {
		validate(accountNo);
		AccountStatement statement = new AccountStatement();
		try {


			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

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

			logger.debug("statement params {}", params);
			logger.info("Fetching account statement with account number {}",accountNo);

			statement = template.postForObject(uri, params, AccountStatement.class);
			logger.info("Successfully fetched account statement with account number {}",accountNo);

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

			logger.info("Fetching account statement with account number {}",accountNo);

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");

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
			logger.info("Successfully fetched account statement with account number {}",accountNo);

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

			String uri = URI + "/tranhistory";
			Map<String, Object> params = new HashMap<>();
			params.put("accountNumber", accountNo);
			params.put("fromDate", formatter.format(fromDate));
			params.put("solId", viewAccountDetails(accountNo).getSolId());
			if (tranType != null)
				params.put("tranType", tranType);
			if (toDate != null)
				params.put("toDate", formatter.format(toDate));

			logger.info("Fetching account statement with account number {}",accountNo);

			statement = template.postForObject(uri, params, AccountStatement.class);

			logger.info("Successfully fetched account statement with account number {}",accountNo);

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

			logger.info("Fetching last N transactions with account number {}",accountNo);

			TransactionHistory[] t = template.postForObject(uri, params, TransactionHistory[].class);
			histories.addAll(Arrays.asList(t));

			logger.info("Successfully fetched last N transactions with account number {}",accountNo);

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

	public TransRequest makeCustomDutyPayment(TransRequest transRequest) throws InternetBankingTransferException {
		TransferType type = transRequest.getTransferType();
		Account account = accountRepo.findFirstByAccountNumber(transRequest.getCustomerAccountNumber());
		validate(account);
		TransferDetails response = null;
		String uri = URI + "/transfer/local";
		Map<String, String> params = new HashMap<>();
		params.put("debitAccountNumber", transRequest.getCustomerAccountNumber());
		params.put("debitAccountName", account.getAccountName());
		params.put("creditAccountNumber", transRequest.getBeneficiaryAccountNumber());
		params.put("creditAccountName", transRequest.getBeneficiaryAccountName());
		params.put("tranAmount", transRequest.getAmount().toString());
		params.put("remarks", transRequest.getRemarks());
		params.put("tranType", type.toString());
		logger.info("Starting Transfer with Params: {}", params.toString());

		try {
			logger.info("Initiating Local Transfer");
			logger.debug("Transfer Params: {}", params.toString());
			response = template.postForObject(uri, params, TransferDetails.class);
			logger.info("Response: {}",response);
			transRequest.setStatus(response.getResponseCode());
			transRequest.setStatusDescription(response.getResponseDescription());
			transRequest.setReferenceNumber(response.getUniqueReferenceCode());
			transRequest.setNarration(response.getNarration());
			return transRequest;
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
			transRequest.setStatus(StatusCode.FAILED.getStatusCode());
			transRequest.setTransferType(TransferType.CUSTOM_DUTY);
			transRequest.setStatusDescription(StatusCode.FAILED.toString());
			return transRequest;
		}
		catch (Exception e) {
			transRequest.setStatus(StatusCode.FAILED.toString());
			transRequest.setTransferType(TransferType.CUSTOM_DUTY);
			transRequest.setStatusDescription(StatusCode.FAILED.toString());
			return transRequest;
		}
	}

	@Override
	public TransRequest makeTransfer(TransRequest transRequest) throws InternetBankingTransferException {
//		logger.info("chanto {}",transRequest);
		TransferType type = transRequest.getTransferType();
		Account account = accountRepo.findFirstByAccountNumber(transRequest.getCustomerAccountNumber());
		validate(account);
		switch (type) {
			case WITHIN_BANK_TRANSFER:
			{
				transRequest.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
				String uri = URI + "/transfer/local";
				try {


					TransferRequest params=new TransferRequest();
					params.setDebitAccountNumber(transRequest.getCustomerAccountNumber());
					params.setDebitAccountName( account.getAccountName());
					params.setCreditAccountNumber(transRequest.getBeneficiaryAccountNumber());
					params.setCreditAccountName(transRequest.getBeneficiaryAccountName());
					params.setTranAmount(transRequest.getAmount().toString());
					params.setRemarks(transRequest.getRemarks());
					params.setAntiFraudData(transRequest.getAntiFraudData());
//					params.setTranType(type.toString());

					logger.info("Starting Transfer with Params: {}", params.toString());
					logger.info("Initiating Local Transfer");
					logger.debug("Transfer Params: {}", params.toString());
					TransferRequest response = template.postForObject(uri, params, TransferRequest.class);

					params.getAntiFraudData().setTranRequestId(transRequest.getId());
					antiFraudRepo.save(params.getAntiFraudData());
					logger.info("AntiFraud data saved {}",params.getAntiFraudData());

//					response = template.postForObject(uri, params, TransferDetails.class);
					logger.info("Response:{}",response);
					transRequest.setStatus(response.getResponseCode());
					transRequest.setStatusDescription(response.getResponseDescription());
					transRequest.setReferenceNumber(response.getUniqueReferenceCode());
					transRequest.setNarration(response.getNaration());
					return transRequest;
				} catch (HttpStatusCodeException e) {
					logger.error("HTTP Error occurred", e);
					transRequest.setStatus(e.getStatusCode().toString());
					transRequest.setStatusDescription(e.getStatusCode().getReasonPhrase());
					return transRequest;
				}
				catch (Exception e) {
//					String reversalUrl = "http://132.10.200.140:9292/service/reverseFundTransfer?uniqueIdentifier=";
//					logger.error("Error occurred making transfer", e);
					transRequest.setStatus(StatusCode.FAILED.toString());
					transRequest.setStatusDescription(messageSource.getMessage("status.code.failed", null, locale));
					//template.postForObject(reversalUrl+response.getUniqueReferenceCode(), params, TransferDetails.class);
					return transRequest;
				}


			}
			case INTER_BANK_TRANSFER: {
				transRequest.setTransferType(TransferType.INTER_BANK_TRANSFER);
//				TransferDetails response;
				TransferRequestNip response = new TransferRequestNip();
				String uri = URI + "/transfer/nip";

				TransferRequestNip params=new TransferRequestNip();
				params.setOriginatorAccountNumber(transRequest.getCustomerAccountNumber());
				params.setOriginatorAccountName( account.getAccountName());
				params.setBeneficiaryAccountNumber(transRequest.getBeneficiaryAccountNumber());
				params.setBeneficiaryAccountName(transRequest.getBeneficiaryAccountName());
				params.setAmount(transRequest.getAmount().toString());
				params.setDestinationInstitutionCode(transRequest.getFinancialInstitution().getInstitutionCode());
				params.setTranType("NIP");
				params.setRemarks(transRequest.getRemarks());
				params.setAntiFraudData(transRequest.getAntiFraudData());
				params.setUniqueReferenceCode(transRequest.getReferenceNumber());

				try {
					logger.info("Initiating Inter Bank Transfer");
					logger.debug("Transfer Params: {}", params.toString());
  					response = template.postForObject(uri, params, TransferRequestNip.class);
					transRequest.setReferenceNumber(response.getUniqueReferenceCode());
					transRequest.setStatus(response.getResponseCode());
					transRequest.setStatusDescription(response.getResponseDescription());
					transRequest.setNarration(response.getNarration());
					params.getAntiFraudData().setTranRequestId(transRequest.getId());

					logger.info("response for transfer {}", response.toString());
					logger.info("response Description {} ", response.getResponseDescription());
					if(response.isStatusNull()){

						transRequest.setStatusDescription(errorService.getMessage(response.getResponseCode()));
						logger.info("response code {}",transRequest.getStatusDescription());

					}

					antiFraudRepo.save(params.getAntiFraudData());
					logger.info("AntiFraud data saved {}", params.getAntiFraudData());


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
                return sendInternationalTransferRequest(transRequest);
			}

			case OWN_ACCOUNT_TRANSFER: {
				transRequest.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);
				TransferDetails response;
				String uri = URI + "/transfer/local";


				TransferRequest params=new TransferRequest();
				params.setDebitAccountNumber(transRequest.getCustomerAccountNumber());
				params.setDebitAccountName( account.getAccountName());
				params.setCreditAccountNumber(transRequest.getBeneficiaryAccountNumber());
				params.setCreditAccountName(transRequest.getBeneficiaryAccountName());
				params.setTranAmount(transRequest.getAmount().toString());
				params.setRemarks(transRequest.getRemarks());
				params.setCurrencyCode(transRequest.getCurrencyCode());
				params.setAntiFraudData(transRequest.getAntiFraudData());


				logger.info("params for transfer {}", params.toString());
				try {
					logger.info("Initiating Local (Own Account) Transfer");
					logger.debug("Transfer Params: {}", params.toString());

					response = template.postForObject(uri, params, TransferDetails.class);
					transRequest.setNarration(response.getNarration());
					transRequest.setReferenceNumber(response.getUniqueReferenceCode());
					transRequest.setStatus(response.getResponseCode());
					transRequest.setStatusDescription(response.getResponseDescription());

					params.getAntiFraudData().setTranRequestId(transRequest.getId());
					antiFraudRepo.save(params.getAntiFraudData());
					logger.info("AntiFraud data saved {}",params.getAntiFraudData());

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

                return sendTransfer(transRequest);
			}
//			case NEFT: {
//				NeftResponse neftTransferRequest = submitNeftTransfer();
////				return neftTransferRequest;
//			}
			case QUICKTELLER: {

				QuickDetails response;
				String uri = QUICKTELLER_URI + quicktellerFundtransfer;
				Map<String,Object> params = new HashMap<>();
				String macCode = EncryptionUtil.getSHA512(transRequest.getQuickInitiation().getAmount() +
						transRequest.getQuickInitiation().getCurrencyCode() +
						transRequest.getQuickInitiation().getPaymentMethodCode() +
						transRequest.getQuickTermination().getAmount() +
						transRequest.getQuickTermination().getCurrencyCode() +
						transRequest.getQuickTermination().getPaymentMethodCode() +
						transRequest.getQuickTermination().getCountryCode(), null);
				String hashCode = EncryptionUtil.getSHA512(appIdQuickteller +
						transRequest.getTransferCode() +
						secretKeyQuickteller, null);
				params.put("appid", appIdQuickteller);
				params.put("beneficiary", transRequest.getQuickBeneficiary());
				params.put("customerAccount", transRequest.getCustomerAccountNumber());
				params.put("hash", hashCode);
				params.put("initiatingEntityCode", initiatingEntityCode);
				params.put("initiation", transRequest.getQuickInitiation());
				params.put("mac", macCode);
				params.put("sender", transRequest.getQuickSender());
				params.put("termination", transRequest.getQuickTermination());
				params.put("transferCode", transRequest.getTransferCode());


				logger.info("params for transfer {}", params.toString());
				try {
					logger.info("Initiating Quickteller Transfer");
					logger.debug("Transfer Params: {}", params.toString());

					response = template.postForObject(uri, params, QuickDetails.class);
					logger.info("Response is: {}", response);
					transRequest.setResponseCode(response.getResponseCode());
					transRequest.setMac(response.getMac());
					transRequest.setTransferCode(response.getTransferCode());
					transRequest.setTransactionDate(response.getTransactionDate());
					transRequest.setResponseCodeGrouping(response.getResponseCodeGrouping());
					transRequest.setStatusDescription("Completed");

					if(response.getResponseCodeGrouping().contains("SUCCESS")){
						transRequest.setStatus("00");
					}

					logger.info("I'm here {}", transRequest);

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
		}
		logger.trace("request did not match any type");
		transRequest.setStatus(ResultType.ERROR.toString());
		return transRequest;
	}

	@Override
	public TransRequest checkQuicktellerTransaction(TransRequest transRequest){


		PaymentResponse checkTransaction;
		String uri = QUICKTELLER_URI+quicktellerQueryTransaction;

		logger.info("Query Transaction url is {}", uri);
		Map<String, String> request = new HashMap<>();
		request.put("hash", EncryptionUtil.getSHA512(
				appIdQuickteller + transRequest.getTransferCode() + secretKeyQuickteller, null));
		request.put("appid", appIdQuickteller);
		request.put("requestReference", transRequest.getTransferCode());
		logger.debug("Fetching data from Quickteller Query Transaction rest service using: {}", request);
		try {
			checkTransaction = template.postForObject(uri, request, PaymentResponse.class);

			logger.info("the query transaction response is {}", checkTransaction);

			if (checkTransaction.getResponseDescription() != null) {
                transRequest.setStatusDescription(checkTransaction.getResponseDescription());
			}
            transRequest.setResponseCode(checkTransaction.getResponseCode());
            if(checkTransaction.getTransactionResponseCode().equalsIgnoreCase("90000")){
                transRequest.setStatus("00");
            }

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



	@Override
	public TransRequest makeBackgroundTransfer(TransRequest transRequest) throws InternetBankingTransferException{
		TransferType type = transRequest.getTransferType();

		Account account = accountRepo.findFirstByAccountNumber(transRequest.getCustomerAccountNumber());
		switch (type) {
			case WITHIN_BANK_TRANSFER:

			{
				transRequest.setTransferType(TransferType.WITHIN_BANK_TRANSFER);
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

                return sendRTGSTransferRequest(transRequest);
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
            return template.getForObject(uri, TransferDetails.class, naps);
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
            return template.getForObject(uri, AccountDetails.class, params);
		} catch (Exception e) {
			logger.error("Error getting account details for {}", acctNo,e);
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
			logger.info("Fetching customer details with account number {}",accNo);

			result = template.getForObject(uri, CustomerDetails.class, params);

			logger.debug("Customer details {}",result.toString());
			logger.info("Successfully fetched customer details with account number {}",accNo);

			return result;
		} catch (Exception e) {
			logger.error("Error occurred fetching customer details", e);
		}

		return result;
	}

	@Override
	public CustomerDetails viewRetailCustomerDetailsByCif(String cifId) {
		CustomerDetails result = new CustomerDetails();
		String uri = URI + "/retail/customer/{cifId}";
		Map<String, String> params = new HashMap<>();
		params.put("cifId", cifId);
		try {
			logger.info("Fetching customer details with CIFID {}",cifId);

			result = template.getForObject(uri, CustomerDetails.class, params);

			logger.debug("Customer details {}",result.toString());
			logger.info("Successfully fetched customer details with CIFID {}",cifId);

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
			logger.info("Fetching customer details with CIFID {}",cifId);

			result = template.getForObject(uri, CustomerDetails.class, params);

			logger.debug("Customer details {}",result.toString());
			logger.info("Successfully fetched customer details with CIFID {}",cifId);

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
            result = (template.postForObject(uri, params, String.class));
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
		logger.debug("Enquiry params {}", params);
		try {

			logger.info("Doing name enquiry for account number {}",accountNumber);

			result = template.postForObject(uri, params, NEnquiryDetails.class);

			logger.info("Completed name enquiry for account number {}",accountNumber);


		} catch (Exception e) {
			logger.error("Error occurred doing name enquiry", e);
		}

		return result;
	}

	@Override
	public NEnquiryDetails doNameEnquiryQuickteller(String destinationInstitutionCode, String accountNumber) {
		NEnquiryDetails result = new NEnquiryDetails();
		String uri = QUICKTELLER_URI + quicktellerNameEnquiry;
		String hashcode = EncryptionUtil.getSHA512(appIdQuickteller + accountNumber + secretKeyQuickteller, null);
		Map<String, String> params = new HashMap<>();
		params.put("accountId", accountNumber);
		params.put("appid", appIdQuickteller);
		params.put("bankCode", destinationInstitutionCode);
		params.put("hash", hashcode);

		logger.debug("Enquiry params {}", params);
		try {

			logger.info("Doing name enquiry for account number {}",accountNumber);

			result = template.postForObject(uri, params, NEnquiryDetails.class);

			logger.info("Completed name enquiry for account number {}",accountNumber);


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
		Map<String, Object> params = new HashMap<>();
		params.put("alertType", "SMS");
		params.put("message", message);
		params.put("subject", subject);
		params.put("contactList", contacts);

		logger.debug("SMS params {}", params);

		try {

			logger.info("Sending SMS to {}",contact);

			result = template.postForObject(alertsSystem, params, ObjectNode.class);

			logger.debug("SMS API response {}",result.toString());
			logger.info("SMS sent to {}",contacts);

		} catch (Exception e) {
			logger.error(alertsSystem, params, e);
		}

		return CompletableFuture.completedFuture(result);
	}

	@Override
	public boolean sendRegCodeSMS(String message, String contact, String subject) {
		List<String> contacts = new ArrayList<>();
		contacts.add(contact);
		ObjectNode result = null;
		Map<String, Object> params = new HashMap<>();
		params.put("alertType", "SMS");
		params.put("message", message);
		params.put("subject", subject);
		params.put("contactList", contacts);
		logger.trace("params {}", params);

		try {

			logger.info("Sending registration code to {} via SMS",contact);

			result = template.postForObject(alertsSystem, params, ObjectNode.class);

			logger.debug("SMS API response {}",result.toString());

			logger.info("Registration code sent to {}",contact);


			if(result != null) {
				boolean response = result.get("success").asBoolean();
				logger.info("the boolean {}",response);
				return response;
			}
		} catch (Exception e) {
			logger.error(alertsSystem, params, e);

		}
		return false;
	}

	@Override
	// @Async
	public Rate getFee(String...channel) {

		String uri = URI + "/transfer/fee";
		Map<String, String> params = new HashMap<>();
		params.put("transactionChannel", channel[0]);
		params.put("tranAmount", channel[1]);
		try {
            return template.postForObject(uri, params, Rate.class);
		} catch (Exception e) {

			logger.error("Error occurred getting  fee", e);

			return new Rate("", "0", "");
		}

	}

	private TransRequest sendRTGSTransferRequest(TransRequest transRequest) {

		try {

			Account account = accountRepo.findFirstByAccountNumber(transRequest.getCustomerAccountNumber());

			Context context = new Context();
			context.setVariable("transRequest", transRequest);
			context.setVariable("customerName",account.getAccountName());

			String mail = templateEngine.process("mail/rtgstransfer", context);
			SettingDTO setting = configService.getSettingByName("CUSTOMER_CARE_EMAIL");
			if (setting != null && setting.isEnabled()) {
				String recipient = setting.getValue();
				String subject = messageSource.getMessage("rtgs.transfer.subject",null,locale);
				mailService.send(recipient, subject, mail, true);
				transRequest.setReferenceNumber(NumberUtils.generateReferenceNumber(15));
				transRequest.setStatus("000");
				transRequest.setStatusDescription(messageSource.getMessage("transfer.successful",null,locale));
			}
		} catch (Exception e) {
			logger.error("Exception occurred {}", e);
			transRequest.setStatus("96");
			transRequest.setStatusDescription("TRANSACTION FAILED");
		}

		return transRequest;
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
				Corporate corporate = corporateRepo.findById(user.getCorporate().getId()).get();
				boolean valid = corporate.getAccounts().contains(acct);
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

	public CustomAssessmentDetail getAssessmentDetails(CustomAssessmentDetailsRequest assessmentDetailsRequest){
		assessmentDetailsRequest.setAppId(appId);
		try {
			assessmentDetailsRequest.setHash(EncryptionUtil.getSHA512(appId+assessmentDetailsRequest.getCustomsCode()
					+ assessmentDetailsRequest.getSadAsmt().getSADAssessmentSerial()+secretKey, null));
			logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl+"/customduty/retrieveassessmentdetail");
			logger.debug("Fetching data assessmentDetailsRequest: {}", assessmentDetailsRequest);
			CustomAssessmentDetail response = template.postForObject(CustomDutyUrl+"/customduty/retrieveassessmentdetail", assessmentDetailsRequest, CustomAssessmentDetail.class);
			logger.debug("{}", response);
			return response;
		}
		catch (Exception e){
			logger.error("Error calling bank service rest service",e);
		}
		return null;
	}

	public CustomsAreaCommand getCustomsAreaCommands(CustomsAreaCommandRequest customsAreaCommandRequest) {
		try {
			logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl);
			CustomsAreaCommand command = template.postForObject(CustomDutyUrl+"/customduty/getncscommand"
					,customsAreaCommandRequest, CustomsAreaCommand.class);
			logger.debug("Fetching data from bank rest service via the url: {}", command);
			return command;
		}
		catch (Exception e){
			logger.error("Error calling bank service rest service1",e);
		}
		return new CustomsAreaCommand();
	}

	public CustomPaymentNotification paymentNotification(CorpPaymentRequest corpPaymentRequest, String userName){
		try {
			Map<String,Object> request = new HashMap<>();
			request.put("appId",appId);
			//hashed out by GB and replaced with the other hash gotten from opsNotificationPayment
			/*request.put("hash",EncryptionUtil.getSHA512(
					appId + corpPaymentRequest.getReferenceNumber() + corpPaymentRequest.getAmount() + secretKey, null));
*/
			request.put("hash",EncryptionUtil.getSHA512(
					appId + corpPaymentRequest.getReferenceNumber() + corpPaymentRequest.getAmount().setScale(2,BigDecimal.ROUND_HALF_UP) + secretKey, null));

			request.put("TranId",corpPaymentRequest.getCustomDutyPayment().getTranId());
			request.put("Amount",corpPaymentRequest.getAmount().toPlainString());
			request.put("LastAuthorizer",userName);
			request.put("InitiatedBy",corpPaymentRequest.getCustomDutyPayment().getInitiatedBy());
			request.put("PaymentRef",corpPaymentRequest.getReferenceNumber());
			request.put("CustomerAccountNo",corpPaymentRequest.getCustomerAccountNumber());
			logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl);
			logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl+"/customduty/payassessment");
			logger.debug("paymentNotificationRequest: {}", request);
			CustomPaymentNotification response = template.postForObject(CustomDutyUrl+"/customduty/payassessment", request, CustomPaymentNotification.class);
			logger.debug("payment notification Response: {}", response);
			logger.info("payment ref {}",corpPaymentRequest.getReferenceNumber());
			logger.info("InitiatedBy {}",corpPaymentRequest.getCustomDutyPayment().getInitiatedBy());
			logger.info("CustomerAccountNo {}",corpPaymentRequest.getCustomerAccountNumber());
			logger.info("LastAuthorizer {}",userName);

			logger.debug("payment notification params: {}", appId + corpPaymentRequest.getReferenceNumber() + corpPaymentRequest.getAmount().setScale(2,BigDecimal.ROUND_HALF_UP) + secretKey);
			logger.debug("payment notification hash: {}",EncryptionUtil.getSHA512(
					appId + corpPaymentRequest.getReferenceNumber() + corpPaymentRequest.getAmount().setScale(2,BigDecimal.ROUND_HALF_UP) + secretKey, null));

			return response;
		}
		catch (Exception e){
			logger.error("Error calling bank service rest service",e);
		}
		return null;
	}


	public CustomPaymentNotification opsPaymentNotification(CorpPaymentRequest corpPaymentRequest, String userName){
		try {
			Map<String,Object> request = new HashMap<>();
			request.put("appId",appId);

			request.put("hash",EncryptionUtil.getSHA512(
					appId + corpPaymentRequest.getReferenceNumber() + corpPaymentRequest.getAmount().setScale(2,BigDecimal.ROUND_HALF_UP) + secretKey, null));
			request.put("TranId",corpPaymentRequest.getCustomDutyPayment().getTranId());
			request.put("Amount",corpPaymentRequest.getAmount().toPlainString());
			request.put("LastAuthorizer",userName);
			request.put("InitiatedBy",corpPaymentRequest.getCustomDutyPayment().getInitiatedBy());
			request.put("PaymentRef",corpPaymentRequest.getReferenceNumber());
//			request.put("CustomerAccountNo",accessBeneficiaryAcct);
			request.put("CustomerAccountNo",corpPaymentRequest.getCustomerAccountNumber());
			logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl);
			logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl+"/customduty/payassessment");
			logger.debug("paymentNotificationRequest: {}", request);
			CustomPaymentNotification response = template.postForObject(CustomDutyUrl+"/customduty/payassessment", request, CustomPaymentNotification.class);
			logger.debug("payment notification Response: {}", response);
			logger.debug("payment notification params: {}", appId + corpPaymentRequest.getReferenceNumber() + corpPaymentRequest.getAmount() + secretKey);
			logger.debug("payment notification hash: {}",EncryptionUtil.getSHA512(
					appId + corpPaymentRequest.getReferenceNumber() + corpPaymentRequest.getAmount() + secretKey, null));
			return response;
		}
		catch (Exception e){
			logger.error("Error calling bank service rest service",e);
		}
		return null;
	}



	@Override
	public CustomTransactionStatus paymentStatus(CorpPaymentRequest corpPaymentRequest){
		try {
			//if(corpPaymentRequest.getCustomDutyPayment().getPaymentStatus().equals("F")) {
				logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl);
				Map<String, String> request = new HashMap<>();
				request.put("hash", EncryptionUtil.getSHA512(
						appId + corpPaymentRequest.getCustomDutyPayment().getTranId() + secretKey, null));
				request.put("appId", appId);
				request.put("Id", corpPaymentRequest.getCustomDutyPayment().getTranId());
				logger.debug("Fetching data from bank rest service using: {}", request);
				CustomTransactionStatus transactionStatus = template.postForObject(CustomDutyUrl + "/customduty/checktransactionstatus", request, CustomTransactionStatus.class);
				logger.info("the transaction status response {}", transactionStatus);
				return transactionStatus;
			//}
		}
		catch (Exception e){
			logger.error("Error calling bank service rest service",e);
		}
		return null;
	}

	@Override
	public String getReciept(String tranId){

		try {
			logger.debug("Fetching data from bank rest service via the url: {}", CustomDutyUrl);
			Map<String,String> request = new HashMap<>();
			request.put("hash",EncryptionUtil.getSHA512(
					appId+tranId+secretKey,null));
			request.put("appId",appId);
			request.put("tranId",tranId);
			logger.debug("Fetching data from bank rest service using: {}", request);
			String receipt= template.postForObject(CustomDutyUrl+"/customduty/getreceipt", request, String.class);
			logger.info("the transaction status response length {}",receipt.length());
			return receipt;
		}
		catch (Exception e){
			logger.error("Error calling bank service rest service",e);
		}
		return null;
	}

	@Override
	public TransferDetails reverseLocalTransfer(String referenceId){
		String reversalUrl = "http://132.10.200.140:9292/service/reverseFundTransfer?uniqueIdentifier=";
		Map<String, String> params = new HashMap<>();
		params.put("uniqueIdentifier", referenceId);
		logger.error("Reversing transfer for {}",referenceId);
		TransferDetails transferDetails = template.postForObject(reversalUrl + referenceId, params, TransferDetails.class);
		logger.info("reversal response {}",transferDetails);
		return transferDetails;
	}

	@Override
	public TransferDetails antiFraudStatusCheck(String transactionType,String referenceNo){
		TransferDetails transferDetails=null;
		StringBuilder sb=new StringBuilder();
		String query=sb.append("?transactionType=").append(transactionType).append("&uniqueIdentifier=").append(referenceNo).toString();

		String uri = antiFraudStatusCheckUrl+query;
		try {
            return template.getForObject(uri, TransferDetails.class);
		} catch (Exception e) {
			logger.error("Error getting status", e);
		}
		return transferDetails;
	}

	@Override
	public String estinameDepositRate(String amount,String tenor, String acctNum) {

		Rate response = null;
		String uri = URI+"/deposit/rate";
		Map<String, String> params = new HashMap<>();
		params.put("initialDepositAmount", amount);
		params.put("accountNumber", acctNum);
		params.put("tenor", tenor);

		try {
			response = template.postForObject(uri, params, Rate.class);

			logger.info("the reponse {}",response.getFeeValue());
			return response.getFeeValue();
		} catch (Exception e) {
			logger.error("Error occurred getting rate", e);
		}

		return "";

	}

	@Override
	public Response liquidateFixDeposit(FixedDepositDTO fixedDepositDTO) {
		Response response =  null;
		String uri = URI + "/liquidate";
		Map<String, String> params = new HashMap<>();
		params.put("debitAccountNumber", fixedDepositDTO.getAccountNumber());
		params.put("liquidateType", fixedDepositDTO.getLiquidateType());
		params.put("amount", fixedDepositDTO.getInitialDepositAmount());
		logger.info("Starting Transfer with Params: {}", params.toString());

		try {
			validate(fixedDepositDTO.getAccountNumber());
			response = template.postForObject(uri, params, Response.class);

			return response;

		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
		} catch (Exception e) {
			logger.error("Error occurred making transfer", e);
		}
		return response;
	}


	@Override
	public Response addFundToDeposit(FixedDepositDTO fixedDepositDTO) {
		Response response =  null;
		String uri = URI + "/addfund";
		Map<String, String> params = new HashMap<>();
		params.put("debitAccountNumber", fixedDepositDTO.getAccountNumber());
		params.put("amount", fixedDepositDTO.getInitialDepositAmount());
		logger.info("Starting Transfer with Params: {}", params.toString());

		try {
			validate(fixedDepositDTO.getAccountNumber());
			response = template.postForObject(uri, params, Response.class);

			return response;

		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
		} catch (Exception e) {
			logger.error("Error occurred making transfer", e);
		}
		return response;
	}

	@Override
	public Response bookFixDeposit(FixedDepositDTO fixedDepositDTO) {
		Response response =  null;
		String uri = URI + "/fixdeposit/book";
		Map<String, String> params = new HashMap<>();
		params.put("accountNumber", fixedDepositDTO.getAccountNumber());
		params.put("depositType", fixedDepositDTO.getDepositType());
		params.put("initialDepositAmount", fixedDepositDTO.getInitialDepositAmount());
		params.put("tenor", fixedDepositDTO.getTenor());
		params.put("rate", fixedDepositDTO.getRate());
		logger.info("Starting Transfer with Params: {}", params.toString());
		try {
			validate(fixedDepositDTO.getAccountNumber());
			response = template.postForObject(uri, params, Response.class);

			return response;

		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
		} catch (Exception e) {
			logger.error("Error occurred making transfer", e);
		}
		return response;
	}

	@Override
	public FixedDepositDTO getFixedDepositDetails(String accountNumber){

		FixedDepositDTO fixedDeposit = new FixedDepositDTO();


		String uri = URI+"/deposit/"+ accountNumber;

//		String uri = "http://localhost:8090"+"/deposit/"+ accountNumber;


		Map<String,String> params = new HashMap<>();
		params.put("accountNumber",accountNumber);


		try{

			fixedDeposit = template.getForObject(uri, FixedDepositDTO.class,params);

			return fixedDeposit;
		}
		catch (Exception e){

			logger.error("Error getting fixed deposit details",e);
		}
		return fixedDeposit;

	}

	@Override
	public List<FixedDepositDTO> getFixedDepositsDetails(String cifId){

		List<FixedDepositDTO> fixedDeposit = new ArrayList<>();

		String uri = URI+"/deposits/"+ cifId;

		Map<String,String> params = new HashMap<>();
		params.put("cifId",cifId);

		try{
			fixedDeposit =	Arrays.stream(template.getForObject(uri, FixedDepositDTO[].class, params))
					.collect(Collectors.toList());
			return fixedDeposit;
		}
		catch (Exception e){

			logger.error("Error getting fixed deposit details",e);
		}
		return fixedDeposit;

	}



    public TransRequest sendInternationalTransferRequest(TransRequest transRequest) {

		try {

			Account account = accountRepo.findFirstByAccountNumber(transRequest.getCustomerAccountNumber());

			Context context = new Context();
			context.setVariable("transRequest", transRequest);
			context.setVariable("customerName",account.getAccountName());


			String mail = templateEngine.process("mail/internationaltransfer", context);
			SettingDTO setting = configService.getSettingByName("CUSTOMER_CARE_EMAIL");
			if (setting != null && setting.isEnabled()) {
				String recipient = setting.getValue();
				String subject = messageSource.getMessage("international.transfer.subject",null,locale);

				/* Pls uncomment this when you're deploying
				* and check the application cnfiguration if mail config has been set
				*/
//				mailService.send(recipient, subject, mail, true);
				transRequest.setReferenceNumber(NumberUtils.generateReferenceNumber(15));
				transRequest.setStatus("000");
				transRequest.setStatusDescription(messageSource.getMessage("transfer.successful",null,locale));
			}
		} catch (Exception e) {
			logger.error("Exception occurred {}", e);
			transRequest.setStatus("96");
			transRequest.setStatusDescription("TRANSACTION FAILED");
		}

		return transRequest;
	}

	public LoanDTO getLoanDetails(String accountNumber){

	     	LoanDTO loan = new LoanDTO();
			String uri = URI+"/loan/{accountNumber}";
			Map<String,String> params = new HashMap<>();
			params.put("accountNumber",accountNumber);

			try{
				loan = template.getForObject(uri,LoanDTO.class,params);

				return loan;
			}
		catch (Exception e){
			logger.error("Error getting loan details",e);
			return null;
		}


	}

	@Override
	public String updateTransferLimit(TransferSetLimit tsl){
		String methodResponse = "success";
		Response response = null;
		TransferSetLimit tslDto = new TransferSetLimit();
		String uri = limitChargeUrl+"/service/updateLimit";
		Map<String,String> params = new HashMap<>();
		params.put("channel", tsl.getChannel());
		params.put("description", tsl.getDescription());
		params.put("customerType", tsl.getCustomerType());
		params.put("delFlag", tsl.getDelFlag());
		params.put("lowerLimit", tsl.getLowerLimit());
		params.put("upperLimit", tsl.getUpperLimit());
		params.put("id", tsl.getId().toString());
		logger.info("Api Params = " + params);
		try{
			response = template.postForObject(uri, params, Response.class);
            logger.info("response {} ", response);
		} catch(Exception e){
			logger.info("Error processing request");
			logger.info(e.getMessage());
			methodResponse = "fail";
		}
		return methodResponse;
	}


	@Override
	public String updateCharge(TransferFeeAdjustment tfaDTO){
		String methodResponse = "success";
		Response response =  null;
		TransferFeeAdjustment tfa = new TransferFeeAdjustment();
		String uri = limitChargeUrl+"/service/updateCharge";
		Map<String,String> params = new HashMap<>();
		params.put("feeRange", tfaDTO.getFeeRange());
		params.put("feeDescription", tfaDTO.getFeeDescription());
		params.put("fixedAmount", tfaDTO.getFixedAmount());
		params.put("fixedAmountValue", tfaDTO.getFixedAmountValue());
		params.put("rate", tfaDTO.getRate());
		params.put("rateValue", tfaDTO.getRateValue());
		params.put("delFlag",tfa.getDelFlag());
		params.put("transactionChannel", tfaDTO.getTransactionChannel());
		params.put("id", tfaDTO.getId().toString());
		logger.info("Api Params = " + params);
		try {
			response = template.postForObject(uri, params, Response.class);

		} catch (Exception e){
			logger.info("Error processing request");
			methodResponse = "fail";
		}
		return methodResponse;
	}

	@Override
	public List<BillerDTO> getBillers(){
		List<BillerDTO> billers = new ArrayList<>();
		String hashedCode = EncryptionUtil.getSHA512(appIdQuickteller+secretKeyQuickteller, null);
		logger.info("skey {}", hashedCode);
		String uri =QUICKTELLER_URI+quicktellerBillers;
		Map<String,String> params = new HashMap<>();
		params.put("appid",appIdQuickteller);
		params.put("hash",hashedCode);
		try {
			BillerResponse billerResponse = template.postForObject(uri,params, BillerResponse.class);
            logger.info("biller response is {}", billerResponse);
			billers = billerResponse.getBillers();
			return billers;
		} catch (Exception e){
			logger.info("Error processing request");
			logger.info("message === {} " , e.getMessage());

		}
			return billers;
	}

	@Override
	public BillPayment billPayment(BillPayment billPayment){
		PaymentResponse payment;
		String uri = QUICKTELLER_URI+quicktellerBillpaymentAdvice;
		logger.info("Bill Payment Advice url is {}", uri);
        BigDecimal d = billPayment.getAmount();
		BigDecimal f = new BigDecimal(100);
        String amount = d.multiply(f).toPlainString();
        logger.info("amount in Big decimal {}", amount);
        Map<String,String> params = new HashMap<>();

        params.put("TerminalId",terminalId);
		logger.info("Terminal ID is {}", terminalId);
        logger.info("appid is {}", appIdQuickteller);
        logger.info("secretKey is {}", secretKeyQuickteller);
		params.put("amount", amount);
        String hashedCode = EncryptionUtil.getSHA512(appIdQuickteller + billPayment.getPaymentCode() + amount + secretKeyQuickteller, null);
		params.put("appid",appIdQuickteller);
		params.put("customerAccount", billPayment.getCustomerAccountNumber());
		params.put("customerEmail", billPayment.getEmailAddress());
		params.put("customerId",billPayment.getCustomerId());
		params.put("customerMobile",billPayment.getPhoneNumber());
		params.put("hash", hashedCode);
		logger.info("Hash is {}", hashedCode);
		params.put("paymentCode",billPayment.getPaymentCode().toString());
		params.put("requestReference",billPayment.getRequestReference());
		logger.info("Starting payment with Params: {}", params.toString());

		try {
			payment	 = template.postForObject(uri,params, PaymentResponse.class);
			logger.info("response for payment {}", payment.toString());
			billPayment.setResponseDescription (payment.getResponseDescription());
			billPayment.setResponseCode(payment.getResponseCode());
			billPayment.setTerminalId(terminalId);
			logger.info("Saved Terminal Id is {}", terminalId);


			return billPayment;
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
			billPayment.setStatus(e.getStatusCode().toString());
			billPayment.setResponseDescription(e.getStatusCode().getReasonPhrase());
			return billPayment;

		} catch (Exception e) {
			logger.error("Error processing Bill Payment", e);
			billPayment.setStatus("Failed");
			billPayment.setResponseDescription("Payment Failed");
			return billPayment;
		}

	}

	@Override
	public BillPayment checkBillPaymentTransaction(BillPayment billPayment){


		PaymentResponse checkTransaction;
		String uri = QUICKTELLER_URI+quicktellerQueryTransaction;

		logger.info("Query Transaction url is {}", uri);
		Map<String, String> request = new HashMap<>();
		request.put("hash", EncryptionUtil.getSHA512(
				appIdQuickteller + billPayment.getRequestReference() + secretKeyQuickteller, null));
		request.put("appid", appIdQuickteller);
		request.put("requestReference", billPayment.getRequestReference());
		logger.debug("Fetching data from Quickteller Query Transaction rest service using: {}", request);
		try {
			checkTransaction = template.postForObject(uri, request, PaymentResponse.class);

			logger.info("the query transaction response is {}", checkTransaction);

			billPayment.setStatus(checkTransaction.getStatus());
			if (checkTransaction.getResponseDescription() != null) {
				billPayment.setResponseDescription(checkTransaction.getResponseDescription());
			}
			billPayment.setResponseCode(checkTransaction.getResponseCode());
			billPayment.setTransactionRef(checkTransaction.getTransactionRef());

			return billPayment;
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
			billPayment.setStatus(e.getStatusCode().toString());
			billPayment.setResponseDescription(e.getStatusCode().getReasonPhrase());
			return billPayment;

		}
	}

	@Override
	public RecurringPayment checkRecurringPaymentTransaction(RecurringPayment recurringPayment){

		PaymentResponse checkTransaction;
		String uri = QUICKTELLER_URI+quicktellerQueryTransaction;

		logger.info("Query Transaction url is {}", uri);
		Map<String, String> request = new HashMap<>();
		request.put("hash", EncryptionUtil.getSHA512(
				appIdQuickteller + recurringPayment.getRequestReference() + secretKeyQuickteller, null));
		request.put("appid", appIdQuickteller);
		request.put("requestReference", recurringPayment.getRequestReference());
		logger.debug("Fetching data from Quickteller Query Transaction rest service using: {}", request);
		try {
			checkTransaction = template.postForObject(uri, request, PaymentResponse.class);
			logger.info("the query transaction response is {}", checkTransaction);


			recurringPayment.setStatus(checkTransaction.getStatus());
			if (checkTransaction.getResponseDescription() != null) {
				recurringPayment.setResponseDescription(checkTransaction.getResponseDescription());
			}
			recurringPayment.setResponseCode(checkTransaction.getResponseCode());
			recurringPayment.setTransactionRef(checkTransaction.getTransactionRef());

			return recurringPayment;
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
			recurringPayment.setStatus(e.getStatusCode().toString());
			recurringPayment.setResponseDescription(e.getStatusCode().getReasonPhrase());
			return recurringPayment;

		}
	}

	@Override
	public RecurringPayment recurringPayment(RecurringPayment recurringPayment){
		PaymentResponse payment;
		String uri = QUICKTELLER_URI+quicktellerBillpaymentAdvice;
		logger.info("Bill Payment Advice url is {}", uri);
		BigDecimal d = recurringPayment.getAmount();
		BigDecimal f = new BigDecimal(100);
		String amount = d.multiply(f).toPlainString();
		Map<String,String> params = new HashMap<>();

		params.put("TerminalId",terminalId);
		params.put("amount", amount);
		String hashedCode = EncryptionUtil.getSHA512(appIdQuickteller + recurringPayment.getPaymentCode() + amount + secretKeyQuickteller, null);
		params.put("appid",appIdQuickteller);
		params.put("customerAccount", recurringPayment.getCustomerAccountNumber());
		params.put("customerEmail", recurringPayment.getEmailAddress());
		params.put("customerId",recurringPayment.getCustomerId());
		params.put("customerMobile",recurringPayment.getPhoneNumber());
		params.put("hash", hashedCode);
		params.put("paymentCode",recurringPayment.getPaymentCode().toString());
		params.put("requestReference",recurringPayment.getRequestReference());
		logger.info("Starting payment with Params: {}", params.toString());

		try {
			payment	 = template.postForObject(uri,params, PaymentResponse.class);
			logger.info("response for payment {}", payment.toString());
			recurringPayment.setResponseDescription (payment.getResponseDescription());
			recurringPayment.setResponseCode(payment.getResponseCode());
			recurringPayment.setTerminalId(terminalId);
			logger.info("Saved Terminal Id is {}", terminalId);

			return recurringPayment;
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP Error occurred", e);
			recurringPayment.setStatus(e.getStatusCode().toString());
			recurringPayment.setResponseDescription(e.getStatusCode().getReasonPhrase());
			return recurringPayment;

		} catch (Exception e) {
			logger.error("Error processing Bill Payment", e);
			recurringPayment.setStatus("Failed");
			recurringPayment.setResponseDescription("Payment Failed");
			return recurringPayment;
		}

	}

	@Override
	public List<PaymentItemDTO> getPaymentItems(Long billerId){
		String id = Long.toString(billerId);
		List<PaymentItemDTO> items = new ArrayList<>();
		String uri = QUICKTELLER_URI+quicktellerPaymentItems;
		Map<String,String> params = new HashMap<>();
		params.put("appid",appIdQuickteller);
		params.put("billerid", id);
		params.put("hash",EncryptionUtil.getSHA512(appIdQuickteller+id+secretKeyQuickteller, null));
		try {
			PaymentItemResponse paymentItemResponse = template.postForObject(uri,params, PaymentItemResponse.class);
			items = paymentItemResponse.getPaymentitems();
			return items;
		} catch (Exception e){
			logger.info("Error processing request");
		}
		return items;
	}

	@Override
	public List<BillerCategoryDTO> getBillerCategories(){
		List<BillerCategoryDTO> items = new ArrayList<>();
		String uri = QUICKTELLER_URI+quicktellerCategories;
		Map<String,String> params = new HashMap<>();
		params.put("appid",appIdQuickteller);
		params.put("hash",EncryptionUtil.getSHA512(appIdQuickteller+secretKeyQuickteller, null));
		try {
			BillerCategoryResponse billerCategoryResponse = template.postForObject(uri,params, BillerCategoryResponse.class);
			items = billerCategoryResponse.getCategorys();
			return items;
		} catch (Exception e){
			logger.info("Error processing request");
			logger.info("message === {} " , e.getMessage());
		}
		return items;
	}

	@Override
	public List<QuicktellerBankCodeDTO> getBankCodes(){
		List<QuicktellerBankCodeDTO> items = new ArrayList<>();
		String uri = QUICKTELLER_URI+getBankCodes;
		Map<String,String> params = new HashMap<>();
		params.put("appid",appIdQuickteller);
		params.put("hash",EncryptionUtil.getSHA512(appIdQuickteller+secretKeyQuickteller, null));
		try {
			BankCodeResponse bankCodeResponse = template.postForObject(uri,params, BankCodeResponse.class);
			items = bankCodeResponse.getCodes();
			return items;
		} catch (Exception e){
			logger.info("Error processing request");
			logger.info("message === {} " , e.getMessage());
		}
		return items;
	}

	@Override
	public List<CoverageDetailsDTO> getCoverages(String coverageNames, String customerId){

		List<CoverageDetailsDTO> coverageDetailsDTOList = new ArrayList<>();

		if ((coverageNames == null ||coverageNames.isEmpty()) || (customerId == null ||customerId.isEmpty())){
			logger.info("No coverage enabled for the user");
			return coverageDetailsDTOList;
		}
		logger.info("Coverage enabled for the user");
		coverageDetailsDTOList = Arrays.stream(coverageNames.split(","))
				.map(coverageName -> {
					CoverageDetailsDTO coverageDetails = new CoverageDetailsDTO();
					coverageDetails.setCoverageName(coverageName);
					coverageDetails.setCustomerId(customerId);
					coverageDetails.setCoverageUrl(customerId +"/"+ coverageName);
				return coverageDetails;
				}).collect(Collectors.toList());

		return coverageDetailsDTOList;
	}

	@Override
	public JsonNode getCoverageDetails(String coverageName, String customerId) {
		Map<String,Object> payloadParameters = new HashMap<>();
        Map<String, List<String>> coverageDetails = new LinkedHashMap<>();

		try {
			String lowerCaseCoverageName = coverageName.toLowerCase();
			String uri = URI+"/{coverageName}/{customerId}";

			payloadParameters.put("coverageName",lowerCaseCoverageName);
			payloadParameters.put("customerId",customerId.replaceAll("(\r\n|\r|\n)",""));
			JsonNode coverageJsonNode = template.getForObject(uri, JsonNode.class, payloadParameters);
//			JsonNode value = coverageJsonNode.fields().next().getValue();
//			if(value instanceof ArrayNode){
//				value.forEach(jsonNode -> extractCoverageDetails(coverageDetails, jsonNode));
//			}else{
//                extractCoverageDetails(coverageDetails, value);
//			}

			return coverageJsonNode;

		} catch (Exception e) {
			logger.error("Error getting coverage details", e);
			throw new InternetBankingException(e.getMessage());
		}
	}

	private void extractCoverageDetails(Map<String, List<String>> coverageDetails, JsonNode jsonNode) {
		Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
		while(fields.hasNext()) {
			Map.Entry<String, JsonNode> field = fields.next();
			String coverageKey = field.getKey().toUpperCase();
			String coverageValue = field.getValue().asText().toUpperCase();
			if(coverageDetails.containsKey(coverageKey)){
				coverageDetails.get(coverageKey).add(coverageValue);
			}
			else {
				coverageDetails.put(coverageKey, new ArrayList(Arrays.asList((coverageValue))));
			}
		}
	}








	@Override
    public NeftResponse submitNeftTransfer() {
		NeftResponse response = new NeftResponse();
		String uri = NEFTURI+"/api/neftOutWard/Submit";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");

        List<NeftTransfer> getUnsettledNeftList = neftTransferRepo.getAllUnsettledList();
        Double totalValue = getTotalValue(getUnsettledNeftList);
        logger.info("getUnsettledList == {}", getUnsettledNeftList);
        String ItemCount = String.valueOf(getUnsettledNeftList.size());
		String newDate = dateFormat.format(new Date());

		Map<String,Object> params = new HashMap<>();
		params.put("appid",appId);
		params.put("MsgID",getMsgId());
		params.put("TotalValue", totalValue);
		params.put("BankCode",bankcode);
		params.put("ItemCount", ItemCount);
		params.put("Date", newDate);
		params.put("SettlementTimeF", newDate);
//		List<NeftTransfer> neftTransfers = getUnsettledNeftList.stream()
//				.peek(neftTransfer -> updateNeftSettlement(newDate, neftTransfer))
//				.collect(Collectors.toList());
		List<NeftTransfer> neftTransfers = updateSequenceNumber(getUnsettledNeftList, newDate);
		logger.info("Complete PFItemDataStores ========= {}", neftTransfers);
		params.put("PFItemDataStores", neftTransfers);
		logger.info("PARAMS ============ {}", params);
		try{
			if (!getUnsettledNeftList.isEmpty()){
				response = template.postForObject(uri,params, NeftResponse.class);
				logger.info("Neft Response {}", response);
				getUnsettledNeftList.forEach(neftTransfer -> {
					updateNeftSettlement(newDate, neftTransfer);
					neftTransferRepo.save(neftTransfer);
				});
			}else {
				logger.info("No pending requests");
			}
		}catch (HttpStatusCodeException e) {

			logger.error("HTTP Error occurred", e);

		}catch (Exception e){
			logger.info("Error processing request ", e);
		}
		return response;
    }

	@Override
	public NeftResponseDTO submitInstantNeftTransfer(NeftTransfer neftTransfer) {
		List<NeftTransfer> neftTransferList = Collections.singletonList(neftTransfer);
		NeftResponseDTO response = new NeftResponseDTO();
		String uri = NEFTURI+"/api/neftOutWard/Submit";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ");

		Double totalValue = neftTransfer.getAmount().doubleValue();
		logger.info("Neft Transfer ======= {}", neftTransferList);

		String newDate = dateFormat.format(new Date());

		Map<String,Object> params = new HashMap<>();
		params.put("appid",appId);
		params.put("MsgID",getMsgId());
		logger.info("Mssg Id =============== {}", getMsgId());
		params.put("TotalVal ue", totalValue);
		params.put("BankCode",bankcode);
		params.put("ItemCount", String.valueOf(neftTransferList.size()));
		params.put("Date", newDate);
		params.put("SettlementTimeF", newDate);

		List<NeftTransfer> neftTransfers = updateSequenceNumber(neftTransferList, newDate);
		logger.info("Complete PFItemDataStores ========= {}", neftTransfers);
		params.put("PFItemDataStores", neftTransfers);
		logger.info("PARAMS ============ {}", params);
		try{
			if (!neftTransferList.isEmpty()){
				logger.info("Making Neft Transfer : {}" , neftTransfer);
				response = template.postForObject(uri,params, NeftResponseDTO.class);
				neftTransfers.forEach(neftTransferRepo::save);
			}
		}catch (Exception e){
			logger.info("Error processing request ", e);
		}
		return response;
	}

	private void updateNeftSettlement(String newDate, NeftTransfer neftTransfer) {
		neftTransfer.setSettlementTime(newDate);
		neftTransfer.setPresentmentDate(newDate);
		neftTransfer.setInstrumentDate(newDate);
		neftTransfer.setBankOfFirstDepositDate(newDate);
		neftTransfer.setStatus("Submitted");
	}

	private Long getMsgId(){
		return (long) Math.floor(Math.random() * 9000000000000L) + 1000000000000L;
	}

	private Double getTotalValue(List<NeftTransfer> neftTransfers){
		return neftTransfers
				.stream()
				.filter(Objects::nonNull)
				.map(NeftTransfer::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.doubleValue();
	}

	private List<NeftTransfer> updateSequenceNumber(List<NeftTransfer> neftTransfers, String newDate){
		List<NeftTransfer> neftTransferList = new ArrayList<>();
		for(int i = 0; i < neftTransfers.size(); i++){
			NeftTransfer neftTransfer = neftTransfers.get(i);
			neftTransfer.setItemSequenceNo(String.valueOf(i+1));
			neftTransfer.setSerialNo(String.valueOf(i+1));
			neftTransferList.add(neftTransfer);
		}
		return neftTransferList.
				stream()
				.peek(neftTransfer -> updateNeftSettlement(newDate, neftTransfer))
				.collect(Collectors.toList());
	}

}
