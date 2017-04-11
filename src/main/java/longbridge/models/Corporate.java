package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collection;

/**
 * Created by Fortune on 3/29/2017.
 */
@Entity
@Audited
public class Corporate extends AbstractEntity{

    private String rcNumber;
    private String customerId;
    private String companyName;
    private String email;
    private String address;
    private boolean isEnabled;

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Collection<CorpLimit> getCorpLimits() {
        return corpLimits;
    }

    public void setCorpLimits(Collection<CorpLimit> corpLimits) {
        this.corpLimits = corpLimits;
    }

    @OneToMany
    private Collection<CorporateUser> users;

//    @OneToMany
//    private Collection<Beneficiary> beneficiaries;

    @OneToMany
    private Collection<CorpLimit> corpLimits;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getRcNumber() {
        return rcNumber;
    }

    public void setRcNumber(String rcNumber) {
        this.rcNumber = rcNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    

	public Collection<CorporateUser> getUsers() {
		return users;
	}

	public void setUsers(Collection<CorporateUser> users) {
		this.users = users;
	}

//	public Collection<Beneficiary> getBeneficiaries() {
//		return beneficiaries;
//	}
//
//	public void setBeneficiaries(Collection<Beneficiary> beneficiaries) {
//		this.beneficiaries = beneficiaries;
//	}

	@Override
	public String toString() {
		return "Corporate [rcNumber=" + rcNumber + ", companyName=" + companyName + ", email=" + email + ", address="
				+ address + "]";
	}

   

}
