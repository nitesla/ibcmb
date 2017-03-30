package longbridge.models;

import javax.persistence.*;

import java.util.Collection;


/**
 * The {@code Account} class is a model that shows
 * an Account information
 * @author ayoade farooq
 * Created on 3/28/2017.
 */

@Entity
@Table(name = "ACCOUNT_TABLE",/*SCHEMA NAME WILL COME IN LATER*/
        schema = " ",//TODO
        uniqueConstraints=
        @UniqueConstraint(columnNames={"accountId", "accountNumber"})
)
public class Account extends AbstractEntity{


    private String accountId;
    private String accountNumber;
    private String accountName;
    private  String customerId;
    private String schemeType;
    private  String schemeCode;
    private  String solId;

    public Account() {
    }

    public Account(String accountId, String accountNumber, String accountName, String customerId, String schemeType, String schmCode, String solId) {
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

    @OneToMany(mappedBy = "account")
    private Collection<FinancialTransaction> financialTransactions;

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

    public Collection<FinancialTransaction> getFinancialTransactions() {
        return financialTransactions;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;
		return true;
	}

	//TODO: Update tostring
	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", accountNumber=" + accountNumber + ", accountName=" + accountName
				+ ", customerId=" + customerId + ", schemeType=" + schemeType + ", schemeCode=" + schemeCode
				+ ", solId=" + solId + ", financialTransactions=" + financialTransactions + "]";
	}

   


    

}
