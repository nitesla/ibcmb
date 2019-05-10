package longbridge.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by ayoade_farooq@yahoo.com on 4/25/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo
{

    private String accountNumber;
    private String accountCurrency;
    private String accountOpenDate;
    private String accountName;
    private String accountStatus;
    private String customerId;
    private String schemeType;
    private String schemeCode;
    private String solId;
    private String freezeCode;
    private String availableBalance;
    private String bookBalance;
    private String sanctionLimit;
    private  String drawingPower;


    public AccountInfo(String accountNumber, String accountCurrency, String accountOpenDate, String accountName, String accountStatus, String customerId, String schemeType, String schemeCode, String solId, String freezeCode, String availableBalance, String bookBalance, String sanctionLimit, String drawingPower) {
        this.accountNumber = accountNumber;
        this.accountCurrency = accountCurrency;
        this.accountOpenDate = accountOpenDate;
        this.accountName = accountName;
        this.accountStatus = accountStatus;
        this.customerId = customerId;
        this.schemeType = schemeType;
        this.schemeCode = schemeCode;
        this.solId = solId;
        this.freezeCode = freezeCode;
        this.availableBalance = availableBalance;
        this.bookBalance = bookBalance;
        this.sanctionLimit = sanctionLimit;
        this.drawingPower = drawingPower;
    }


    public AccountInfo() {
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public String getAccountOpenDate() {
        return accountOpenDate;
    }

    public void setAccountOpenDate(String accountOpenDate) {
        this.accountOpenDate = accountOpenDate;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getSolId() {
        return solId;
    }

    public void setSolId(String solId) {
        this.solId = solId;
    }

    public String getFreezeCode() {
        return freezeCode;
    }

    public void setFreezeCode(String freezeCode) {
        this.freezeCode = freezeCode;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getBookBalance() {
        return bookBalance;
    }

    public void setBookBalance(String bookBalance) {
        this.bookBalance = bookBalance;
    }

    public String getSanctionLimit() {
        return sanctionLimit;
    }

    public void setSanctionLimit(String sanctionLimit) {
        this.sanctionLimit = sanctionLimit;
    }

    public String getDrawingPower() {
        return drawingPower;
    }

    public void setDrawingPower(String drawingPower) {
        this.drawingPower = drawingPower;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "accountNumber='" + accountNumber + '\'' +
                ", accountCurrency='" + accountCurrency + '\'' +
                ", accountOpenDate='" + accountOpenDate + '\'' +
                ", accountName='" + accountName + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", customerId='" + customerId + '\'' +
                ", schemeType='" + schemeType + '\'' +
                ", schemeCode='" + schemeCode + '\'' +
                ", solId='" + solId + '\'' +
                ", freezeCode='" + freezeCode + '\'' +
                ", availableBalance='" + availableBalance + '\'' +
                ", bookBalance='" + bookBalance + '\'' +
                ", sanctionLimit='" + sanctionLimit + '\'' +
                ", drawingPower='" + drawingPower + '\'' +
                '}';
    }
}
