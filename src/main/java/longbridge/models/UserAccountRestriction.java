package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by Fortune on 4/27/2017.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class UserAccountRestriction extends AbstractEntity{

    private Long corporateUserId;
    private Long accountId;
    @Enumerated(EnumType.STRING)
    private RestrictionType restrictionType;

    public UserAccountRestriction() {
    }

    public UserAccountRestriction(Long userId, Long accountId, RestrictionType restrictionType) {
        this.corporateUserId = userId;
        this.accountId = accountId;
        this.restrictionType = restrictionType;
    }

    public enum RestrictionType{
       VIEW, TRANSACTION, NONE
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public RestrictionType getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(RestrictionType restrictionType) {
        this.restrictionType = restrictionType;
    }


    public Long getCorporateUserId() {
        return corporateUserId;
    }

    public void setCorporateUserId(Long corporateUserId) {
        this.corporateUserId = corporateUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccountRestriction)) return false;
        if (!super.equals(o)) return false;

        UserAccountRestriction that = (UserAccountRestriction) o;

        if (!getCorporateUserId().equals(that.getCorporateUserId())) return false;
        return getAccountId().equals(that.getAccountId());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getCorporateUserId().hashCode();
        result = 31 * result + getAccountId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserAccountRestriction{" +
                "corporateUserId=" + corporateUserId +
                ", accountId=" + accountId +
                ", restrictionType=" + restrictionType +
                "} " + super.toString();
    }
}
