package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The {@code FinancialInstitution} class model contains details of a bank or any
 * other financial institution that participates in electronic transfers
 * @author Fortunatus Ekenachi
 * Created on 3/30/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"institutionCode","institutionType","deletedOn"}))

public class FinancialInstitution extends AbstractEntity {


    private String institutionCode;

    private FinancialInstitutionType institutionType;

    private String institutionName;

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

    @Override
    public String toString() {
        return "FinancialInstitution{" +
                "institutionCode='" + institutionCode + '\'' +
                ", institutionType='" + institutionType + '\'' +
                ", institutionName='" + institutionName + '\'' +
                '}';
    }



	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
}

