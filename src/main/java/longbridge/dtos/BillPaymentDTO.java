package longbridge.dtos;

import javax.validation.constraints.NotEmpty;
import java.util.Date;


public class BillPaymentDTO {

    private Long id;
    @NotEmpty(message = "Amount is required")
    private String amount;
    private String categoryName;
    @NotEmpty(message = "Payment Item is required")
    private String paymentItemId;
    private String paymentItemName;
    @NotEmpty(message = "Biller is required")
    private String billerId;
    private String billerName;
    @NotEmpty(message = "Customer Identifier is required")
    private String customerIdentifier;
    private String customerAccountNumber;
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;
    @NotEmpty(message = "Email address is required")
    private String emailAddress;
    private String status ;
    private Date createdOn;


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

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public void setCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
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

    @Override
    public String toString() {
        return "BillPaymentDTO{" +
                "id=" + id +
                ", amount='" + amount + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", paymentItemId='" + paymentItemId + '\'' +
                ", paymentItemName='" + paymentItemName + '\'' +
                ", billerId='" + billerId + '\'' +
                ", billerName='" + billerName + '\'' +
                ", customerIdentifier='" + customerIdentifier + '\'' +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                '}';
    }
}
