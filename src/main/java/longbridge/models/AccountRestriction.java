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
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class AccountRestriction extends AbstractEntity{

    private String accountNumber;
    private String restrictionType;
    private Date dateCreated;


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
    public String
    toString() {
        return "AccountRestriction{" +
                "accountNumber='" + accountNumber + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }
}
