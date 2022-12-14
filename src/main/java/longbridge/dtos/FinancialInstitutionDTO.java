package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.FinancialInstitutionType;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
public class FinancialInstitutionDTO implements Serializable {

    @JsonProperty("DT_RowId")
    private Long id;

    private String sortCode;
    @NotEmpty(message = "institutionCode")
    private String institutionCode;
    private FinancialInstitutionType institutionType;
    @NotEmpty(message = "institutionName")
    private String institutionName;
    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public FinancialInstitutionType getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(FinancialInstitutionType institutionType) {
        this.institutionType = institutionType;
    }

    public String getInstitutionName() {
        if (institutionName!=null) return institutionName.toUpperCase();
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
