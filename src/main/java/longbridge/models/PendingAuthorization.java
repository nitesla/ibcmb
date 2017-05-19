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
public class PendingAuthorization extends AbstractEntity {

    @ManyToOne
    private CorporateUser authorizer;

    @ManyToOne
    CorpTransferRequest corpTransferRequest;

    public CorporateUser getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(CorporateUser authorizer) {
        this.authorizer = authorizer;
    }

    public CorpTransferRequest getCorpTransferRequest() {
        return corpTransferRequest;
    }

    public void setCorpTransferRequest(CorpTransferRequest corpTransferRequest) {
        this.corpTransferRequest = corpTransferRequest;
    }
}
