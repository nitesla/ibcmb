package longbridge.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferRequest implements Serializable{

    public TransferRequest() {
    }

    private String debitAcctNum;
    private String debitAcctName;
    private String creditAcctNum;
    private String creditAcctName;
    private String tranAmount;
    private String naration;
    private String remarks;
    private String destInstCode;
    private String tranType;
    private String status;
    private String refNum;
    private String userRefNum;
    private BigDecimal amount;
    private String charge;
    private String currencyCode;

    private AntiFraudData antiFraudData;
    private String statusDescription;

    private String respCode;
    private String respDescription;
    private String uniqueRefCode;

    private QuickBeneficiary quickBen;
    private QuickInitiation quickInit;
    private QuickSender quickSender;
    private QuickTermination quickTermination;


    public String getDebitAcctNum() {
        return debitAcctNum;
    }

    public void setDebitAcctNum(String debitAcctNum) {
        this.debitAcctNum = debitAcctNum;
    }

    public String getDebitAcctName() {
        return debitAcctName;
    }

    public void setDebitAcctName(String debitAcctName) {
        this.debitAcctName = debitAcctName;
    }

    public String getCreditAcctNum() {
        return creditAcctNum;
    }

    public void setCreditAcctNum(String creditAcctNum) {
        this.creditAcctNum = creditAcctNum;
    }

    public String getCreditAcctName() {
        return creditAcctName;
    }

    public void setCreditAcctName(String creditAcctName) {
        this.creditAcctName = creditAcctName;
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

    public String getDestInstCode() {
        return destInstCode;
    }

    public void setDestInstCode(String destInstCode) {
        this.destInstCode = destInstCode;
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

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public String getUserRefNum() {
        return userRefNum;
    }

    public void setUserRefNum(String userRefNum) {
        this.userRefNum = userRefNum;
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

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespDescription() {
        return respDescription;
    }

    public void setRespDescription(String respDescription) {
        this.respDescription = respDescription;
    }

    public String getUniqueRefCode() {
        return uniqueRefCode;
    }

    public void setUniqueRefCode(String uniqueRefCode) {
        this.uniqueRefCode = uniqueRefCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public QuickBeneficiary getQuickBen() {
        return quickBen;
    }

    public void setQuickBen(QuickBeneficiary quickBen) {
        this.quickBen = quickBen;
    }

    public QuickInitiation getQuickInit() {
        return quickInit;
    }

    public void setQuickInit(QuickInitiation quickInit) {
        this.quickInit = quickInit;
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
                "debitAccountNumber='" + debitAcctNum + '\'' +
                ", debitAccountName='" + debitAcctName + '\'' +
                ", creditAccountNumber='" + creditAcctNum + '\'' +
                ", creditAccountName='" + creditAcctName + '\'' +
                ", tranAmount='" + tranAmount + '\'' +
                ", naration='" + naration + '\'' +
                ", remarks='" + remarks + '\'' +
                ", destinationInstitutionCode='" + destInstCode + '\'' +
                ", tranType='" + tranType + '\'' +
                ", status='" + status + '\'' +
                ", referenceNumber='" + refNum + '\'' +
                ", userReferenceNumber='" + userRefNum + '\'' +
                ", amount=" + amount +
                ", charge='" + charge + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", antiFraudData=" + antiFraudData +
                ", statusDescription='" + statusDescription + '\'' +
                ", responseCode='" + respCode + '\'' +
                ", responseDescription='" + respDescription + '\'' +
                ", uniqueReferenceCode='" + uniqueRefCode + '\'' +
                ", quickBeneficiary=" + quickBen +
                ", quickInitiation=" + quickInit +
                ", quickSender=" + quickSender +
                ", quickTermination=" + quickTermination +
                '}';
    }
}

