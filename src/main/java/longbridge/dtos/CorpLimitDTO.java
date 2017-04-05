package longbridge.dtos;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

/**
 * Created by Fortune on 4/5/2017.
 *
 */



public class CorpLimitDTO extends Limit {

	private CorporateDTO corporateDTO;
	private String description;
	private String type;
	private BigDecimal lowerLimit;
	private BigDecimal upperLimit;
	private String currency;
	private String status;
	private LocalDateTime effectiveDate;


	public CorpLimitDTO(){}




	public CorpLimitDTO(Limit limit) {
		super(limit.getDescription(), limit.getType(), limit.getLowerLimit(), limit.getUpperLimit(),
				limit.getCurrency(), limit.getStatus(), limit.getEffectiveDate());
	}


	public CorporateDTO getCorporateDTO() {
		return corporateDTO;
	}


	public void setCorporateDTO(CorporateDTO corporateDTO) {
		this.corporateDTO = corporateDTO;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public BigDecimal getLowerLimit() {
		return lowerLimit;
	}

	@Override
	public void setLowerLimit(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	@Override
	public BigDecimal getUpperLimit() {
		return upperLimit;
	}

	@Override
	public void setUpperLimit(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}

	@Override
	public String getCurrency() {
		return currency;
	}

	@Override
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public LocalDateTime getEffectiveDate() {
		return effectiveDate;
	}

	@Override
	public void setEffectiveDate(LocalDateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}
