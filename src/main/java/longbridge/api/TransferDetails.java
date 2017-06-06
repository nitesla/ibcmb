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

    String narration;
    public TransferDetails() {
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
                ", narration='" + narration + '\'' +
                '}';
    }
}
