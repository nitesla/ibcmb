package longbridge.services.bulkTransfers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 8/5/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatus {
    private String tranxStatus;
    private String batchProcessingStatus;
    private String beneficiaryAccountNumber;
    private String beneficiaryName;
    private String amount;
    private String batchId;
    private String paymentReference;


    public String getTranxStatus() {
        return tranxStatus;
    }

    public void setTranxStatus(String tranxStatus) {
        this.tranxStatus = tranxStatus;
    }

    public String getBatchProcessingStatus() {
        return batchProcessingStatus;
    }

    public void setBatchProcessingStatus(String batchProcessingStatus) {
        this.batchProcessingStatus = batchProcessingStatus;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    @Override
    public String toString() {
        return "TransactionStatus{" +
                "tranxStatus='" + tranxStatus + '\'' +
                ", batchProcessingStatus='" + batchProcessingStatus + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", amount='" + amount + '\'' +
                ", batchId='" + batchId + '\'' +
                ", paymentReference='" + paymentReference + '\'' +
                '}';
    }
}
