package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomPaymentNotification {

    @JsonProperty("PaymentRef")
    private String paymentRef;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Message")
    private String message;

    public String getPaymentRef() {
        return paymentRef;
    }

    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CustomPaymentNotification{" +
                "paymentRef='" + paymentRef + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
