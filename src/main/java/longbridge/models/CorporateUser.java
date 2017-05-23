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
@Audited
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))
public class CorporateUser extends User {

	@ManyToOne
    private Corporate corporate;

	@ManyToMany(mappedBy = "authorizers")
	private List<CorpTransferRule> corpTransferRules;

	@OneToMany(mappedBy = "authorizer")
	List<PendingAuthorization> pendingAuthorizations = new ArrayList<PendingAuthorization>();


    public CorporateUser(){
		this.userType = (UserType.CORPORATE);
	}

	public Corporate getCorporate() {
		return corporate;
	}

	public void setCorporate(Corporate corporate) {
		this.corporate = corporate;
	}

	public List<CorpTransferRule> getCorpTransferRules() {
		return corpTransferRules;
	}

	public void setCorpTransferRules(List<CorpTransferRule> corpTransferRules) {
		this.corpTransferRules = corpTransferRules;
	}

	public List<PendingAuthorization> getPendingAuthorizations() {
		return pendingAuthorizations;
	}

	public void setPendingAuthorizations(List<PendingAuthorization> pendingAuthorizations) {
		this.pendingAuthorizations = pendingAuthorizations;
	}

	@Override
	public String toString() {
		return "CorporateUser{" + super.toString() + "," +
				"corporate=" + corporate +
				'}';
	}
}
