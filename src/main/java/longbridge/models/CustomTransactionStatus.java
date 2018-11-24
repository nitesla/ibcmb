package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomTransactionStatus {
    @JsonProperty("PaymentStatus")
    private String PaymentStatus;
    @JsonProperty("ApprovalStatus")
    private String ApprovalStatus;
    @JsonProperty("NotificationStatus")
    private String NotificationStatus;
    @JsonProperty("PaymentRef")
    private String PaymentRef;
    @JsonProperty("Code")
    private String Code;
    @JsonProperty("Message")
    private String Message;

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getApprovalStatus() {
        return ApprovalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        ApprovalStatus = approvalStatus;
    }

    public String getNotificationStatus() {
        return NotificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        NotificationStatus = notificationStatus;
    }

    public String getPaymentRef() {
        return PaymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.PaymentRef = paymentRef;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        this.Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    @Override
    public String toString() {
        return "CustomTransactionStatus{" +
                "PaymentStatus='" + PaymentStatus + '\'' +
                ", ApprovalStatus='" + ApprovalStatus + '\'' +
                ", NotificationStatus='" + NotificationStatus + '\'' +
                ", PaymentRef='" + PaymentRef + '\'' +
                ", Code='" + Code + '\'' +
                ", Message='" + Message + '\'' +
                '}';
    }
}
