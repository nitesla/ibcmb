package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table (name = "PAYMENT_ITEM")
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class PaymentItem extends AbstractEntity {

	@Column(name = "PAYMENT_ITEM_ID")
	private Long paymentItemId;
	@Column(name = "PAYMENT_ITEM_NAME")
	private Long paymentItemName;
	@Column(name = "AMOUNT")
	private Double amount;
	@Column(name = "CODE")
	private Long code;
	@Column(name = "CURRENCY_CODE")
	private Long currencyCode;
	@Column(name = "ITEM_CURRENCY_SYMBOL")
	private String itemCurrencySymbol;
	@Column(name = "PAYMENT_CODE")
	private Long paymentCode;
	@Column(name = "IS_AMOUNT_FIXED")
	private Integer isAmountFixed;
	@Column(name = "READONLY")
	private Integer readonly;
	
//	@ManyToOne @JsonIgnore
//	private Billers biller;

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

	public Integer getIsAmountFixed() {
		return isAmountFixed;
	}

	public void setIsAmountFixed(Integer isAmountFixed) {
		this.isAmountFixed = isAmountFixed;
	}

	public Integer getReadonly() {
		return readonly;
	}

	public void setReadonly(Integer readonly) {
		this.readonly = readonly;
	}

//	public Billers getBiller() {
//		return biller;
//	}
//
//	public void setBiller(Billers biller) {
//		this.biller = biller;
//	}

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
//				", billers=" + biller +
				'}';
	}


}
