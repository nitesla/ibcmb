package longbridge.models;

import javax.persistence.Entity;

@Entity
public class RetailCustLimit extends Limit {

	private RetailUser customer;

	public RetailUser getCustomer() {
		return customer;
	}

	public void setCustomer(RetailUser customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "RetailCustLimit [customer=" + customer + ","+ super.toString() +"]";
	}
	
}
