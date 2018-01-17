package longbridge.models;

import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.Date;

@MappedSuperclass
public class Limit extends AbstractEntity {
	
	private String description;
	private String type;
	private double lowerLimit;
	private double upperLimit;
	private String currency;
	private String status;
	private Date effectiveDate;
	
	public Limit(){
	}
	
	public Limit(String description, String type, double lowerLimit, double upperLimit, String currency,
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


	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
