package longbridge.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class QuickDetails {
    private String responseCode;
    private String mac;
    private String transferCode;
    private String transactionDate;
    private String responseCodeGrouping;

    public QuickDetails() {
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getTransferCode() {
        return transferCode;
    }

    public void setTransferCode(String transferCode) {
        this.transferCode = transferCode;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getResponseCodeGrouping() {
        return responseCodeGrouping;
    }

    public void setResponseCodeGrouping(String responseCodeGrouping) {
        this.responseCodeGrouping = responseCodeGrouping;
    }


    @Override
    public String toString() {
        return "QuickDetails{" +
                "responseCode='" + responseCode + '\'' +
                ", mac='" + mac + '\'' +
                ", transferCode='" + transferCode + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", responseCodeGrouping='" + responseCodeGrouping + '\'' +
                '}';
    }
}
