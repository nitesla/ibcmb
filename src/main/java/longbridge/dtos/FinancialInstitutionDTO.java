package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Showboy on 24/04/2017.
 */
public class FinancialInstitutionDTO {

    @JsonProperty("DT_RowId")
    private Long id;
    @NotEmpty
    private String institutionCode;
    @NotEmpty
    private String institutionName;

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
}
