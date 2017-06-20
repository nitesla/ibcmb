package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
    private  String status;

    @OneToMany(mappedBy = "bulkTransfer",cascade = {CascadeType.ALL})
    private List<CreditRequest> creditRequestList;

    @ManyToOne
    private Corporate corporate;


    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

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

    public BulkTransfer(String debitAccount, String date, String reference, String status, List<CreditRequest> creditRequestList, Corporate corporate) {
        this.debitAccount = debitAccount;
        this.date = date;
        this.reference = reference;
        this.status = status;
        this.creditRequestList = creditRequestList;
        this.corporate = corporate;
    }

    public BulkTransfer() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
