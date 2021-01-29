package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TransferSetLimitDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;
    private String channel;
    private String customerType;
    private String description;
    private String lowerLimit;
    private String upperLimit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCustomerType() {
        return customerType;
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

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }


    @Override
    public String toString() {
        return "TransferSetLimitDTO{" +
                "id=" + id +
                ", channel='" + channel + '\'' +
                ", customerType='" + customerType + '\'' +
                ", description='" + description + '\'' +
                ", lowerLimit='" + lowerLimit + '\'' +
                ", upperLimit='" + upperLimit + '\'' +
                '}';
    }
}
