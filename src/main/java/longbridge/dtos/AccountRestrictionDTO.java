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
    @NotEmpty(message = "Please enter an Account Number")
    private String accountNumber;
    @NotEmpty(message = "Please enter a Restriction Type")
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
                ", accountNumber='" + accountNumber + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }
}
