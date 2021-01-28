package longbridge.dtos;

import longbridge.models.RetailUser;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/** Created by Fortune on 4/5/2017.
 *
 */

public class RetailCustLimitDTO implements Serializable {

	private RetailUser customer;
	private String description;
	private String type;
	private BigDecimal lowerLimit;
	private BigDecimal upperLimit;
	private String currency;
	private String status;
	private Date effectiveDate;


	public RetailUser getCustomer() {
		return customer;
	}

	public void setCustomer(RetailUser customer) {
		this.customer = customer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public BigDecimal getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
