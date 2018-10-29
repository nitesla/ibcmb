package longbridge.models;

public class CustomPaymentNotificationRequest {

    private String TranId;
    private String  Amount;
    private String  LastAuthorizer;
    private String InitiatedBy;
    private String  PaymentRef;
    private String CustomerAccountNo;
    private String  appId;
    private String  hash;

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
}
