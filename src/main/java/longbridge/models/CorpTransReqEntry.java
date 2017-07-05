package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;

/**
 * Created by Fortune on 7/5/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class CorpTransReqEntry extends AbstractEntity {

    private String comments;

    private String status;

    @ManyToOne
    private CorporateRole corporateRole;

    @ManyToOne
    private CorporateUser corporateUser;
    private Date entryDate;



    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CorporateRole getCorporateRole() {
        return corporateRole;
    }

    public void setCorporateRole(CorporateRole corporateRole) {
        this.corporateRole = corporateRole;
    }

    public CorporateUser getCorporateUser() {
        return corporateUser;
    }

    public void setCorporateUser(CorporateUser corporateUser) {
        this.corporateUser = corporateUser;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }
}
