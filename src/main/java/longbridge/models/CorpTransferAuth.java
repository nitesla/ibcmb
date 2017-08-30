package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

/**
 * Created by Fortune on 7/5/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause = "del_Flag='N'")
public class CorpTransferAuth extends  AbstractEntity {


    private String status;
    private Date lastEntry;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<CorpTransReqEntry> auths = new HashSet<>();

    @OneToOne
    private CorpTransRequest corpTransRequest;

    public CorpTransRequest getCorpTransRequest() {
        return corpTransRequest;
    }

    public void setCorpTransRequest(CorpTransRequest corpTransRequest) {
        this.corpTransRequest = corpTransRequest;
    }

    public Set<CorpTransReqEntry> getAuths() {
        return auths;
    }

    public void setAuths(Set<CorpTransReqEntry> auths) {
        this.auths = auths;
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
