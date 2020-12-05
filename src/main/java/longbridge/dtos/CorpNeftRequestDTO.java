package longbridge.dtos;

public class CorpNeftRequestDTO extends NeftTransferRequestDTO {
    private String corporateId;

    private String transAuthId;

    public CorpNeftRequestDTO() {
    }

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

    @Override
    public String toString() {
        return "CorpNeftRequestDTO{" +
                "corporateId='" + corporateId + '\'' +
                ", transAuthId='" + transAuthId + '\'' +
                '}';
    }
}
