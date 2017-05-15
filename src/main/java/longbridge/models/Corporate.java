package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Fortune on 3/29/2017.
 */
@Entity
@Audited
@Where(clause ="del_Flag='N'" )
public class Corporate extends AbstractEntity{

    private String rcNumber;
    private String customerId;
    private  String corporateType;
    private String companyName;
    private String email;
    private String address;
    private String status ;
    private Date createdOnDate;



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

    public String getCorporateType() {
        return corporateType;
    }

    public void setCorporateType(String corporateType) {
        this.corporateType = corporateType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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


    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    @Override
    public String toString() {
        return "Corporate{" +
                "rcNumber='" + rcNumber + '\'' +
                ", customerId='" + customerId + '\'' +
                ", corporateType='" + corporateType + '\'' +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", createdOnDate=" + createdOnDate +
                ", users=" + users +
                ", corpLimits=" + corpLimits +
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
