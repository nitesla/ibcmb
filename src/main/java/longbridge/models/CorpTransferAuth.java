package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by Fortune on 7/5/2017.
 */

@Entity
@Where(clause = "del_Flag='N'")
public class CorpTransferAuth extends  AbstractEntity {


    private String status;
    private Date lastEntry;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<CorpTransReqEntry> authorizations;

    @OneToOne
    private CorpTransRequest corpTransRequest;

    public CorpTransRequest getCorpTransRequest() {
        return corpTransRequest;
    }

    public void setCorpTransRequest(CorpTransRequest corpTransRequest) {
        this.corpTransRequest = corpTransRequest;
    }

    public Set<CorpTransReqEntry> getAuthorizations() {
        return authorizations;
    }

    public void setAuthorizations(Set<CorpTransReqEntry> authorizations) {
        this.authorizations = authorizations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastEntry() {
        return lastEntry;
    }

    public void setLastEntry(Date lastEntry) {
        this.lastEntry = lastEntry;
    }
}
