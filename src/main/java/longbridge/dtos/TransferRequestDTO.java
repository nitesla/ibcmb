package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.*;
import longbridge.utils.TransferType;

import java.io.Serializable;
import java.math.BigDecimal;

/** *
 * Created by Fortune on 4/5/2017.
 *
 */

public class TransferRequestDTO implements Serializable{


    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String customerAccountNumber;
    private TransferType transferType;

    private FinancialInstitution financialInstitution;

    private String beneficiaryAccountNumber;

    private String beneficiaryAccountName;

    private String remarks;

    private String status;

    private String referenceNumber;

    private String userReferenceNumber;

    private String narration;

    private String sessionId;

    private BigDecimal amount;
    private String statusDescription;


    public TransferRequestDTO() {
    }

    public TransferRequestDTO(Long id, int version, String customerAccountNumber, TransferType transferType, FinancialInstitution financialInstitution, String beneficiaryAccountNumber, String beneficiaryAccountName, String remarks, String status, String referenceNumber, String userReferenceNumber, String narration, String sessionId, BigDecimal amount, String statusDescription) {
        this.id = id;
        this.version = version;
        this.customerAccountNumber = customerAccountNumber;
        this.transferType = transferType;
        this.financialInstitution = financialInstitution;
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.beneficiaryAccountName = beneficiaryAccountName;
        this.remarks = remarks;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.userReferenceNumber = userReferenceNumber;
        this.narration = narration;
        this.sessionId = sessionId;
        this.amount = amount;
        this.statusDescription = statusDescription;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCustomerAccountNumber() {
        return customerAccountNumber;
    }

    public void setCustomerAccountNumber(String customerAccountNumber) {
        this.customerAccountNumber = customerAccountNumber;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }

    public FinancialInstitution getFinancialInstitution() {
        return financialInstitution;
    }

    public void setFinancialInstitution(FinancialInstitution financialInstitution) {
        this.financialInstitution = financialInstitution;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getBeneficiaryAccountName() {
        return beneficiaryAccountName;
    }

    public void setBeneficiaryAccountName(String beneficiaryAccountName) {
        this.beneficiaryAccountName = beneficiaryAccountName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String toString() {
        return "TransferRequestDTO{" +
                ", version=" + version +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", transferType=" + transferType +
                ", financialInstitution=" + financialInstitution +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", beneficiaryAccountName='" + beneficiaryAccountName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", status='" + status + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", userReferenceNumber='" + userReferenceNumber + '\'' +
                ", narration='" + narration + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", amount=" + amount +
                ", statusDescription='" + statusDescription + '\'' +
                '}';
    }

}
