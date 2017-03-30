package longbridge.models;

import java.util.Collection;

import javax.persistence.*;

/**
 * Created by Wunmi on 3/28/2017.
 * RetailUser is a bank customer. Typically with a single identity operating a set of accounts.
 */


@Entity
public class RetailUser extends User{
	
	private Collection<Beneficiary> beneficiaries;

	
	
	public Collection<Beneficiary> getBeneficiaries() {
		return beneficiaries;
	}


	public void setBeneficiaries(Collection<Beneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}


	@Override
	public String toString() {
		return "RetailUser ["+super.toString()+"]";
	}

}
