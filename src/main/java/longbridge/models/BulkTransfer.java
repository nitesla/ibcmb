package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by Longbridge on 14/06/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class BulkTransfer extends AbstractEntity{
    private String debitAccount;
    private String date;
    private String reference;

    @OneToMany(mappedBy = "bulkTransfer")
    private List<CreditRequest> creditRequestList;

    //and other stuffs


    public String getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<CreditRequest> getCreditRequestList() {
        return creditRequestList;
    }

    public void setCreditRequestList(List<CreditRequest> creditRequestList) {
        this.creditRequestList = creditRequestList;
    }

    public BulkTransfer(String debitAccount, String date, String reference, List<CreditRequest> creditRequestList) {
        this.debitAccount = debitAccount;
        this.date = date;
        this.reference = reference;
        this.creditRequestList = creditRequestList;
    }

    public BulkTransfer() {
    }
}
