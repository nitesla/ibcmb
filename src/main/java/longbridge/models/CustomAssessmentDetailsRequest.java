package longbridge.models;

import java.util.List;

public class CustomAssessmentDetailsRequest {
    private String customsCode;
    private List<CustomSADAsmt> sADAsmt;
    private String appId;
    private String hash;

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    public List<CustomSADAsmt> getsADAsmt() {
        return sADAsmt;
    }

    public void setsADAsmt(List<CustomSADAsmt> sADAsmt) {
        this.sADAsmt = sADAsmt;
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
}
