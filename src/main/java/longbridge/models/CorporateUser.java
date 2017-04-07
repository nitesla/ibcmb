package longbridge.models;

import javax.persistence.*;

/**
 * Created by Wunmi on 27/03/2017. CorporateUser is a bank customer. Typically
 * with a multiple identities representing an organization operating a set of
 * accounts.
 */
@Entity
public class CorporateUser extends User {

	private boolean isEnabled;

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
	}

	@ManyToOne
    private Corporate corporate;

    public CorporateUser(){
		this.userType = (UserType.CORPORATE);
	}

	public Corporate getCorporate() {
		return corporate;
	}

	public void setCorporate(Corporate corporate) {
		this.corporate = corporate;
	}

	@Override
	public String toString() {
		return "CorporateUser{" + super.toString() + "," +
				"corporate=" + corporate +
				'}';
	}
}
