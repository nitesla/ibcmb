package longbridge.utils.statement;



/**
 * Created by ayoade_farooq@yahoo.com on 6/23/2017.
 */

public class TransactionHistory
{

    private String narration;
    private String postedDate;
    private String valueDate;
    private String tranType;
    private String tranId;
    private String balance;

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
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


    @Override
    public String toString() {
        return "TransactionHistory{" +
                "narration='" + narration + '\'' +
                ", postedDate='" + postedDate + '\'' +
                ", valueDate='" + valueDate + '\'' +
                ", tranType='" + tranType + '\'' +
                ", tranId='" + tranId + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
