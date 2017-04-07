package longbridge.dtos;

import longbridge.models.AbstractEntity;

import javax.persistence.Entity;



public class AccountDTO{

    private Long id;
    private String accountId;
    private String accountNumber;
    private String accountName;
    private  String customerId;
    private String schemeType;
    private  String schemeCode;
    private  String solId;

    public AccountDTO() {
    }

    public AccountDTO(String accountId, String accountNumber, String accountName, String customerId, String schemeType, String schmCode, String solId) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.customerId = customerId;
        this.schemeType = schemeType;
        this.schemeCode = schmCode;
        this.solId = solId;
    }


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
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



    @Override
    public String toString() {
        return "{\"Account\":"
                + super.toString()
                + ",                         \"accountId\":\"" + accountId + "\""
                + ",                         \"accountNumber\":\"" + accountNumber + "\""
                + ",                         \"accountName\":\"" + accountName + "\""
                + ",                         \"customerId\":\"" + customerId + "\""
                + ",                         \"schemeType\":\"" + schemeType + "\""
                + ",                         \"schmCode\":\"" + schemeCode + "\""
                + ",                         \"solId\":\"" + solId + "\""
                + "}";
    }



}
