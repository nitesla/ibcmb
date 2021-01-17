package longbridge.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.api.*;
import longbridge.dtos.*;
import longbridge.dtos.apidtos.NeftResponseDTO;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.*;
import longbridge.response.NeftResponse;
import longbridge.utils.Response;
import longbridge.utils.statement.AccountStatement;
import longbridge.utils.statement.TransactionHistory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The {@IntegrationService} interface provides the methods for accessing the various integration service
 */
public interface IntegrationService {


    /**
     * Returns all the accounts of a customer
     *
     * @param cifid the customer's id
     * @return a list of accounts
     */
    List<AccountInfo> fetchAccounts(String cifid);

    List<ExchangeRate> getExchangeRate();


    /**
     * Fetches the {@link AccountStatement} of the account identified by
     * {@code accountId} for the period between {@code fromDate} and {@code toDate}
     *
     * @param accountNo the  account Number of the Account
     * @param fromDate  the Date from where to begin fetching the account statement
     * @param toDate    the Date to stop fetching the account statement (inclusive)
     * @return {@code AccountStatement} object
     */
    AccountStatement getAccountStatements(String accountNo, Date fromDate, Date toDate, String tranType,String numOfTxn,PaginationDetails paginationDetails);
    AccountStatement getAccountStatements(String accountNo, Date fromDate, Date toDate, String tranType,String numOfTxn);
    AccountStatement getFullAccountStatement(String accountNo, Date fromDate, Date toDate, String tranType);
    AccountStatement getTransactionHistory(String accountNo, Date fromDate, Date toDate, String tranType);

    List<TransactionHistory> getLastNTransactions(String accountNo, String numberOfRecords);

    /**
     * Fetches the account Balance of the account specified by accountId
     *
     * @param accountId accountId of the account
     * @return Map containing the available balance and ledger balance of the account
     */
    Map<String, BigDecimal> getBalance(String accountId);


    /**
     * Initiates a transfer request to the relevant Transfer service.
     */
    TransRequest makeTransfer(TransRequest transRequest) throws InternetBankingTransferException;

    TransferDetails makeNapsTransfer(Naps naps) throws InternetBankingTransferException;

    /**
     * Fetches the account Name, Balance , Type from the account table specified by account Number
     *
     * @param acctNo account number of the account
     * @return Map containing the available details of the account
     */
    AccountDetails viewAccountDetails(String acctNo);

    CustomerDetails isAccountValid(String accNo, String email, String dob);

    CustomerDetails viewCustomerDetails(String accNo);

    CustomerDetails viewCustomerDetailsByCif(String cifId);


    /** Validate the account, with a valid account number, email and dob
     *
     * @param accNo account number of the account
     * @param email email of the account
     * @param dob date of birth specified in the account
     * @return Map that returns false of the details doesnt correspond or exist
     */


    /**
     * This fetches the full name of the customer connected to the
     * specified account number
     *
     * @param accountNumber
     * @return The full name of the customer or null
     */
    String getAccountName(String accountNumber);

    /**
     * This method make an API that fetches the daily transactions with accountNo
     *
     * @param acctNo account number
     * @return map containing the total transaction amount
     */

    BigDecimal getDailyDebitTransaction(String acctNo);

    /**
     * This method make an API that fetches the Daily Account Limit with accountNo and Channel
     *
     * @param accNo   the account number
     * @param channel the transaction channel
     * @return map containing the total transaction limit
     */
    String getDailyAccountLimit(String accNo, String channel);


    NEnquiryDetails doNameEnquiry(String destinationInstitutionCode, String accountNumber);

    NEnquiryDetails doNameEnquiryQuickteller(String destinationInstitutionCode, String accountNumber);

    BigDecimal getAvailableBalance(String s);
    @Async
    CompletableFuture<ObjectNode>  sendSMS(String message, String contact, String subject);

    boolean  sendRegCodeSMS(String message, String contact, String subject);

    Rate getFee(String...channel);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomsAreaCommand getCustomsAreaCommands(CustomsAreaCommandRequest customsAreaCommandRequest);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomAssessmentDetail getAssessmentDetails(CustomAssessmentDetailsRequest assessmentDetailsRequest);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomPaymentNotification paymentNotification(CorpPaymentRequest corpPaymentRequest, String username);

    @PreAuthorize("hasAuthority('VIEW_CUSTOM_DUTY')")
    CustomPaymentNotification opsPaymentNotification(CorpPaymentRequest corpPaymentRequest, String userName);

    @PreAuthorize("hasAuthority('CUSTOM_DUTY')")
    CustomTransactionStatus paymentStatus(CorpPaymentRequest corpPaymentRequest);

    String getReciept(String tranId);

    TransferDetails reverseLocalTransfer(String referenceId);

    TransRequest makeCustomDutyPayment(TransRequest transRequest);

    TransferDetails antiFraudStatusCheck(String transactionType,String referenceNo);
    String estinameDepositRate(String amount,String tenor, String acctNum);
    FixedDepositDTO getFixedDepositDetails(String accountNumber);
    List<FixedDepositDTO> getFixedDepositsDetails(String cifId);
    Response liquidateFixDeposit(FixedDepositDTO fixedDepositDTO);
    Response addFundToDeposit(FixedDepositDTO fixedDepositDTO);
    Response bookFixDeposit(FixedDepositDTO fixedDepositDTO);

    TransRequest makeBackgroundTransfer(TransRequest transRequest) throws InternetBankingTransferException;
    LoanDTO getLoanDetails(String accountNumber);
    String updateTransferLimit(TransferSetLimit tsl);
    String updateCharge(TransferFeeAdjustment tfaDTO);
    List<BillerDTO> getBillers();
    List<PaymentItemDTO> getPaymentItems(Long billerId);
    BillPayment billPayment(BillPayment billPayment);
    RecurringPayment recurringPayment(RecurringPayment recurringPayment);
    List<BillerCategoryDTO> getBillerCategories();
    List<QuicktellerBankCodeDTO> getBankCodes();

    List<CoverageDetailsDTO> getCoverages(String coverageName, String customerId);
    Map<String, List<String>> getCoverageDetails(String coverageName, String customerId);
    NeftResponse submitNeftTransfer();
//    NeftTransfer checkNeftStatus();
    NeftResponseDTO submitInstantNeftTransfer(NeftTransfer neftTransfer);

}
