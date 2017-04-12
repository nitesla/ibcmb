package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * The {@code FinancialInstitution} class model contains details of a bank or any
 * other financial institution that participates in electronic transfers
 * @author Fortunatus Ekenachi
 * Created on 3/30/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class FinancialInstitution extends AbstractEntity {


    private String institutionCode;

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

    @Override
    public String toString() {
        return "FinancialInstitution{" +
                "institutionCode='" + institutionCode + '\'' +
                ", institutionName='" + institutionName + '\'' +
                '}';
    }
}

