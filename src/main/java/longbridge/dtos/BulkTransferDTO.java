package longbridge.dtos;

/**
 * Created by Longbridge on 14/06/2017.
 */
import com.fasterxml.jackson.annotation.JsonSetter;
public class BulkTransferDTO {
    private long dt_RowId;
    private String reference;
    private String accountNumber;
    private String sortCode;
    private String accountName;
    private String amount;
    private String narration;

    public BulkTransferDTO(String reference, String accountNumber, String sortCode, String accountName, String amount, String narration) {
        this.reference = reference;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.accountName = accountName;
        this.amount = amount;
        this.narration = narration;
    }

    public BulkTransferDTO() {
    }

    public long getDt_RowId() {
        return dt_RowId;
    }

    @JsonSetter("DT_RowId")
    public void setDt_RowId(long dt_RowId) {
        this.dt_RowId = dt_RowId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
