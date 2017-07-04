package longbridge.utils.statement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by ayoade_farooq@yahoo.com on 6/15/2017.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountStatement{
    List<TransactionDetails> transactionDetails;
    private String accountNumber;
    private String Address;
    private String openingBalance;
    private String closingBalance;
    private String totalCredit;
    private String totalDebit;
    private String customerName;
    private String currencyCode;
    private String creditCount;
    private String debitCount;

    public List<TransactionDetails> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(List<TransactionDetails> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(String openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(String closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(String totalCredit) {
        this.totalCredit = totalCredit;
    }

    public String getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(String totalDebit) {
        this.totalDebit = totalDebit;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCreditCount() {
        return creditCount;
    }

    public void setCreditCount(String creditCount) {
        this.creditCount = creditCount;
    }

    public String getDebitCount() {
        return debitCount;
    }

    public void setDebitCount(String debitCount) {
        this.debitCount = debitCount;
    }

    @Override
    public String toString() {
        return "AccountStatement{" +
                "transactionDetails=" + transactionDetails +
                ", accountNumber='" + accountNumber + '\'' +
                ", Address='" + Address + '\'' +
                ", openingBalance='" + openingBalance + '\'' +
                ", closingBalance='" + closingBalance + '\'' +
                ", totalCredit='" + totalCredit + '\'' +
                ", totalDebit='" + totalDebit + '\'' +
                ", customerName='" + customerName + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", creditCount='" + creditCount + '\'' +
                ", debitCount='" + debitCount + '\'' +
                '}';
    }
}
