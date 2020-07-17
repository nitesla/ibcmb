package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */

public class QuicktellerRequestDTO implements Serializable {


    @JsonProperty("DT_RowId")
    private Long id;
    private String amount;
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
    private Long PaymentCode;
    private String CustomerId;
    private String requestReference;
    private String token;
    private String sessionId;


    public QuicktellerRequestDTO() {
    }

    public QuicktellerRequestDTO(Long id, String amount, String categoryName, String paymentItemId, String paymentItemName, String billerId, String billerName, String customerAccountNumber, String phoneNumber, String emailAddress, String status, Date createdOn, String terminalId, Long paymentCode, String customerId, String requestReference, String token, String sessionId) {
        this.id = id;
        this.amount = amount;
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
        PaymentCode = paymentCode;
        CustomerId = customerId;
        this.requestReference = requestReference;
        this.token = token;
        this.sessionId = sessionId;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public Long getPaymentCode() {
        return PaymentCode;
    }

    public void setPaymentCode(Long paymentCode) {
        PaymentCode = paymentCode;
    }

    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String customerId) {
        CustomerId = customerId;
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

    @Override
    public String toString() {
        return "QuicktellerRequestDTO{" +
                "id=" + id +
                ", amount='" + amount + '\'' +
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
                ", PaymentCode=" + PaymentCode +
                ", CustomerId='" + CustomerId + '\'' +
                ", requestReference='" + requestReference + '\'' +
                ", token='" + token + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
