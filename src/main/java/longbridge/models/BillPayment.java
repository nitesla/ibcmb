package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fortune on 7/6/2018.
 */

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_flag='N'")
public class BillPayment extends AbstractEntity {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal amount;
    private Long paymentItemId;
    private Long billerId;
    private String customerAccountNumber;
    private String paymentItemName;
    private String phoneNumber;
    private String emailAddress;
    private String billerName;
    private String status ;
    private  Date createdOn = new Date();
    private String terminalId;
    private Long paymentCode;
    private String customerId;
    private String requestReference;
    private String responseCode;
    private String responseCodeGrouping;
    private String approvedAmount;
    private String rechargePin;
    private String transactionRef;
    private String responseDescription;
    private String miscData;
    private String categoryName;


    public BillPayment() {
    }

    public BillPayment(Long id, BigDecimal amount, Long paymentItemId, Long billerId, String customerAccountNumber, String paymentItemName, String phoneNumber, String emailAddress, String billerName, String status, Date createdOn, String terminalId, Long paymentCode, String customerId, String requestReference, String responseCode, String responseCodeGrouping, String approvedAmount, String rechargePin, String transactionRef, String responseDescription, String miscData, String categoryName) {
        this.id = id;
        this.amount = amount;
        this.paymentItemId = paymentItemId;
        this.billerId = billerId;
        this.customerAccountNumber = customerAccountNumber;
        this.paymentItemName = paymentItemName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.billerName = billerName;
        this.status = status;
        this.createdOn = createdOn;
        this.terminalId = terminalId;
        this.paymentCode = paymentCode;
        this.customerId = customerId;
        this.requestReference = requestReference;
        this.responseCode = responseCode;
        this.responseCodeGrouping = responseCodeGrouping;
        this.approvedAmount = approvedAmount;
        this.rechargePin = rechargePin;
        this.transactionRef = transactionRef;
        this.responseDescription = responseDescription;
        this.miscData = miscData;
        this.categoryName = categoryName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getPaymentItemId() {
        return paymentItemId;
    }

    public void setPaymentItemId(Long paymentItemId) {
        this.paymentItemId = paymentItemId;
    }

    public Long getBillerId() {
        return billerId;
    }

    public void setBillerId(Long billerId) {
        this.billerId = billerId;
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

    public void setCreatedOn(Date createdOn) {this.createdOn = createdOn;}

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

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getMiscData() {
        return miscData;
    }

    public void setMiscData(String miscData) {
        this.miscData = miscData;
    }

    public String getPaymentItemName() {
        return paymentItemName;
    }

    public void setPaymentItemName(String paymentItemName) {
        this.paymentItemName = paymentItemName;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "BillPayment{" +
                "id=" + id +
                ", amount=" + amount +
                ", paymentItemId=" + paymentItemId +
                ", billerId=" + billerId +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", paymentItemName='" + paymentItemName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", billerName='" + billerName + '\'' +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                ", terminalId='" + terminalId + '\'' +
                ", paymentCode=" + paymentCode +
                ", customerId='" + customerId + '\'' +
                ", requestReference='" + requestReference + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseCodeGrouping='" + responseCodeGrouping + '\'' +
                ", approvedAmount='" + approvedAmount + '\'' +
                ", rechargePin='" + rechargePin + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", miscData='" + miscData + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
