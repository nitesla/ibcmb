package longbridge.dtos;

/**
 * Created by Longbridge on 14/06/2017.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
public class BulkTransferDTO {
    @JsonProperty("DT_RowId")
    private Long id;
    private String debitAccount;
    private String requestDate;
    private String refCode;
    private  String status;
    private Long batchId;


    public BulkTransferDTO() {
    }

    public BulkTransferDTO(String debitAccount, String requestDate, String refCode, String status, Long batchId) {
        this.debitAccount = debitAccount;
        this.requestDate = requestDate;
        this.refCode = refCode;
        this.status = status;
        this.batchId = batchId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
