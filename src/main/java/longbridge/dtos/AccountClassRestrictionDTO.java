package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Fortune on 4/28/2017.
 */
public class AccountClassRestrictionDTO {


    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    @NotEmpty(message = "accountClass")
    private String accountClass;
    @NotEmpty(message = "restrictionType")
    private String restrictionType;
    private String dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "AccountClassRestriction{" +
                "id=" + id +
                ", version=" + version +
                ", accountClass='" + accountClass + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }
}
