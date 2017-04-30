package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.FinancialInstitutionType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
public class FinancialInstitutionDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    @NotEmpty
    private String institutionCode;
    @NotNull
    private FinancialInstitutionType institutionType;
    @NotEmpty
    private String institutionName;
    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
