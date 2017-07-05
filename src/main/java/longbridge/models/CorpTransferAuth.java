package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created by Fortune on 7/5/2017.
 */

@Entity
@Where(clause = "del_Flag='N'")
public class CorpTransferAuth extends  AbstractEntity {

    CorporateRole role;
    CorpTransRequest corpTransRequest;

    public CorporateRole getRole() {
        return role;
    }

    public void setRole(CorporateRole role) {
        this.role = role;
    }

    public CorpTransRequest getCorpTransRequest() {
        return corpTransRequest;
    }

    public void setCorpTransRequest(CorpTransRequest corpTransRequest) {
        this.corpTransRequest = corpTransRequest;
    }
}
