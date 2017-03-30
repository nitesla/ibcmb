package longbridge.models;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Investment extends AbstractEntity{

    private int depositPeriodMonths;
    private int depositPeriodDays;
    private String referenceNumber;
    private Date effectiveOpenDate;
    private String currencyCode;
    private BigDecimal maturityAmount;
    private String customerId;
    private double interestRate;
    private Date maturityDate;
    public String fundingAccountName;
    public String fundingAccountNumber;
    public String accountName;
    public String addressLine1;
    public String addressLine2;
    public String addressLine3;
    public String contractRate;

    public int getDepositPeriodMonths() {
        return depositPeriodMonths;
    }

    public void setDepositPeriodMonths(int depositPeriodMonths) {
        this.depositPeriodMonths = depositPeriodMonths;
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

    public Date getEffectiveOpenDate() {
        return effectiveOpenDate;
    }

    public void setEffectiveOpenDate(Date effectiveOpenDate) {
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

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
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
