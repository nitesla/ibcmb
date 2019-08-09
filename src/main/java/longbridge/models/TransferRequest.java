package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferRequest implements Serializable{

    public TransferRequest() {
    }

    private String debitAccountNumber;
    private String debitAccountName;
    private String creditAccountNumber;
    private String creditAccountName;
    private String tranAmount;
    private String naration;
    private String remarks;
    private String destinationInstitutionCode;
    private String tranType;
    private String status;
    private String referenceNumber;
    private String userReferenceNumber;
    private BigDecimal amount;
    private String charge;
    private AntiFraudData antiFraudData;
    private String statusDescription;

    private String responseCode;
    private String  responseDescription;
    private String  uniqueReferenceCode;


    public String getDebitAccountNumber() {
        return debitAccountNumber;
    }

    public void setDebitAccountNumber(String debitAccountNumber) {
        this.debitAccountNumber = debitAccountNumber;
    }

    public String getDebitAccountName() {
        return debitAccountName;
    }

    public void setDebitAccountName(String debitAccountName) {
        this.debitAccountName = debitAccountName;
    }

    public String getCreditAccountNumber() {
        return creditAccountNumber;
    }

    public void setCreditAccountNumber(String creditAccountNumber) {
        this.creditAccountNumber = creditAccountNumber;
    }

    public String getCreditAccountName() {
        return creditAccountName;
    }

    public void setCreditAccountName(String creditAccountName) {
        this.creditAccountName = creditAccountName;
    }

    public String getTranAmount() {
        return tranAmount;
    }

    public void setTranAmount(String tranAmount) {
        this.tranAmount = tranAmount;
    }

    public String getNaration() {
        return naration;
    }

    public void setNaration(String naration) {
        this.naration = naration;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDestinationInstitutionCode() {
        return destinationInstitutionCode;
    }

    public void setDestinationInstitutionCode(String destinationInstitutionCode) {
        this.destinationInstitutionCode = destinationInstitutionCode;
    }

    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
    }

    public AntiFraudData getAntiFraudData() {
        return antiFraudData;
    }

    public void setAntiFraudData(AntiFraudData antiFraudData) {
        this.antiFraudData = antiFraudData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getUserReferenceNumber() {
        return userReferenceNumber;
    }

    public void setUserReferenceNumber(String userReferenceNumber) {
        this.userReferenceNumber = userReferenceNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
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

    @Override
    public String toString() {
        return "TransferRequest{" +
                "debitAccountNumber='" + debitAccountNumber + '\'' +
                ", debitAccountName='" + debitAccountName + '\'' +
                ", creditAccountNumber='" + creditAccountNumber + '\'' +
                ", creditAccountName='" + creditAccountName + '\'' +
                ", tranAmount='" + tranAmount + '\'' +
                ", naration='" + naration + '\'' +
                ", remarks='" + remarks + '\'' +
                ", destinationInstitutionCode='" + destinationInstitutionCode + '\'' +
                ", tranType='" + tranType + '\'' +
                ", status='" + status + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", userReferenceNumber='" + userReferenceNumber + '\'' +
                ", amount=" + amount +
                ", charge='" + charge + '\'' +
                ", antiFraudData=" + antiFraudData +
                ", statusDescription='" + statusDescription + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", uniqueReferenceCode='" + uniqueReferenceCode + '\'' +
                '}';
    }
}

