package longbridge.models;



import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(name = "acct_crv")
public class AccountCoverage extends AbstractEntity  {



    private boolean isEnabled;

    @ManyToOne
    private Corporate corporate;

    @OneToOne()
    @JoinColumn(name = "code_id",referencedColumnName = "id")
    private Code code;



    public AccountCoverage() {
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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
        return isEnabled == that.isEnabled &&
                Objects.equals(corporate, that.corporate) &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isEnabled, corporate, code);
    }

    @Override
    public String toString() {
        return "AccountCoverage{" +
                "isEnabled=" + isEnabled +
                ", corporate=" + corporate +
                ", code=" + code +
                '}';
    }
}
