
package longbridge.billerresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

    private String responseCode;

    private String responseCodeGrouping;

    private String approvedAmount;

    private String rechargePin;

    private String transactionRef;

    private String responseDescription;

    private String miscData;

    public PaymentResponse() {
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

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "responseCode='" + responseCode + '\'' +
                ", responseCodeGrouping='" + responseCodeGrouping + '\'' +
                ", approvedAmount='" + approvedAmount + '\'' +
                ", rechargePin='" + rechargePin + '\'' +
                ", transactionRef='" + transactionRef + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", miscData='" + miscData + '\'' +
                '}';
    }
}
