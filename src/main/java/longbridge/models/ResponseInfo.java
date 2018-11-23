package longbridge.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ResponseInfo {
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
    private List<Tax> taxDetails;
    @JsonProperty("BankBranchCode")
    private String bankBranchCode;

    @JsonProperty("CollectionAccount")
    private String collectionAccount;

    @JsonProperty("Version")
    private String version;

    public ResponseInfo() {
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

    public List<Tax> getTaxDetails() {
        return taxDetails;
    }

    public void setTaxDetails(List<Tax> taxDetails) {
        this.taxDetails = taxDetails;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ResponseInfo{" +
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
                ", collectionAccount='" + collectionAccount + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
