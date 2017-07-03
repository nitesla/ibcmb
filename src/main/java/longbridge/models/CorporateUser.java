package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

	@ManyToOne @JsonIgnore
    private Corporate corporate;

	@ManyToOne
	private CorporateRole corporateRole;


    public CorporateUser(){
		this.userType = (UserType.CORPORATE);
	}

	public Corporate getCorporate() {
		return corporate;
	}

	public void setCorporate(Corporate corporate) {
		this.corporate = corporate;
	}


	public CorporateRole getCorporateRole() {
		return corporateRole;
	}

	public void setCorporateRole(CorporateRole corporateRole) {
		this.corporateRole = corporateRole;
	}


	@Override
	public int hashCode(){
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o){
		return super.equals(o);
	}

}
