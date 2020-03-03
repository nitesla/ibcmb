package longbridge.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class Product extends AbstractEntity {
	
	
	private String name;
	private Double price;
	private boolean readonly;
	
	@ManyToOne @JsonIgnore
	private Merchant merchant;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", price=" + price + ", readonly=" + readonly
				+ ", merchant=" + merchant + "]";
	}
	
	

}
