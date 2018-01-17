package longbridge.utils.statement;


import java.util.Date;

/**
 * Created by ayoade_farooq@yahoo.com on 6/15/2017.
 */


public class TransactionDetails {
    private String currencyCode;
    private String postDate;
    private String valueDate;
    private String narration;
    private String accountBalance;
    private String tranType;
    private String tranAmount;
    private String tranId;

    private String tranDate;
    private String tranSN;
    private String debitAmount;
    private String creditAmount;
    public String getTranDate() {
        return tranDate;
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranSN() {
        return tranSN;
    }

    public void setTranSN(String tranSN) {
        this.tranSN = tranSN;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getTranAmount() {
        return tranAmount;
    }

    public void setTranAmount(String tranAmount) {
        this.tranAmount = tranAmount;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    @Override
    public String toString() {
        return "TransactionDetails{" +
                "currencyCode='" + currencyCode + '\'' +
                ", postDate='" + postDate + '\'' +
                ", valueDate='" + valueDate + '\'' +
                ", narration='" + narration + '\'' +
                ", accountBalance='" + accountBalance + '\'' +
                ", tranType='" + tranType + '\'' +
                ", tranAmount='" + tranAmount + '\'' +
                ", tranId='" + tranId + '\'' +
                ", tranDate='" + tranDate + '\'' +
                ", tranSN='" + tranSN + '\'' +
                '}';
    }


}


