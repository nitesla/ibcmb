package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditRequest extends AbstractEntity{

    private String accountNumber;
    private String accountName;
    private BigDecimal amount;
    private String narration;
    private String status;
    private String sortCode;

    @ManyToOne
    BulkTransfer bulkTransfer;


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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public BulkTransfer getBulkTransfer() {
        return bulkTransfer;
    }

    public void setBulkTransfer(BulkTransfer bulkTransfer) {
        this.bulkTransfer = bulkTransfer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CreditRequest() {
    }

    public CreditRequest(String accountNumber, String sortCode, String accountName, BigDecimal amount, String narration, String status, BulkTransfer bulkTransfer) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.accountName = accountName;
        this.amount = amount;
        this.narration = narration;
        this.status = status;
        this.bulkTransfer = bulkTransfer;
    }
}
