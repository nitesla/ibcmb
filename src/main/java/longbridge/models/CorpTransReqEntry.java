package longbridge.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Date;

/**
 * Created by Fortune on 7/5/2017.
 */
@Entity
public class CorpTransReqEntry extends AbstractEntity {

    private String comments;

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
