package longbridge.dtos;


public class CorpNeftBeneficiaryDTO extends NeftBeneficiaryDTO {

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
