package longbridge.models;

import javax.persistence.Entity;
import java.util.Collection;

/**
 * Created by Fortune on 3/29/2017.
 */
@Entity
public class Corporate extends AbstractEntity{

    private String rcNumber;
    private String customerId;
    private String companyName;
    private String email;
    private String address;
   
    private Collection<CorporateUser> users;
    
    private Collection<Beneficiary> beneficiaries;

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

	public Collection<Beneficiary> getBeneficiaries() {
		return beneficiaries;
	}

	public void setBeneficiaries(Collection<Beneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	@Override
	public String toString() {
		return "Corporate [rcNumber=" + rcNumber + ", companyName=" + companyName + ", email=" + email + ", address="
				+ address + "]";
	}

   

}
