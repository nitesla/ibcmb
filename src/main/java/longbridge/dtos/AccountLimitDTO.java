package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * Created by Fortune on 4/25/2017.
 */
public class AccountLimitDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String customerType;
    @NotEmpty
    private String accountNumber;
    private String description;
    @NotEmpty
    private String channel;
    @Min(0)
    private double lowerLimit;
    @Min(0)
    private double upperLimit;
    @NotEmpty
    private String currency;
    private String status;
    @NotEmpty
    private String frequency;


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

    public String getCustomerType() {
        return customerType;
    }



    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }


    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    @Override
    public String toString() {
        return "AccountLimitDTO{" +
                "id=" + id +
                ", version=" + version +
                ", customerType='" + customerType + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", description='" + description + '\'' +
                ", channel='" + channel + '\'' +
                ", lowerLimit=" + lowerLimit +
                ", upperLimit=" + upperLimit +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", frequency='" + frequency + '\'' +
                '}';
    }
}
