package longbridge.dtos;

public class CorpPaymentRequestDTO extends TransferRequestDTO {

    private String corporateId;

    private String transAuthId;

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public String getTransAuthId() {
        return transAuthId;
    }

    public void setTransAuthId(String transAuthId) {
        this.transAuthId = transAuthId;
    }
}
