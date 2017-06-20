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
    private String requestDate;
    private String reference;
    private  String status;

    @OneToMany(mappedBy = "bulkTransfer",cascade = {CascadeType.ALL})
    private List<CreditRequest> creditRequestList;

    @ManyToOne
    private Corporate corporate;



    public BulkTransfer() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
