package longbridge.dtos.apidtos;

import longbridge.utils.statement.TransactionDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MobileAccountStatementDTO implements Serializable {

    private String acctNum;
    private String fromDate;
    private String toDate;
    private String tranType;

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
    private String hasMoreData;
    private List<TransactionDetails> transactionDetails = new ArrayList<>();


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

    public String getHasMoreData() {
        return hasMoreData;
    }

    public void setHasMoreData(String hasMoreData) {
        this.hasMoreData = hasMoreData;
    }

    public List<TransactionDetails> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(List<TransactionDetails> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public String getAcctNum() {
        return acctNum;
    }

    public void setAcctNum(String acctNum) {
        this.acctNum = acctNum;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    @Override
    public String toString() {
        return "MobileAccountStatementDTO{" +
                "acctNum='" + acctNum + '\'' +
                ", fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", tranType='" + tranType + '\'' +
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
                ", hasMoreData='" + hasMoreData + '\'' +
                ", transactionDetails=" + transactionDetails +
                '}';
    }
}
