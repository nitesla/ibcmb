package longbridge.models;



import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(name = "account_coverage")
public class AccountCoverage extends AbstractEntity  {



    private boolean enabled;

    @ManyToOne
    private Corporate corporate;

    @ManyToOne
    private Code code;



    public AccountCoverage() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountCoverage)) return false;
        if (!super.equals(o)) return false;
        AccountCoverage that = (AccountCoverage) o;
        return enabled == that.enabled &&
                Objects.equals(corporate, that.corporate) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled, corporate, code);
    }

    @Override
    public String toString() {
        return "AccountCoverage{" +
                "enabled=" + enabled +
                ", corporate=" + corporate +
                ", code=" + code +
                '}';
    }
}
