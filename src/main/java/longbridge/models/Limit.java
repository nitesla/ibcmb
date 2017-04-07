package longbridge.models;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Limit extends AbstractEntity {
	
	private String description;
	private String type;
	private double lowerLimit;
	private double upperLimit;
	private String currency;
	private String status;
	private LocalDateTime effectiveDate;
	
	public Limit(){
	}
	
	public Limit(String description, String type, double lowerLimit, double upperLimit, String currency,
			String status, LocalDateTime effectiveDate) {
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
	public double getLowerLimit() {
		return lowerLimit;
	}
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public double getUpperLimit() {
		return upperLimit;
	}
	public void setUpperLimit(double upperLimit) {
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

	public LocalDateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public String toString() {
		return "Limit [description=" + description + ", type=" + type + ", upperLimit=" + upperLimit + ", currency="
				+ currency + "]";
	}
	
	

}
