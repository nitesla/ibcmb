package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * Created by Fortune on 4/27/2017.
 */

@Entity
@Audited
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"accountClass"}))
public class AccountClassRestriction extends AbstractEntity {

    private String accountClass;
    private String restrictionType;
    private Date dateCreated;

    public String getAccountClass() {
        return accountClass;
    }

    public void setAccountClass(String accountClass) {
        this.accountClass = accountClass;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Override
    public String toString() {
        return "AccountClassRestriction{" +
                "accountClass='" + accountClass + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
