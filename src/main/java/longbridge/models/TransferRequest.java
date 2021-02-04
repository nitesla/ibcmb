package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferRequest implements Serializable{

    public TransferRequest() {
    }

    @Column(name = "dbt_acct_num")
    private String debitAccountNumber;
    @Column(name = "dbt_acct_name")
    private String debitAccountName;
    @Column(name = "cdt_acct_num")
    private String creditAccountNumber;
    @Column(name = "cdt_acct_name")
    private String creditAccountName;
    @Column(name = "tran_amt")
    private String tranAmount;
    private String naration;
    private String remarks;
    @Column(name = "dest_inst_cod")
    private String destinationInstitutionCode;
    private String tranType;
    private String status;
    @Column(name = "ref_num")
    private String referenceNumber;
    @Column(name = "usr_ref_num")
    private String userReferenceNumber;
    private BigDecimal amount;
    private String charge;
    private String currencyCode;

    private AntiFraudData antiFraudData;
    @Column(name = "stat_descpt")
    private String statusDescription;

    private String responseCode;
    @Column(name = "resp_descpt")
    private String  responseDescription;
    @Column(name = "uniq_ref_cod")
    private String  uniqueReferenceCode;

    @Column(name = "quick_ben")
    private QuickBeneficiary quickBeneficiary;
    @Column(name = "quick_init")
    private QuickInitiation quickInitiation;
    private QuickSender quickSender;
    @Column(name = "quick_term")
    private QuickTermination quickTermination;


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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public QuickBeneficiary getQuickBeneficiary() {
        return quickBeneficiary;
    }

    public void setQuickBeneficiary(QuickBeneficiary quickBeneficiary) {
        this.quickBeneficiary = quickBeneficiary;
    }

    public QuickInitiation getQuickInitiation() {
        return quickInitiation;
    }

    public void setQuickInitiation(QuickInitiation quickInitiation) {
        this.quickInitiation = quickInitiation;
    }

    public QuickSender getQuickSender() {
        return quickSender;
    }

    public void setQuickSender(QuickSender quickSender) {
        this.quickSender = quickSender;
    }

    public QuickTermination getQuickTermination() {
        return quickTermination;
    }

    public void setQuickTermination(QuickTermination quickTermination) {
        this.quickTermination = quickTermination;
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
                ", currencyCode='" + currencyCode + '\'' +
                ", antiFraudData=" + antiFraudData +
                ", statusDescription='" + statusDescription + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", responseDescription='" + responseDescription + '\'' +
                ", uniqueReferenceCode='" + uniqueReferenceCode + '\'' +
                ", quickBeneficiary=" + quickBeneficiary +
                ", quickInitiation=" + quickInitiation +
                ", quickSender=" + quickSender +
                ", quickTermination=" + quickTermination +
                '}';
    }
}

