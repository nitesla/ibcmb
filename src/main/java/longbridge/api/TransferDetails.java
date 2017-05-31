package longbridge.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 5/8/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferDetails {
    private String responseCode;
    private String  responseDescription;
    private String  uniqueReferenceCode;
    private String sessionId;
    String narration;
    public TransferDetails() {
    }


    public TransferDetails(String responseCode, String responseDescription, String uniqueReferenceCode, String sessionId) {
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
        this.uniqueReferenceCode = uniqueReferenceCode;
        this.sessionId = sessionId;
    }


    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getUniqueReferenceCode() {
        return uniqueReferenceCode;
    }

    public void setUniqueReferenceCode(String uniqueReferenceCode) {
        this.uniqueReferenceCode = uniqueReferenceCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }


    @Override
    public String toString() {
        return "TransferDetails{" +
                "responseCode='" + responseCode + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", uniqueReferenceCode='" + uniqueReferenceCode + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", narration='" + narration + '\'' +
                '}';
    }
}
