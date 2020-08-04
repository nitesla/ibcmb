package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import org.joda.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;


@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
public class RecurringPayment extends AbstractEntity {

	@ManyToOne
	private RetailUser retailUser;
	protected String customerAccountNumber;
	protected BigDecimal amount;
	protected int intervalDays;
	protected Date nextDebitDate;
	protected Date dateCreated;
	protected Date startDate;
	protected Date endDate;
	protected String narration;
	protected String categoryName;
	protected String paymentItemId;
	protected String paymentItemName;
	protected String billerId;
	protected String billerName;
	protected String phoneNumber;
	protected String emailAddress;
	protected String status ;
	protected Date createdOn;
	protected String terminalId;
	protected Long paymentCode;
	protected String customerId;
	protected String responseCode;
	protected String responseCodeGrouping;
	protected String approvedAmount;
	protected String rechargePin;
	protected String requestReference;
	protected String token;
	protected String responseDescription;
	protected String transactionRef;
	protected boolean authenticate;
	@OneToMany(mappedBy = "recurringPayment", cascade = CascadeType.ALL)
	protected Collection<PaymentStat> payments;

	@ManyToOne
	CorporateUser corporateUser;

	Long corporate ;

	public RetailUser getRetailUser() {
		return retailUser;
	}

	public void setRetailUser(RetailUser retailUser) {
		this.retailUser = retailUser;
	}

	public String getCustomerAccountNumber() {
		return customerAccountNumber;
	}

	public void setCustomerAccountNumber(String customerAccountNumber) {
		this.customerAccountNumber = customerAccountNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getIntervalDays() {
		return intervalDays;
	}

	public void setIntervalDays(int intervalDays) {
		this.intervalDays = intervalDays;
	}

	public Date getNextDebitDate() {
		return nextDebitDate;
	}

	public void setNextDebitDate(Date nextDebitDate) {
		this.nextDebitDate = nextDebitDate;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getPaymentItemId() {
		return paymentItemId;
	}

	public void setPaymentItemId(String paymentItemId) {
		this.paymentItemId = paymentItemId;
	}

	public String getPaymentItemName() {
		return paymentItemName;
	}

	public void setPaymentItemName(String paymentItemName) {
		this.paymentItemName = paymentItemName;
	}

	public String getBillerId() {
		return billerId;
	}

	public void setBillerId(String billerId) {
		this.billerId = billerId;
	}

	public String getBillerName() {
		return billerName;
	}

	public void setBillerName(String billerName) {
		this.billerName = billerName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Long getPaymentCode() {
		return paymentCode;
	}

	public void setPaymentCode(Long paymentCode) {
		this.paymentCode = paymentCode;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getRequestReference() {
		return requestReference;
	}

	public void setRequestReference(String requestReference) {
		this.requestReference = requestReference;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getResponseDescription() {
		return responseDescription;
	}

	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}

	public String getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}

	public boolean isAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}

	public Collection<PaymentStat> getPayments() {
		return payments;
	}

	public void setPayments(Collection<PaymentStat> payments) {
		this.payments = payments;
	}

	public CorporateUser getCorporateUser() {
		return corporateUser;
	}

	public void setCorporateUser(CorporateUser corporateUser) {
		this.corporateUser = corporateUser;
	}

	public Long getCorporate() {
		return corporate;
	}

	public void setCorporate(Long corporate) {
		this.corporate = corporate;
	}

	public void proceedToNextDebitDate(){
		LocalDate previous = LocalDate.fromDateFields(getDateCreated());
		setNextDebitDate(previous.plusDays(getIntervalDays()).toDate());
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseCodeGrouping() {
		return responseCodeGrouping;
	}

	public void setResponseCodeGrouping(String responseCodeGrouping) {
		this.responseCodeGrouping = responseCodeGrouping;
	}

	public String getApprovedAmount() {
		return approvedAmount;
	}

	public void setApprovedAmount(String approvedAmount) {
		this.approvedAmount = approvedAmount;
	}

	public String getRechargePin() {
		return rechargePin;
	}

	public void setRechargePin(String rechargePin) {
		this.rechargePin = rechargePin;
	}

	@Override
	public String toString() {
		return "RecurringPayment{" +
				"retailUser=" + retailUser +
				", customerAccountNumber='" + customerAccountNumber + '\'' +
				", amount=" + amount +
				", intervalDays=" + intervalDays +
				", nextDebitDate=" + nextDebitDate +
				", dateCreated=" + dateCreated +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", narration='" + narration + '\'' +
				", categoryName='" + categoryName + '\'' +
				", paymentItemId='" + paymentItemId + '\'' +
				", paymentItemName='" + paymentItemName + '\'' +
				", billerId='" + billerId + '\'' +
				", billerName='" + billerName + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", emailAddress='" + emailAddress + '\'' +
				", status='" + status + '\'' +
				", createdOn=" + createdOn +
				", terminalId='" + terminalId + '\'' +
				", paymentCode=" + paymentCode +
				", customerId='" + customerId + '\'' +
				", responseCode='" + responseCode + '\'' +
				", responseCodeGrouping='" + responseCodeGrouping + '\'' +
				", approvedAmount='" + approvedAmount + '\'' +
				", rechargePin='" + rechargePin + '\'' +
				", requestReference='" + requestReference + '\'' +
				", token='" + token + '\'' +
				", responseDescription='" + responseDescription + '\'' +
				", transactionRef='" + transactionRef + '\'' +
				", authenticate=" + authenticate +
				", payments=" + payments +
				", corporateUser=" + corporateUser +
				", corporate=" + corporate +
				", id=" + id +
				'}';
	}
}
