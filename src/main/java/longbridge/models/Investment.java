package longbridge.models;

import org.joda.time.LocalDateTime;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Investment extends AbstractEntity{

    private int tenor;
    private int depositPeriodDays;
    private String referenceNumber;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setEffectiveOpenDate(LocalDateTime effectiveOpenDate) {
        this.effectiveOpenDate = effectiveOpenDate;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public void setMaturityDate(LocalDateTime maturityDate) {
        this.maturityDate = maturityDate;
    }

    public int getDepositPeriodDays() {
        return depositPeriodDays;
    }

    public void setDepositPeriodDays(int depositPeriodDays) {
        this.depositPeriodDays = depositPeriodDays;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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
                "depositPeriodDays=" + depositPeriodDays +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", maturityAmount=" + maturityAmount +
                ", customerId='" + customerId + '\'' +
                ", interestRate=" + interestRate +
                ", maturityDate=" + maturityDate +
                ", accountName='" + accountName + '\'' +
                '}';
    }
}
