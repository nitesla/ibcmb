package longbridge.dtos;

 import java.io.Serializable;

/**
 * Created by Fortune on 5/19/2017.
 */

public class CorpTransferRequestDTO extends TransferRequestDTO implements Serializable {

    private String corporateId;

    private String transAuthId;

    private String paymentMethodCode;

    private Long neftId;

    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }

    public void setPaymentMethodCode(String paymentMethodCode) {
        this.paymentMethodCode = paymentMethodCode;
    }

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

    public Long getNeftId() {
        return neftId;
    }

    public void setNeftId(Long neftId) {
        this.neftId = neftId;
    }
}
