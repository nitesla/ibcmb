
package longbridge.response;


public class PaymentResponse {

    private String ResponseCode;

    private String ResponseCodeGrouping;

    private String ApprovedAmount;

    private String RechargePin;

    private String TransactionRef;

    private String ResponseDescription;

    private String MiscData;

    private String amount;

    private String currencyCode;

    private String customer;

    private String customerEmail;

    private String customerMobile;

    private String paymentDate;

    private String requestReference;

    private String serviceCode;

    private String serviceName;

    private String serviceProviderId;

    private String status;

    private String surcharge;

    private String transactionResponseCode;

    private String transactionSet;

    public PaymentResponse() {
    }

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getResponseCodeGrouping() {
        return ResponseCodeGrouping;
    }

    public void setResponseCodeGrouping(String responseCodeGrouping) {
        ResponseCodeGrouping = responseCodeGrouping;
    }

    public String getApprovedAmount() {
        return ApprovedAmount;
    }

    public void setApprovedAmount(String approvedAmount) {
        ApprovedAmount = approvedAmount;
    }

    public String getRechargePin() {
        return RechargePin;
    }

    public void setRechargePin(String rechargePin) {
        RechargePin = rechargePin;
    }

    public String getTransactionRef() {
        return TransactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        TransactionRef = transactionRef;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getMiscData() {
        return MiscData;
    }

    public void setMiscData(String miscData) {
        MiscData = miscData;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getRequestReference() {
        return requestReference;
    }

    public void setRequestReference(String requestReference) {
        this.requestReference = requestReference;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(String surcharge) {
        this.surcharge = surcharge;
    }

    public String getTransactionResponseCode() {
        return transactionResponseCode;
    }

    public void setTransactionResponseCode(String transactionResponseCode) {
        this.transactionResponseCode = transactionResponseCode;
    }

    public String getTransactionSet() {
        return transactionSet;
    }

    public void setTransactionSet(String transactionSet) {
        this.transactionSet = transactionSet;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "ResponseCode='" + ResponseCode + '\'' +
                ", ResponseCodeGrouping='" + ResponseCodeGrouping + '\'' +
                ", ApprovedAmount='" + ApprovedAmount + '\'' +
                ", RechargePin='" + RechargePin + '\'' +
                ", TransactionRef='" + TransactionRef + '\'' +
                ", ResponseDescription='" + ResponseDescription + '\'' +
                ", MiscData='" + MiscData + '\'' +
                ", amount='" + amount + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", customer='" + customer + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerMobile='" + customerMobile + '\'' +
                ", paymentDate='" + paymentDate + '\'' +
                ", requestReference='" + requestReference + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceProviderId='" + serviceProviderId + '\'' +
                ", status='" + status + '\'' +
                ", surcharge='" + surcharge + '\'' +
                ", transactionResponseCode='" + transactionResponseCode + '\'' +
                ", transactionSet='" + transactionSet + '\'' +
                '}';
    }
}
