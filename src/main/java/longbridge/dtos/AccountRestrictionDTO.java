package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Fortune on 4/28/2017.
 */
public class AccountRestrictionDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    @NotEmpty(message = "restrictionValue")
    private String restrictionValue;
    @NotEmpty(message = "restrictionType")
    private String restrictionType;
    @NotEmpty(message = "restrictedFor")
    private String restrictedFor;
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

    public String getRestrictionValue() {
        return restrictionValue;
    }

    public void setRestrictionValue(String restrictionValue) {
        this.restrictionValue = restrictionValue;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public String getRestrictedFor() {
        return restrictedFor;
    }

    public void setRestrictedFor(String restrictedFor) {
        this.restrictedFor = restrictedFor;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "AccountRestrictionDTO{" +
                "id=" + id +
                ", version=" + version +
                ", restrictionValue='" + restrictionValue + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", restrictedFor='" + restrictedFor + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }
}
