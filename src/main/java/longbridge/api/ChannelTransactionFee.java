package longbridge.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 4/25/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelTransactionFee
{
    private  String feeName;
    private String feeValue;
    private String feeDescription;

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(String feeValue) {
        this.feeValue = feeValue;
    }

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
    }
}
