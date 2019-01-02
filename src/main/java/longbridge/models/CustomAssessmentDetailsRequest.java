package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomAssessmentDetailsRequest {

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("CustomsCode")
    private String customsCode;

    @JsonProperty("SadAsmt")
    private CustomSADAsmt sadAsmt;

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public CustomSADAsmt getSadAsmt() {
        return sadAsmt;
    }

    public void setSadAsmt(CustomSADAsmt sadAsmt) {
        this.sadAsmt = sadAsmt;
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
                "appId='" + appId + '\'' +
                ", hash='" + hash + '\'' +
                ", customsCode='" + customsCode + '\'' +
                ", sadAsmt=" + sadAsmt +
                '}';
    }
}
