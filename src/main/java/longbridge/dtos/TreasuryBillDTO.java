package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Fortune on 2/26/2018.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class TreasuryBillDTO {

    private String customerId;
    private String startDate;
    private String maturityDate;
    private String faceValue;
    private String carryingValue;
    private String rate;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(String faceValue) {
        this.faceValue = faceValue;
    }

    public String getCarryingValue() {
        return carryingValue;
    }

    public void setCarryingValue(String carryingValue) {
        this.carryingValue = carryingValue;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}


