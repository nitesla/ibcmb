package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

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

	@ManyToOne
    private Corporate corporate;

	@ManyToMany(mappedBy = "auths")
	private List<CorpTransRule> corpTransRules;

	@OneToMany(mappedBy = "authorizer")
	List<PendAuth> pendAuths = new ArrayList<PendAuth>();


    public CorporateUser(){
		this.userType = (UserType.CORPORATE);
	}

	public Corporate getCorporate() {
		return corporate;
	}

	public void setCorporate(Corporate corporate) {
		this.corporate = corporate;
	}

	public List<CorpTransRule> getCorpTransRules() {
		return corpTransRules;
	}

	public void setCorpTransRules(List<CorpTransRule> corpTransRules) {
		this.corpTransRules = corpTransRules;
	}

	public List<PendAuth> getPendAuths() {
		return pendAuths;
	}

	public void setPendAuths(List<PendAuth> pendAuths) {
		this.pendAuths = pendAuths;
	}

	@Override
	public String toString() {
		return "CorporateUser{" + super.toString() + "," +
				"corporate=" + corporate +
				'}';
	}
}
