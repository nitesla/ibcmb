package longbridge.dtos;

import longbridge.models.RetailUser;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class RecurringPaymentDTO implements Serializable {

    private Long id;
    private String customerAccountNumber;
    private BigDecimal amount;
    private int intervalDays;
    private Date nextDebitDate;
    private Date dateCreated;
    private RetailUser retailUser;
    private String narration;
    private String start;
    private String end;
    private Date startDate;
    private Date endDate;
    private String categoryName;
    private String paymentItemId;
    private String paymentItemName;
    private String billerId;
    private String billerName;
    private String phoneNumber;
    private String emailAddress;
    private String status ;
    private Date createdOn;
    private String terminalId;
    private Long paymentCode;
    private String customerId;
    private String requestReference;
    private String token;
    private String responseDescription;
    private String transactionRef;
    private boolean authenticate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public RetailUser getRetailUser() {
        return retailUser;
    }

    public void setRetailUser(RetailUser retailUser) {
        this.retailUser = retailUser;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    @Override
    public String toString() {
        return "RecurringPaymentDTO{" +
                "id=" + id +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", amount=" + amount +
                ", intervalDays=" + intervalDays +
                ", nextDebitDate=" + nextDebitDate +
                ", dateCreated=" + dateCreated +
                ", retailUser=" + retailUser +
                ", narration='" + narration + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
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
                ", requestReference='" + requestReference + '\'' +
                ", token='" + token + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                ", authenticate=" + authenticate +
                '}';
    }
}
