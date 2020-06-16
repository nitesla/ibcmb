package longbridge.models;


import longbridge.utils.CoverageMapConverter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(name = "account_coverage")
public class AccountCoverage extends AbstractEntity  {


    private String code;
    private boolean isEnabled;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "code_id",referencedColumnName = "id")
    private Code codeEntity;

    //    private String type;
//    private String description;
//
//
//    @Convert(converter = CoverageMapConverter.class)
//    private Map<String,Object> coverageDetails;


    public AccountCoverage() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Code getCodeEntity() {
        return codeEntity;
    }

    public void setCodeEntity(Code codeEntity) {
        this.codeEntity = codeEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountCoverage)) return false;
        if (!super.equals(o)) return false;
        AccountCoverage that = (AccountCoverage) o;
        return isEnabled == that.isEnabled &&
                Objects.equals(code, that.code) &&
                Objects.equals(codeEntity, that.codeEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code, isEnabled, codeEntity);
    }

    @Override
    public String toString() {
        return "AccountCoverage{" +
                "code='" + code + '\'' +
                ", isEnabled=" + isEnabled +
                ", codeEntity=" + codeEntity +
                '}';
    }
}
