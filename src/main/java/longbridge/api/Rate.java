package longbridge.api;


/**
 * Created by ayoade_farooq@yahoo.com on 6/12/2017.
 */

public class Rate {

    private String feeName;
    private String feeValue;
    private String feeDescription;

    public Rate(String feeName, String feeValue, String feeDescription) {
        this.feeName = feeName;
        this.feeValue = feeValue;
        this.feeDescription = feeDescription;
    }

    public Rate() {
    }

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


    @Override
    public String toString() {
        return "Rate{" +
                "feeName='" + feeName + '\'' +
                ", feeValue='" + feeValue + '\'' +
                ", feeDescription='" + feeDescription + '\'' +
                '}';
    }
}
