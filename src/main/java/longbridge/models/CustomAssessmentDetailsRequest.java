package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CustomAssessmentDetailsRequest {

    private CustomSADAsmt SadAsmt;
    private String customsCode;
    private String SADAssessmentSerial;
    private String SADAssessmentNumber;
    private String SADYear;
    private String appId;
    private String hash;

    public CustomAssessmentDetailsRequest() {
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public String getSADAssessmentSerial() {
        return SADAssessmentSerial;
    }

    public void setSADAssessmentSerial(String SADAssessmentSerial) {
        this.SADAssessmentSerial = SADAssessmentSerial;
    }

    public String getSADAssessmentNumber() {
        return SADAssessmentNumber;
    }

    public void setSADAssessmentNumber(String SADAssessmentNumber) {
        this.SADAssessmentNumber = SADAssessmentNumber;
    }

    public String getSADYear() {
        return SADYear;
    }

    public void setSADYear(String SADYear) {
        this.SADYear = SADYear;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "CustomAssessmentDetailsRequest{" +
                "customsCode='" + customsCode + '\'' +
                ", SADAssessmentSerial='" + SADAssessmentSerial + '\'' +
                ", SADAssessmentNumber='" + SADAssessmentNumber + '\'' +
                ", SADYear='" + SADYear + '\'' +
                ", appId='" + appId + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
