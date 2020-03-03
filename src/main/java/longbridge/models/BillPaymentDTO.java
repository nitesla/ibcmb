package longbridge.models;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * Created by mac on 22/02/2018.
 */
public class BillPaymentDTO {

    private Long id;
    @NotEmpty(message = "Amount is required")
    private String amount;
    private String category;
    @NotEmpty(message = "Product is required")
    private String productId;
    private String productName;
    @NotEmpty(message = "Merchant is required")
    private String merchantId;
    private String merchantName;
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


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }


    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public void setCustomerIdentifier(String customerIdentifier) {
        this.customerIdentifier = customerIdentifier;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "id=" + id +
                ", amount='" + amount + '\'' +
                ", category='" + category + '\'' +
                ", productId='" + productId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", customerIdentifier='" + customerIdentifier + '\'' +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
