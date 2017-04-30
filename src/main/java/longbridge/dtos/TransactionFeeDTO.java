package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by Fortune on 4/29/2017.
 */


public class TransactionFeeDTO  {

    @JsonProperty("DT_RowId")
    private  Long id;
    private int version;
    @NotEmpty(message = "transactionType")
    private String transactionType;
    @NotEmpty(message = "currency")
    private String currency;
    @Min(message = "fixed",value = 0)
    private  double fixed;
    @Min(message = "percentage",value = 0)
    @Max(message = "percentage",value = 100)
    private double percentage;
    private String dateCreated;
    private boolean enabled;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
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
        return "TransactionFeeDTO{" +
                "id=" + id +
                ", version=" + version +
                ", transactionType='" + transactionType + '\'' +
                ", percentage=" + percentage +
                ", currency='" + currency + '\'' +
                ", fixed=" + fixed +
                ", dateCreated=" + dateCreated +
                ", enabled=" + enabled +
                '}';
    }
}
