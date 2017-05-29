package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by SYLVESTER on 4/26/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class TransactionFee extends AbstractEntity{

    private String transactionType;
    private double percentage;
    private  double fixed;
    private String currency;
    private Date dateCreated;
    private boolean enabled;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getFixed() {
        return fixed;
    }

    public void setFixed(double fixed) {
        this.fixed = fixed;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "TransactionFee{" +
                "transactionType='" + transactionType + '\'' +
                ", percentage=" + percentage +
                ", fixed=" + fixed +
                ", currency='" + currency + '\'' +
                ", dateCreated=" + dateCreated +
                ", enabled=" + enabled +
                '}';
    }
}
