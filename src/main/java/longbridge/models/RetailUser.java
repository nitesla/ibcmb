package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Wunmi on 3/28/2017.
 * RetailUser is a bank customer. Typically with a single identity operating a set of accounts.
 */


@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"userName","deletedOn"}))
public class RetailUser extends User{

	private String bvn;
	private String customerId;
	private Date birthDate;

	@OneToMany
	@JsonIgnore
	private Collection<RetailCustLimit> rtlCustLmt;

	@OneToMany(mappedBy= "user")
	@JsonIgnore
    private Collection<LocalBeneficiary> beneficiaries;


//	private Collection<Beneficiary> beneficiaries;
//
//	public Collection<Beneficiary> getBeneficiaries() {
//		return beneficiaries;
//	}
//
//
//	public void setBeneficiaries(Collection<Beneficiary> beneficiaries) {
//		this.beneficiaries = beneficiaries;
//	}


	public String getBvn() {
		return bvn;
	}

	public void setBvn(String bvn) {
		this.bvn = bvn;
	}

	public RetailUser(){
		this.userType = (UserType.RETAIL);
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Collection<RetailCustLimit> getRtlCustLmt() {
		return rtlCustLmt;
	}

	public void setRtlCustLmt(Collection<RetailCustLimit> rtlCustLmt) {
		this.rtlCustLmt = rtlCustLmt;
	}

	public Collection<LocalBeneficiary> getBeneficiaries() {
		return beneficiaries;
	}

	public void setBeneficiaries(Collection<LocalBeneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}


}
