package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Fortune on 5/18/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause = "del_Flag='N'")
public class PendAuth extends AbstractEntity {

    @ManyToOne
    private CorporateRole role;

    @ManyToOne
    CorpTransRequest corpTransferRequest;


    public CorporateRole getRole() {
        return role;
    }

    public void setRole(CorporateRole role) {
        this.role = role;
    }

    public CorpTransRequest getCorpTransferRequest() {
        return corpTransferRequest;
    }

    public void setCorpTransferRequest(CorpTransRequest corpTransferRequest) {
        this.corpTransferRequest = corpTransferRequest;
    }
}
