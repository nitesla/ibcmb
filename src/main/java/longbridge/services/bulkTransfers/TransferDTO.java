package longbridge.services.bulkTransfers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import longbridge.models.AntiFraudData;

import java.io.Serializable;

/**
 * Created by ayoade_farooq@yahoo.com on 6/24/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferDTO implements Serializable {
    private String accountName;
    private String accountNumber;
    private String beneficiaryAccountNumber;
    private String beneficiaryName;
    private String batchId;
    private String email;
    private String narration;
    private String paymentReference;
    private String beneficiaryBankCode;
    private String amount;
    private String payerAccountNumber;
    private String payerName;

    private AntiFraudData antiFraudData;

    private String status;
    private String setStatusDescription;

    private String responseCode;
    private String  responseDescription;
    private String  uniqueReferenceCode;
    private String description;



    public TransferDTO() {
    }

    public TransferDTO(String beneficiaryName, String batchId, String email, String narration, String paymentReference, String beneficiaryBankCode, String amount, String payerAccountNumber, String payerName) {
        this.beneficiaryName = beneficiaryName;
        this.batchId = batchId;
        this.email = email;
        this.narration = narration;
        this.paymentReference = paymentReference;
        this.beneficiaryBankCode = beneficiaryBankCode;
        this.amount = amount;
        this.payerAccountNumber = payerAccountNumber;
        this.payerName = payerName;
    }


    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getBeneficiaryBankCode() {
        return beneficiaryBankCode;
    }

    public void setBeneficiaryBankCode(String beneficiaryBankCode) {
        this.beneficiaryBankCode = beneficiaryBankCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayerAccountNumber() {
        return payerAccountNumber;
    }

    public void setPayerAccountNumber(String payerAccountNumber) {
        this.payerAccountNumber = payerAccountNumber;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getUniqueReferenceCode() {
        return uniqueReferenceCode;
    }

    public void setUniqueReferenceCode(String uniqueReferenceCode) {
        this.uniqueReferenceCode = uniqueReferenceCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSetStatusDescription() {
        return setStatusDescription;
    }

    public void setSetStatusDescription(String setStatusDescription) {
        this.setStatusDescription = setStatusDescription;
    }

    public AntiFraudData getAntiFraudData() {
        return antiFraudData;
    }

    public void setAntiFraudData(AntiFraudData antiFraudData) {
        this.antiFraudData = antiFraudData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TransferDTO{" +
                "accountName='" + accountName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", batchId='" + batchId + '\'' +
                ", email='" + email + '\'' +
                ", narration='" + narration + '\'' +
                ", paymentReference='" + paymentReference + '\'' +
                ", beneficiaryBankCode='" + beneficiaryBankCode + '\'' +
                ", amount='" + amount + '\'' +
                ", payerAccountNumber='" + payerAccountNumber + '\'' +
                ", payerName='" + payerName + '\'' +
                ", antiFraudData=" + antiFraudData +
                ", status='" + status + '\'' +
                ", setStatusDescription='" + setStatusDescription + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", uniqueReferenceCode='" + uniqueReferenceCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
