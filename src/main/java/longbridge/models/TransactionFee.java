package longbridge.models;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by SYLVESTER on 4/26/2017.
 */
@Entity
public class TransactionFee extends AbstractEntity{
    private String transactionType;
    private String percentage;
    private String fixed;
    private Date date;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
