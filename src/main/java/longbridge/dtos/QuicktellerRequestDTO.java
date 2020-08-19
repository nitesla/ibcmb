package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */

public class QuicktellerRequestDTO implements Serializable {


    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String categoryName;
    private String paymentItemId;
    private String paymentItemName;
    private String billerId;
    private String billerName;
    private String customerAccountNumber;
    private String phoneNumber;
    private String emailAddress;
    private String status ;
    private Date createdOn = new Date();
    private String terminalId;
    private String requestReference;
    private String token;
    private String sessionId;
    private String remarks;
    private Date tranDate = new Date();
    private BigDecimal amount;
    private int intervalDays;
    private Date nextDebitDate;
    private Date dateCreated;
    private Date startDate;
    private Date endDate;
    private String narration;
    private Long paymentCode;
    private String customerId;
    private String responseCode;
    private String responseCodeGrouping;
    private String approvedAmount;
    private String rechargePin;
    private String responseDescription;
    private String transactionRef;
    private String statusDescription;



    public QuicktellerRequestDTO() {
    }

    public QuicktellerRequestDTO(Long id, int version, String categoryName, String paymentItemId, String paymentItemName, String billerId, String billerName, String customerAccountNumber, String phoneNumber, String emailAddress, String status, Date createdOn, String terminalId, String requestReference, String token, String sessionId, String remarks, Date tranDate, BigDecimal amount, int intervalDays, Date nextDebitDate, Date dateCreated, Date startDate, Date endDate, String narration, Long paymentCode, String customerId, String responseCode, String responseCodeGrouping, String approvedAmount, String rechargePin, String responseDescription, String transactionRef, String statusDescription) {
        this.id = id;
        this.version = version;
        this.categoryName = categoryName;
        this.paymentItemId = paymentItemId;
        this.paymentItemName = paymentItemName;
        this.billerId = billerId;
        this.billerName = billerName;
        this.customerAccountNumber = customerAccountNumber;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.status = status;
        this.createdOn = createdOn;
        this.terminalId = terminalId;
        this.requestReference = requestReference;
        this.token = token;
        this.sessionId = sessionId;
        this.remarks = remarks;
        this.tranDate = tranDate;
        this.amount = amount;
        this.intervalDays = intervalDays;
        this.nextDebitDate = nextDebitDate;
        this.dateCreated = dateCreated;
        this.startDate = startDate;
        this.endDate = endDate;
        this.narration = narration;
        this.paymentCode = paymentCode;
        this.customerId = customerId;
        this.responseCode = responseCode;
        this.responseCodeGrouping = responseCodeGrouping;
        this.approvedAmount = approvedAmount;
        this.rechargePin = rechargePin;
        this.responseDescription = responseDescription;
        this.transactionRef = transactionRef;
        this.statusDescription = statusDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
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

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String toString() {
        return "QuicktellerRequestDTO{" +
                "id=" + id +
                ", version=" + version +
                ", categoryName='" + categoryName + '\'' +
                ", paymentItemId='" + paymentItemId + '\'' +
                ", paymentItemName='" + paymentItemName + '\'' +
                ", billerId='" + billerId + '\'' +
                ", billerName='" + billerName + '\'' +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                ", terminalId='" + terminalId + '\'' +
                ", requestReference='" + requestReference + '\'' +
                ", token='" + token + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", remarks='" + remarks + '\'' +
                ", tranDate=" + tranDate +
                ", amount=" + amount +
                ", intervalDays=" + intervalDays +
                ", nextDebitDate=" + nextDebitDate +
                ", dateCreated=" + dateCreated +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", narration='" + narration + '\'' +
                ", paymentCode=" + paymentCode +
                ", customerId='" + customerId + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseCodeGrouping='" + responseCodeGrouping + '\'' +
                ", approvedAmount='" + approvedAmount + '\'' +
                ", rechargePin='" + rechargePin + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                '}';
    }
}
