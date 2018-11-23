package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_Flag='N'" )
//@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomDutyPayment extends AbstractEntity {
    private String dtype;
    private String transferRequestId;
    private String declarantCode;
    private String declarantName;
    private String companyCode;
    private String SGDAssessmentDate;
    private String SADAssessmentSerial;
    private String SADAssessmentNumber;
    private String SADYear;
    private String commandDutyArea;
    private BigDecimal totalAmount;
    private String companyName;
    private String formMNumber;
    private String approvalStatusDesc;
    private String approvalStatus;
    private String tranId;
    private String bankCode;
    private String bankBranchCode;
    private String collectionAccount;
    private String account;
    private String code;
    private String message;
    private String initiatedBy;
    private String lastAuthorizer;
    private String paymentStatus;
    private String notificationStatus;

    @OneToOne
    private CorpPaymentRequest corpPaymentRequest;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Tax> taxs;

    public CustomDutyPayment() {
    }

    public CustomDutyPayment(long customDutyPaymentId, String dtype, String transferRequestId, String declarantCode,
                             String declarantName, String companyCode, String SGDAssessmentDate, String SADAssessmentSerial,
                             String SADAssessmentNumber, String SADYear, String commandDutyArea, BigDecimal totalAmount,
                             String companyName, String formMNumber, String approvalStatusDesc, String approvalStatus,
                             String tranId, String bankCode, String bankBranchCode, String collectionAccount, String account,
                             String code, String message, longbridge.models.CorpPaymentRequest corpPaymentRequest,String lastAuthorizer,
                             String initiatedBy, String paymentStatus, String notificationStatus) {
        this.dtype = dtype;
        this.transferRequestId = transferRequestId;
        this.declarantCode = declarantCode;
        this.declarantName = declarantName;
        this.companyCode = companyCode;
        this.SGDAssessmentDate = SGDAssessmentDate;
        this.SADAssessmentSerial = SADAssessmentSerial;
        this.SADAssessmentNumber = SADAssessmentNumber;
        this.SADYear = SADYear;
        this.commandDutyArea = commandDutyArea;
        this.totalAmount = totalAmount;
        this.companyName = companyName;
        this.formMNumber = formMNumber;
        this.approvalStatusDesc = approvalStatusDesc;
        this.approvalStatus = approvalStatus;
        this.tranId = tranId;
        this.bankCode = bankCode;
        this.bankBranchCode = bankBranchCode;
        this.collectionAccount = collectionAccount;
        this.account = account;
        this.code = code;
        this.message = message;
        this.corpPaymentRequest = corpPaymentRequest;
        this.lastAuthorizer = lastAuthorizer;
        this.initiatedBy = initiatedBy;
        this.paymentStatus = paymentStatus;
        this.notificationStatus = notificationStatus;
    }

   public longbridge.models.CorpPaymentRequest getCorpPaymentRequest() {
        return corpPaymentRequest;
    }

    public void setCorpPaymentRequest(longbridge.models.CorpPaymentRequest corpPaymentRequest) {
        corpPaymentRequest = corpPaymentRequest;
    }


    public String getSADAssessmentSerial() {
        return SADAssessmentSerial;
    }

    public void setSADAssessmentSerial(String SADAssessmentSerial) {
        this.SADAssessmentSerial = SADAssessmentSerial;
    }

    public String getSADAssessmentNumber() {
        return SADAssessmentNumber;
    }

    public void setSADAssessmentNumber(String SADAssessmentNumber) {
        this.SADAssessmentNumber = SADAssessmentNumber;
    }

    public String getSADYear() {
        return SADYear;
    }

    public void setSADYear(String SADYear) {
        this.SADYear = SADYear;
    }

    public String getCommandDutyArea() {
        return commandDutyArea;
    }

    public void setCommandDutyArea(String commandDutyArea) {
        this.commandDutyArea = commandDutyArea;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public String getTransferRequestId() {
        return transferRequestId;
    }

    public void setTransferRequestId(String transferRequestId) {
        this.transferRequestId = transferRequestId;
    }

    public String getDeclarantCode() {
        return declarantCode;
    }

    public void setDeclarantCode(String declarantCode) {
        this.declarantCode = declarantCode;
    }

    public String getDeclarantName() {
        return declarantName;
    }

    public void setDeclarantName(String declarantName) {
        this.declarantName = declarantName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getSGDAssessmentDate() {
        return SGDAssessmentDate;
    }

    public void setSGDAssessmentDate(String SGDAssessmentDate) {
        this.SGDAssessmentDate = SGDAssessmentDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFormMNumber() {
        return formMNumber;
    }

    public void setFormMNumber(String formMNumber) {
        this.formMNumber = formMNumber;
    }

    public String getApprovalStatusDescription() {
        return approvalStatusDesc;
    }

    public void setApprovalStatusDescription(String approvalStatusDesc) {
        this.approvalStatusDesc = approvalStatusDesc;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public List<Tax> getTaxs() {
        return taxs;
    }

    public void setTaxs(List<Tax> taxs) {
        this.taxs = taxs;
    }

    public String getBankBranchCode() {
        return bankBranchCode;
    }

    public void setBankBranchCode(String bankBranchCode) {
        this.bankBranchCode = bankBranchCode;
    }

    public String getCollectionAccount() {
        return collectionAccount;
    }

    public void setCollectionAccount(String collectionAccount) {
        this.collectionAccount = collectionAccount;
    }


    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getLastAuthorizer() {
        return lastAuthorizer;
    }

    public void setLastAuthorizer(String lastAuthorizer) {
        this.lastAuthorizer = lastAuthorizer;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }
}
