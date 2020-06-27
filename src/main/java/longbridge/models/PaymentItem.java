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
	private String paymentItemName;
	@Column(name = "AMOUNT")
	private Double amount;
	@Column(name = "CODE")
	private Long code;
	@Column(name = "CURRENCY_CODE")
	private Long currencyCode;
	@Column(name = "ITEM_CURRENCY_SYMBOL")
	private String itemCurrencySymbol;
	@Column(name = "SORT_ORDER")
	private Long sortOrder;
	@Column(name = "PICTURE_ID")
	private Long pictureId;
	@Column(name = "PAYMENT_CODE")
	private Long paymentCode;
	@Column(name = "IS_AMOUNT_FIXED")
	private Boolean isAmountFixed;
	@Column(name = "READONLY")
	private Integer readonly;
	@Column(name = "enabled")
	private boolean enabled;

//	@ManyToOne @JsonIgnore
	private Long billerId;

	public Long getPaymentItemId() {
		return paymentItemId;
	}

	public void setPaymentItemId(Long paymentItemId) {
		this.paymentItemId = paymentItemId;
	}

	public String getPaymentItemName() {
		return paymentItemName;
	}

	public void setPaymentItemName(String paymentItemName) {
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

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Long getPictureId() {
		return pictureId;
	}

	public void setPictureId(Long pictureId) {
		this.pictureId = pictureId;
	}

	public Long getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(Long paymentCode) {
		this.paymentCode = paymentCode;
	}

	public Boolean getIsAmountFixed() {
		return isAmountFixed;
	}

	public void setIsAmountFixed(Boolean isAmountFixed) {
		this.isAmountFixed = isAmountFixed;
	}

	public Integer getReadonly() {
		return readonly;
	}

	public void setReadonly(Integer readonly) {
		this.readonly = readonly;
	}


	public Long getBillerId() {
		return billerId;
	}

	public void setBillerId(Long billerId) {
		this.billerId = billerId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
				", sortOrder=" + sortOrder +
				", pictureId=" + pictureId +
				", paymentCode=" + paymentCode +
				", isAmountFixed=" + isAmountFixed +
				", readonly=" + readonly +
				", billerId=" + billerId +
				'}';
	}
}
