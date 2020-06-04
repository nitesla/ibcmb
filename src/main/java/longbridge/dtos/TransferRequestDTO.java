package longbridge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import longbridge.models.*;
import longbridge.utils.TransferType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */

public class TransferRequestDTO implements Serializable {


    @JsonProperty("DT_RowId")
    private Long id;
    private int version;
    private String customerAccountNumber;
    private TransferType transferType;

    private FinancialInstitution financialInstitution;

    private String beneficiaryBank;

    private String beneficiaryAccountNumber;

    private String beneficiaryAccountName;

    private String remarks;

    private String status;

    private Date tranDate = new Date();

    private String referenceNumber;

    private String userReferenceNumber;

    private String narration;

    private String sessionId;

    private String currencyCode;

    private BigDecimal amount;
    private String statusDescription;
    private String charge;
    private String token;
    private String addBeneficiaryFlag;
    private String beneficiaryPrefferedName;

    //below fields are for antiFraudData
    private String countryCode;
    private String deviceNumber;
    private String headerProxyAuthorization;
    private String headerUserAgent;
    private String ip;
    private String loginName;
    private String sessionkey;
    private String  sfactorAuthIndicator;
    private String tranLocation;
    private String channel;


    public TransferRequestDTO() {
    }

    public TransferRequestDTO(Long id, int version, String customerAccountNumber, TransferType transferType, FinancialInstitution financialInstitution, String beneficiaryBank,String beneficiaryAccountNumber, String beneficiaryAccountName, String remarks, String status, String referenceNumber, String userReferenceNumber, String narration, String sessionId, BigDecimal amount, String statusDescription) {
        this.id = id;
        this.version = version;
        this.customerAccountNumber = customerAccountNumber;
        this.transferType = transferType;
        this.financialInstitution = financialInstitution;
        this.beneficiaryBank = beneficiaryBank;
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
    public String getBeneficiaryPrefferedName() {
        return beneficiaryPrefferedName;
    }

    public void setBeneficiaryPrefferedName(String beneficiaryPrefferedName) {
        this.beneficiaryPrefferedName = beneficiaryPrefferedName;
    }

    public String getAddBeneficiaryFlag() {
        return addBeneficiaryFlag;
    }

    public void setAddBeneficiaryFlag(String addBeneficiaryFlag) {
        this.addBeneficiaryFlag = addBeneficiaryFlag;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getBeneficiaryBank() {
        return beneficiaryBank;
    }

    public void setBeneficiaryBank(String beneficiaryBank) {
        this.beneficiaryBank = beneficiaryBank;
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



    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
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

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getHeaderProxyAuthorization() {
        return headerProxyAuthorization;
    }

    public void setHeaderProxyAuthorization(String headerProxyAuthorization) {
        this.headerProxyAuthorization = headerProxyAuthorization;
    }

    public String getHeaderUserAgent() {
        return headerUserAgent;
    }

    public void setHeaderUserAgent(String headerUserAgent) {
        this.headerUserAgent = headerUserAgent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getSessionkey() {
        return sessionkey;
    }

    public void setSessionkey(String sessionkey) {
        this.sessionkey = sessionkey;
    }

    public String getSfactorAuthIndicator() {
        return sfactorAuthIndicator;
    }

    public void setSfactorAuthIndicator(String sfactorAuthIndicator) {
        this.sfactorAuthIndicator = sfactorAuthIndicator;
    }

    public String getTranLocation() {
        return tranLocation;
    }

    public void setTranLocation(String tranLocation) {
        this.tranLocation = tranLocation;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public String toString() {
        return "TransferRequestDTO{" +
                "id=" + id +
                ", version=" + version +
                ", customerAccountNumber='" + customerAccountNumber + '\'' +
                ", transferType=" + transferType +
                ", financialInstitution=" + financialInstitution +
                ", beneficiaryBank='" + beneficiaryBank + '\'' +
                ", beneficiaryAccountNumber='" + beneficiaryAccountNumber + '\'' +
                ", beneficiaryAccountName='" + beneficiaryAccountName + '\'' +
                ", remarks='" + remarks + '\'' +
                ", status='" + status + '\'' +
                ", tranDate=" + tranDate +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", userReferenceNumber='" + userReferenceNumber + '\'' +
                ", narration='" + narration + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", amount=" + amount +
                ", statusDescription='" + statusDescription + '\'' +
                ", charge='" + charge + '\'' +
                ", token='" + token + '\'' +
                ", addBeneficiaryFlag='" + addBeneficiaryFlag + '\'' +
                ", beneficiaryPrefferedName='" + beneficiaryPrefferedName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", deviceNumber='" + deviceNumber + '\'' +
                ", headerProxyAuthorization='" + headerProxyAuthorization + '\'' +
                ", headerUserAgent='" + headerUserAgent + '\'' +
                ", ip='" + ip + '\'' +
                ", loginName='" + loginName + '\'' +
                ", sessionkey='" + sessionkey + '\'' +
                ", sfactorAuthIndicator='" + sfactorAuthIndicator + '\'' +
                ", tranLocation='" + tranLocation + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
