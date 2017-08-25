package longbridge.utils.statement;


import java.util.Date;

/**
 * Created by ayoade_farooq@yahoo.com on 6/23/2017.
 */

public class TransactionHistory
{

    private String narration;
    private Date postedDate;
    private Date valueDate;
    private String tranType;
    private String tranId;
    private String balance;
    private String debitAmount;
    private String creditAmount;

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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
        return "TransactionHistory{" +
                "narration='" + narration + '\'' +
                ", postedDate=" + postedDate +
                ", valueDate=" + valueDate +
                ", tranType='" + tranType + '\'' +
                ", tranId='" + tranId + '\'' +
                ", balance='" + balance + '\'' +
                ", debitAmount='" + debitAmount + '\'' +
                ", creditAmount='" + creditAmount + '\'' +
                '}';
    }
}
