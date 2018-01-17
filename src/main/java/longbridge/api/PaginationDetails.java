package longbridge.api;



/**
 * Created by ayoade_farooq@yahoo.com on 6/24/2017.
 */


public class PaginationDetails
{
private String lastAccountBalance;
private String lastAccountCurrency;
private String lastPostDate;
private String lastTranDate;
private String  lastTranId;
private String lastTranSN;


    public String getLastAccountBalance() {
        return lastAccountBalance;
    }

    public void setLastAccountBalance(String lastAccountBalance) {
        this.lastAccountBalance = lastAccountBalance;
    }

    public String getLastAccountCurrency() {
        return lastAccountCurrency;
    }

    public void setLastAccountCurrency(String lastAccountCurrency) {
        this.lastAccountCurrency = lastAccountCurrency;
    }

    public String getLastPostDate() {
        return lastPostDate;
    }

    public void setLastPostDate(String lastPostDate) {
        this.lastPostDate = lastPostDate;
    }

    public String getLastTranDate() {
        return lastTranDate;
    }

    public void setLastTranDate(String lastTranDate) {
        this.lastTranDate = lastTranDate;
    }

    public String getLastTranId() {
        return lastTranId;
    }

    public void setLastTranId(String lastTranId) {
        this.lastTranId = lastTranId;
    }

    public String getLastTranSN() {
        return lastTranSN;
    }

    public void setLastTranSN(String lastTranSN) {
        this.lastTranSN = lastTranSN;
    }

    @Override
    public String toString() {
        return "PaginationDetails{" +
                "lastAccountBalance='" + lastAccountBalance + '\'' +
                ", lastAccountCurrency='" + lastAccountCurrency + '\'' +
                ", lastPostDate='" + lastPostDate + '\'' +
                ", lastTranDate='" + lastTranDate + '\'' +
                ", lastTranId='" + lastTranId + '\'' +
                ", lastTranSN='" + lastTranSN + '\'' +
                '}';
    }
}
