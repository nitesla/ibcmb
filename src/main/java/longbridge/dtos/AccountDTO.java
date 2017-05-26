package longbridge.dtos;

import org.hibernate.validator.constraints.NotEmpty;

public class AccountDTO{

    private Long id;
    private String accountId;
    @NotEmpty(message = "accountNumber")
    private String accountNumber;
    private String accountName;
    private String customerId;
    private String schemeType;
    private String schemeCode;
    private String solId;
    private String primaryFlag;
    private String hiddenFlag;
    private String currencyCode;

    
    private String accountType;
    private String accountBalance;
    private String ledgerBalance;
    
    public AccountDTO() {
    }

    public AccountDTO(Long id, String accountId, String accountNumber, String accountName, String customerId, String schemeType, String schemeCode, String solId, String primaryFlag, String hiddenFlag, String currencyCode) {
        this.id = id;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.customerId = customerId;
        this.schemeType = schemeType;
        this.schemeCode = schemeCode;
        this.solId = solId;
        this.primaryFlag = primaryFlag;
        this.hiddenFlag = hiddenFlag;
        this.currencyCode=currencyCode;
    }

    public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}

	

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPrimaryFlag() {
        return primaryFlag;
    }

    public void setPrimaryFlag(String primaryFlag) {
        this.primaryFlag = primaryFlag;
    }

    public String getHiddenFlag() {
        return hiddenFlag;
    }

    public void setHiddenFlag(String hiddenFlag) {
        this.hiddenFlag = hiddenFlag;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getLedgerBalance() {
        return ledgerBalance;
    }

    public void setLedgerBalance(String ledgerBalance) {
        this.ledgerBalance = ledgerBalance;
    }

    @Override
    public String toString() {
        return "AccountDTO{"
                + super.toString() +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountName='" + accountName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", schemeType='" + schemeType + '\'' +
                ", schemeCode='" + schemeCode + '\'' +
                ", solId='" + solId + '\'' +
                ", primaryFlag='" + primaryFlag + '\'' +
                ", hiddenFlag='" + hiddenFlag + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }

    //
}
