package longbridge.models;


import longbridge.utils.CoverageMapConverter;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;

@Entity
public class AccountCoverage extends AbstractEntity  {
    private static final long serialVersionUID = -5786181085941056612L;

    private String code;
    private boolean isEnabled;

    //    private String type;
//    private String description;
//
//
//    @Convert(converter = CoverageMapConverter.class)
//    private Map<String,Object> coverageDetails;


    public AccountCoverage() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountCoverage)) return false;
        if (!super.equals(o)) return false;
        AccountCoverage that = (AccountCoverage) o;
        return isEnabled == that.isEnabled &&
                Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code, isEnabled);
    }

    @Override
    public String toString() {
        return "AccountCoverage{" +
                "code='" + code + '\'' +
                ", isEnabled=" + isEnabled +
                '}';
    }

}
