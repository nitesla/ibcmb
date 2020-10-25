
package longbridge.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

    private String ResponseCode;

    private String ResponseCodeGrouping;

    private String ApprovedAmount;

    private String RechargePin;

    private String TransactionRef;

    private String ResponseDescription;

    private String MiscData;

    private boolean statusNull=false;

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

    public boolean isStatusNull() {
        return statusNull;
    }

    public void setStatusNull(boolean statusNull) {
        this.statusNull = statusNull;
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
                '}';
    }
}
