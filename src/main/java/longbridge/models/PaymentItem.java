package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class PaymentItem extends AbstractEntity {
	
	
	private Long paymentItemId;
	private Long paymentItemName;
	private Double amount;
	private Long code;
	private Long currencyCode;
	private String itemCurrencySymbol;
	private Long paymentCode;
	private boolean isAmountFixed;
	private boolean readonly;
	
	@ManyToOne @JsonIgnore
	private Billers billers;

	public Long getPaymentItemId() {
		return paymentItemId;
	}

	public void setPaymentItemId(Long paymentItemId) {
		this.paymentItemId = paymentItemId;
	}

	public Long getPaymentItemName() {
		return paymentItemName;
	}

	public void setPaymentItemName(Long paymentItemName) {
		this.paymentItemName = paymentItemName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getCode() {
		return code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public Long getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(Long currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getItemCurrencySymbol() {
		return itemCurrencySymbol;
	}

	public void setItemCurrencySymbol(String itemCurrencySymbol) {
		this.itemCurrencySymbol = itemCurrencySymbol;
	}

	public Long getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(Long paymentCode) {
		this.paymentCode = paymentCode;
	}

	public boolean isAmountFixed() {
		return isAmountFixed;
	}

	public void setAmountFixed(boolean amountFixed) {
		isAmountFixed = amountFixed;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public Billers getBillers() {
		return billers;
	}

	public void setBillers(Billers billers) {
		this.billers = billers;
	}

	@Override
	public String toString() {
		return "PaymentItem{" +
				"paymentItemId=" + paymentItemId +
				", paymentItemName=" + paymentItemName +
				", amount=" + amount +
				", code=" + code +
				", currencyCode=" + currencyCode +
				", itemCurrencySymbol='" + itemCurrencySymbol + '\'' +
				", paymentCode=" + paymentCode +
				", isAmountFixed=" + isAmountFixed +
				", readonly=" + readonly +
				", billers=" + billers +
				'}';
	}


}
