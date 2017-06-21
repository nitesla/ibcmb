package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CreditRequest extends AbstractEntity{
    private String accountName;
    private String accountNumber;
    private String amount;
    private String sortCode;

    @ManyToOne
    BulkTransfer bulkTransfer;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public BulkTransfer getBulkTransfer() {
        return bulkTransfer;
    }

    public void setBulkTransfer(BulkTransfer bulkTransfer) {
        this.bulkTransfer = bulkTransfer;
    }

    public CreditRequest(String accountName, String accountNumber, String amount, String sortCode, BulkTransfer bulkTransfer) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.sortCode = sortCode;
        this.bulkTransfer = bulkTransfer;
    }

    public CreditRequest() {
    }
}
