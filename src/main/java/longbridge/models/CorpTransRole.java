package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Audited(withModifiedFlag=true)
public class CorpTransRole extends AbstractEntity {

	private int numOfRole = 1;

	private CorporateRole role;

	public int getNumOfRole() {
		return numOfRole;
	}

	public void setNumOfRole(int numOfRole) {
		this.numOfRole = numOfRole;
	}

	public CorporateRole getRole() {
		return role;
	}

	public void setRole(CorporateRole role) {
		this.role = role;
	}
}
