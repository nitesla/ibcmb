package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)

public class TransferRequestNip implements Serializable {
    public TransferRequestNip() {
    }

    private String  destinationInstitutionCode;
    private String beneficiaryAccountName;
    private String beneficiaryAccountNumber;
    private String originatorAccountName;
    private String originatorAccountNumber;

    private String narration;
    private String paymentReference;
    private String amount;
    private String transactionChannel;
    private AntiFraudData antiFraudData;

    private String responseCode;
    private String  responseDescription;
    private String  uniqueReferenceCode;
    private String remarks;
    private String tranType;


    public String getDestinationInstitutionCode() {
        return destinationInstitutionCode;
    }

    public void setDestinationInstitutionCode(String destinationInstitutionCode) {
        this.destinationInstitutionCode = destinationInstitutionCode;
    }

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public void setBeneficiaryAccountName(String beneficiaryAccountName) {
        this.beneficiaryAccountName = beneficiaryAccountName;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getOriginatorAccountName() {
        return originatorAccountName;
    }

    public void setOriginatorAccountName(String originatorAccountName) {
        this.originatorAccountName = originatorAccountName;
    }

    public String getOriginatorAccountNumber() {
        return originatorAccountNumber;
    }

    public void setOriginatorAccountNumber(String originatorAccountNumber) {
        this.originatorAccountNumber = originatorAccountNumber;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionChannel() {
        return transactionChannel;
    }

    public void setTransactionChannel(String transactionChannel) {
        this.transactionChannel = transactionChannel;
    }

    public AntiFraudData getAntiFraudData() {
        return antiFraudData;
    }

    public void setAntiFraudData(AntiFraudData antiFraudData) {
        this.antiFraudData = antiFraudData;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    @Override
    public String toString() {
        return "TransferRequestNip{" +
                ", destinationInstitutionCode='" + destinationInstitutionCode + '\'' +
                ", beneficiaryAccountName='" + beneficiaryAccountName + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", originatorAccountName='" + originatorAccountName + '\'' +
                ", originatorAccountNumber='" + originatorAccountNumber + '\'' +
                ", narration='" + narration + '\'' +
                ", paymentReference='" + paymentReference + '\'' +
                ", amount='" + amount + '\'' +
                ", transactionChannel='" + transactionChannel + '\'' +
                ", antiFraudData=" + antiFraudData +
                ", responseCode='" + responseCode + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", uniqueReferenceCode='" + uniqueReferenceCode + '\'' +
                ", remarks='" + remarks + '\'' +
                ", tranType='" + tranType + '\'' +
                '}';
    }
}
