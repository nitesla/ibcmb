package longbridge.api;

/**
 * Created by ayoade_farooq@yahoo.com on 5/3/2017.
 */
public class LocalTransferResponse {

    private String responseCode;
    private String responseDescription;


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
        return "{\"LocalTransferResponse\":{"
                + "                        \"responseCode\":\"" + responseCode + "\""
                + ",                         \"responseDescription\":\"" + responseDescription + "\""
                + "}}";
    }
}
