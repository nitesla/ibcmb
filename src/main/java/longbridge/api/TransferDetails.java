package longbridge.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 5/8/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferDetails {
    private String responseCode;
    private String  responseDescription;

    public TransferDetails() {
    }

    public TransferDetails(String responseCode, String responseDescription) {
        this.responseCode = responseCode;
        this.responseDescription = responseDescription;
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
        return "{\"TransferDetails\":{"
                + "                        \"responseCode\":\"" + responseCode + "\""
                + ",                         \"responseDescription\":\"" + responseDescription + "\""
                + "}}";
    }
}
