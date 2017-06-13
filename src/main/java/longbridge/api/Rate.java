package longbridge.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 6/12/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rate {

    private String feeName;
    private Integer feeValue;
    private String feeDescription;


    public Rate() {
    }

    public Rate(String feeName, Integer feeValue, String feeDescription) {
        this.feeName = feeName;
        this.feeValue = feeValue;
        this.feeDescription = feeDescription;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public Integer getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(Integer feeValue) {
        this.feeValue = feeValue;
    }

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
    }


    @Override
    public String toString() {
        return "Rate{" +
                "feeName='" + feeName + '\'' +
                ", feeValue='" + feeValue + '\'' +
                ", feeDescription='" + feeDescription + '\'' +
                '}';
    }
}
