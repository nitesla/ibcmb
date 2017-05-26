package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by Fortune on 5/18/2017.
 */

@Entity
@Audited
@Where(clause = "del_Flag='N'")
public class PendAuth extends AbstractEntity {

    @ManyToOne
    private CorporateUser authorizer;

    @ManyToOne
    CorpTransRequest corpTransferRequest;

    public CorporateUser getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(CorporateUser authorizer) {
        this.authorizer = authorizer;
    }

    public CorpTransRequest getCorpTransferRequest() {
        return corpTransferRequest;
    }

    public void setCorpTransferRequest(CorpTransRequest corpTransferRequest) {
        this.corpTransferRequest = corpTransferRequest;
    }
}
