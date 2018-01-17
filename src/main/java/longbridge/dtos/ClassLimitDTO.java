package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * Created by Fortune on 4/25/2017.
 */
public class ClassLimitDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String customerType;
    @NotEmpty
    private String accountClass;
    private String description;
    @NotEmpty
    private String channel;

    private String maxLimit;
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

    public String getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(String accountClass) {
        this.accountClass = accountClass;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
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

    public String getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
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
        return "ClassLimitDTO{" +
                "id=" + id +
                ", version=" + version +
                ", customerType='" + customerType + '\'' +
                ", accountClass='" + accountClass + '\'' +
                ", description='" + description + '\'' +
                ", channel='" + channel + '\'' +
                ", maxLimit=" + maxLimit +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", frequency='" + frequency + '\'' +
                '}';
    }
}
