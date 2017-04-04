package longbridge.models;

import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Created by Chigozirim Torti on 27/03/2017.
 */
public class Investment {

    private int tenor;
    private int depositPeriodDays;
    private String accountNumber;
    private String accountId;
    private LocalDateTime effectiveOpenDate;
    private String currencyCode;
    private BigDecimal maturityAmount;
    private BigDecimal depositAmount;
    private String customerId;
    private double interestRate;
    private LocalDateTime maturityDate;
    private String fundingAccountName;
    private String fundingAccountNumber;
    private String accountName;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String contractRate;

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public int getDepositPeriodDays() {
        return depositPeriodDays;
    }

    public void setDepositPeriodDays(int depositPeriodDays) {
        this.depositPeriodDays = depositPeriodDays;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getEffectiveOpenDate() {
        return effectiveOpenDate;
    }

    public void setEffectiveOpenDate(LocalDateTime effectiveOpenDate) {
        this.effectiveOpenDate = effectiveOpenDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(BigDecimal maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDateTime getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDateTime maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getFundingAccountName() {
        return fundingAccountName;
    }

    public void setFundingAccountName(String fundingAccountName) {
        this.fundingAccountName = fundingAccountName;
    }

    public String getFundingAccountNumber() {
        return fundingAccountNumber;
    }

    public void setFundingAccountNumber(String fundingAccountNumber) {
        this.fundingAccountNumber = fundingAccountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public String getContractRate() {
        return contractRate;
    }

    public void setContractRate(String contractRate) {
        this.contractRate = contractRate;
    }

    @Override
    public String toString() {
        return "Investment{" +
                "tenor=" + tenor +
                ", depositPeriodDays=" + depositPeriodDays +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountId='" + accountId + '\'' +
                ", effectiveOpenDate=" + effectiveOpenDate +
                ", currencyCode='" + currencyCode + '\'' +
                ", maturityAmount=" + maturityAmount +
                ", depositAmount=" + depositAmount +
                ", customerId='" + customerId + '\'' +
                ", interestRate=" + interestRate +
                ", maturityDate=" + maturityDate +
                ", fundingAccountName='" + fundingAccountName + '\'' +
                ", fundingAccountNumber='" + fundingAccountNumber + '\'' +
                ", accountName='" + accountName + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", addressLine3='" + addressLine3 + '\'' +
                ", contractRate='" + contractRate + '\'' +
                '}';
    }
}
