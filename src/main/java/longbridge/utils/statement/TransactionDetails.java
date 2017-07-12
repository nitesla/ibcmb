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

    @Override
    public String toString() {
        return "TransactionDetails{" +
                "currencyCode='" + currencyCode + '\'' +
                ", postDate='" + postDate + '\'' +
                ", valueDate='" + valueDate + '\'' +
                ", narration='" + narration + '\'' +
                ", accountBalance='" + accountBalance + '\'' +
                ", tranType='" + tranType + '\'' +
                '}';
    }
}


