package longbridge.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * The {@code FinancialInstitution} class model contains details of a bank or any
 * other financial institution that participates in electronic transfers
 * @author Fortunatus Ekenachi
 * Created on 3/30/2017.
 */
@Entity
public class FinancialInstitution extends AbstractEntity {


    private String institutionCode;

    private String institutionName;

    @OneToMany(mappedBy = "financialInstitution")
    private Transfer transfer;

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

    @Override
    public String toString() {
        return "FinancialInstitution{" +
                "institutionCode='" + institutionCode + '\'' +
                ", institutionName='" + institutionName + '\'' +
                '}';
    }
}

