package longbridge.models;

import javax.persistence.Entity;

/**
 * Created by ayoade_farooq@yahoo.com on 7/16/2017.
 *
 */

@Entity
public class TransferCode extends AbstractEntity {

private String responseCode;
private String responseDesc;
private String responseMessage;


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
