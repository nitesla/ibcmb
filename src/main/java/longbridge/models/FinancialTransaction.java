package longbridge.models;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

/**
 * Created by Wunmi on 3/28/2017.
 */
public class FinancialTransaction{
    private String tranType ;
    private String transactionParticulars;
    private String currencyCode;
    private BigDecimal amount;
    private BigDecimal currentBalance;
    private String accountId;
    private LocalDateTime valueDate;
    private LocalDateTime postDate;

    public String getTransactionParticulars() {
        return transactionParticulars;
    }

    public void setTransactionParticulars(String transactionParticulars) {
        this.transactionParticulars = transactionParticulars;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDateTime valueDate) {
        this.valueDate = valueDate;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }


    @Override
    public String toString() {
        return "FinancialTransaction{" +
                "transactionParticulars='" + transactionParticulars + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", amount=" + amount +
                ", currentBalance=" + currentBalance +
                ", tranType=" + tranType +
                ", accountId='" + accountId + '\'' +
                ", valueDate=" + valueDate +
                ", postDate=" + postDate +
                '}';
    }
}
