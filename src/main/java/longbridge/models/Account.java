package longbridge.models;

import javax.persistence.*;

/**
 *
 */
@Entity
public class Account extends AbstractEntity{

    private String accountNumber;
    private String accountName;
    private String accountType;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
