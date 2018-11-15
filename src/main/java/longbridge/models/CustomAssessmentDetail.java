package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CustomAssessmentDetail {

    @JsonProperty("DeclarantCode")
    private String declarantCode;
    @JsonProperty("DeclarantName")
    private String declarantName;
    @JsonProperty("CompanyCode")
    private String companyCode;
    @JsonProperty("SGDAssessmentDate")
    private String SGDAssessmentDate;
    @JsonProperty("TotalAmount")
    private double totalAmount;
    @JsonProperty("CompanyName")
    private String companyName;
    @JsonProperty("FormMNumber")
    private String formMNumber;
    @JsonProperty("ApprovalStatusDescription")
    private String approvalStatusDescription;
    @JsonProperty("ApprovalStatus")
    private String approvalStatus;
    @JsonProperty("TranId")
    private String tranId;
    @JsonProperty("BankCode")
    private String bankCode;
    @JsonProperty("TaxDetails")
    private List<CustomTaxDetail> taxDetails;
    @JsonProperty("BankBranchCode")
    private String bankBranchCode;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Message")
    private String message;

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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
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
        return approvalStatusDescription;
    }

    public void setApprovalStatusDescription(String approvalStatusDescription) {
        this.approvalStatusDescription = approvalStatusDescription;
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

    public List<CustomTaxDetail> getTaxDetails() {
        return taxDetails;
    }

    public void setTaxDetails(List<CustomTaxDetail> taxDetails) {
        this.taxDetails = taxDetails;
    }

    public String getBankBranchCode() {
        return bankBranchCode;
    }

    public void setBankBranchCode(String bankBranchCode) {
        this.bankBranchCode = bankBranchCode;
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

    public CustomAssessmentDetail() {
    }

    @Override
    public String toString() {
        return "CustomAssessmentDetail{" +
                "declarantCode='" + declarantCode + '\'' +
                ", declarantName='" + declarantName + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", SGDAssessmentDate='" + SGDAssessmentDate + '\'' +
                ", totalAmount=" + totalAmount +
                ", companyName='" + companyName + '\'' +
                ", formMNumber='" + formMNumber + '\'' +
                ", approvalStatusDescription='" + approvalStatusDescription + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", tranId='" + tranId + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", taxDetails=" + taxDetails +
                ", bankBranchCode='" + bankBranchCode + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
