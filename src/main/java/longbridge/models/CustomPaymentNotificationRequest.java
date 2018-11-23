package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomPaymentNotificationRequest {

    @JsonProperty("TranId")
    private String TranId;
    @JsonProperty("Amount")
    private String Amount;
    @JsonProperty("LastAuthorizer")
    private String LastAuthorizer;
    @JsonProperty("InitiatedBy")
    private String InitiatedBy;
    @JsonProperty("PaymentRef")
    private String PaymentRef;
    @JsonProperty("CustomerAccountNo")
    private String CustomerAccountNo;
    private String appId;
    private String hash;

    public String getTranId() {
        return TranId;
    }

    public void setTranId(String tranId) {
        TranId = tranId;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getLastAuthorizer() {
        return LastAuthorizer;
    }

    public void setLastAuthorizer(String lastAuthorizer) {
        LastAuthorizer = lastAuthorizer;
    }

    public String getInitiatedBy() {
        return InitiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        InitiatedBy = initiatedBy;
    }

    public String getPaymentRef() {
        return PaymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        PaymentRef = paymentRef;
    }

    public String getCustomerAccountNo() {
        return CustomerAccountNo;
    }

    public void setCustomerAccountNo(String customerAccountNo) {
        CustomerAccountNo = customerAccountNo;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "CustomPaymentNotificationRequest{" +
                "TranId='" + TranId + '\'' +
                ", Amount='" + Amount + '\'' +
                ", LastAuthorizer='" + LastAuthorizer + '\'' +
                ", InitiatedBy='" + InitiatedBy + '\'' +
                ", PaymentRef='" + PaymentRef + '\'' +
                ", CustomerAccountNo='" + CustomerAccountNo + '\'' +
                ", appId='" + appId + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
