package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Created by Wunmi on 27/03/2017. CorporateUser is a bank customer. Typically
 * with a multiple identities representing an organization operating a set of
 * accounts.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))
public class CorporateUser extends User {

	protected String isFirstTimeLogon = "Y";

	@ManyToOne
    private Corporate corporate;

//	private CorporateRole corporateRole;
//

    public String getIsFirstTimeLogon() {
        return isFirstTimeLogon;
    }

    public void setIsFirstTimeLogon(String isFirstTimeLogon) {
        this.isFirstTimeLogon = isFirstTimeLogon;
    }

    public CorporateUser(){
		this.userType = (UserType.CORPORATE);
	}

	public Corporate getCorporate() {
		return corporate;
	}

	public void setCorporate(Corporate corporate) {
		this.corporate = corporate;
	}


//	public CorporateRole getCorporateRole() {
//		return corporateRole;
//	}
//
//	public void setCorporateRole(CorporateRole corporateRole) {
//		this.corporateRole = corporateRole;
//	}


	@Override
	public int hashCode(){
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o){
		return super.equals(o);
	}




}
