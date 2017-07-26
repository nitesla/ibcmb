package longbridge.models;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;


/**
 * The {@code Account} class is a model that shows
 * an Account information
 * @author ayoade farooq
 * Created on 3/28/2017.
 */
@Entity
@Audited(withModifiedFlag=true)
@Where(clause ="del_flag='N'")
public class Account extends AbstractEntity {

    private String accountId;
    private String accountNumber;
    private String accountName;
    private String preferredName;
    private String customerId;
    private String schemeType;
    private String schemeCode;
    private String solId;
    private String primaryFlag;
    private String hiddenFlag;
    private String currencyCode;
    private String status;

    public Account() {
    }

    public Account(String accountId, String accountNumber, String accountName, String customerId, String schemeType, String schemeCode, String solId, String primaryFlag, String hiddenFlag, String currencyCode) {
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

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        if (!super.equals(o)) return false;

        Account account = (Account) o;

//        if (!accountId.equals(account.accountId)) return false;
        if (!accountNumber.equals(account.accountNumber)) return false;
        if (!customerId.equals(account.customerId)) return false;
        return solId != null ? solId.equals(account.solId) : account.solId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
//        result = 31 * result + accountId.hashCode();
        result = 31 * result + accountNumber.hashCode();
        result = 31 * result + customerId.hashCode();
        result = 31 * result + (solId != null ? solId.hashCode() : 0);
        return result;
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
                + ",                         \"currencyCode\":\"" + currencyCode + "\""
                + "}";
    }

//	@Override
//	public String serialize() throws JsonProcessingException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public AbstractEntity deserialize(String data) throws JsonParseException, JsonMappingException, IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
    
    /** Gets a description for the account.
     * This description is of the form: ACCOUNTNUMBER - ACCOUNT CLASS
     * @return
     */
    public String getAccountDescription(){
    	return String.format("%s - %s", getAccountNumber(),getSchemeCode());
    }

	public static OperationCode getAddCode() {
		// TODO Auto-generated method stub
		return null;
	}

	public static OperationCode getModifyCode() {
		// TODO Auto-generated method stub
		return null;
	}

    
}
