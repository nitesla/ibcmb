package longbridge.models;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class RetailCustLimit extends Limit {

	@ManyToOne
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
