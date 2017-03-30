package longbridge.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Limit extends AbstractEntity {
	
	private String description;
	private String type;
	private BigDecimal lowerLimit;
	private BigDecimal upperLimit;
	private String currency;
	private String status;
	private Date effectiveDate;
	
	public Limit(){
	}
	
	public Limit(String description, String type, BigDecimal lowerLimit, BigDecimal upperLimit, String currency,
			String status, Date effectiveDate) {
		super();
		this.description = description;
		this.type = type;
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.currency = currency;
		this.status = status;
		this.effectiveDate = effectiveDate;
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
	@Override
	public String toString() {
		return "Limit [description=" + description + ", type=" + type + ", upperLimit=" + upperLimit + ", currency="
				+ currency + "]";
	}
	
	

}
