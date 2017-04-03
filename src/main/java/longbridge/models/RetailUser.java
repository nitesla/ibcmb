package longbridge.models;

import javax.persistence.Entity;
import java.util.Collection;

/**
 * Created by Wunmi on 3/28/2017.
 * RetailUser is a bank customer. Typically with a single identity operating a set of accounts.
 */


@Entity
public class RetailUser extends AbstractEntity {


	private Collection<Beneficiary> beneficiaries;


	
	public Collection<Beneficiary> getBeneficiaries() {
		return beneficiaries;
	}


	public void setBeneficiaries(Collection<Beneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}


//	public RetailUser(){
//		this.userType = (UserType.RETAIL.toString());
//	}

	@Override
	public String toString() {
		return "RetailUser ["+super.toString()+"]";
	}

}
