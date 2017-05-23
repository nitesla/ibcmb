package longbridge.dtos;

/**
 * Created by Fortune on 5/19/2017.
 */

public class CorpTransferRequestDTO extends TransferRequestDTO {

    private String corporateId;

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }
}
