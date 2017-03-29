package longbridge.models;

import javax.persistence.*;
/**
 * The {@code Account} class is a model that shows
 * an Account information
 * @author ayoade_farooq@yahoo.com
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
    private  String custId;
    private String schmType;
    private  String schmCode;
    private  String solId;

    public Account() {
    }

    public Account(String accountId, String accountNumber, String accountName, String custId, String schmType, String schmCode, String solId) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.custId = custId;
        this.schmType = schmType;
        this.schmCode = schmCode;
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

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getSchmType() {
        return schmType;
    }

    public void setSchmType(String schmType) {
        this.schmType = schmType;
    }

    public String getSchmCode() {
        return schmCode;
    }

    public void setSchmCode(String schmCode) {
        this.schmCode = schmCode;
    }

    public String getSolId() {
        return solId;
    }

    public void setSolId(String solId) {
        this.solId = solId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        if (!super.equals(o)) return false;

        Account account = (Account) o;

        if (!accountId.equals(account.accountId)) return false;
        if (!accountNumber.equals(account.accountNumber)) return false;
        if (!accountName.equals(account.accountName)) return false;
        if (!custId.equals(account.custId)) return false;
        if (!schmType.equals(account.schmType)) return false;
        if (!schmCode.equals(account.schmCode)) return false;
        return solId != null ? solId.equals(account.solId) : account.solId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + accountId.hashCode();
        result = 31 * result + accountNumber.hashCode();
        result = 31 * result + accountName.hashCode();
        result = 31 * result + custId.hashCode();
        result = 31 * result + schmType.hashCode();
        result = 31 * result + schmCode.hashCode();
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
                + ",                         \"custId\":\"" + custId + "\""
                + ",                         \"schmType\":\"" + schmType + "\""
                + ",                         \"schmCode\":\"" + schmCode + "\""
                + ",                         \"solId\":\"" + solId + "\""
                + "}";
    }
}
