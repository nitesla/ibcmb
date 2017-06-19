package longbridge.models;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Date valueDate;
    private Date postDate;

private SimpleDateFormat format=new SimpleDateFormat("DD/MM/YYYY");



    public FinancialTransaction(String tranBreak)
    {

        String [] tranToken = tranBreak.split(",");
        this.accountId=tranToken[0].trim();
        this.amount=new BigDecimal(tranToken[1].trim().replaceAll(" ", ""));
        this.currencyCode=tranToken[2].trim();
        this.currentBalance=new BigDecimal(tranToken[3].trim().replaceAll(" ", ""));
        try {
            this.postDate=format.parse(tranToken[4].trim());
            this.valueDate=format.parse(tranToken[7].trim());
        } catch (ParseException e) {
            this.postDate= new Date();
            this.valueDate= new Date();
        }
        this.transactionParticulars=tranToken[5].trim();
        this.tranType=tranToken[6].trim();




    }

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

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
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
