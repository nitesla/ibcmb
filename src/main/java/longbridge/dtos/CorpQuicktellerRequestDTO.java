package longbridge.dtos;

 import java.io.Serializable;

/**
 * Created by Fortune on 5/19/2017.
 */

public class CorpQuicktellerRequestDTO extends QuicktellerRequestDTO implements Serializable {

    private String corporateId;

    private String transAuthId;

    public String getTransAuthId() {
        return transAuthId;
    }

    public void setTransAuthId(String transAuthId) {
        this.transAuthId = transAuthId;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }
}
