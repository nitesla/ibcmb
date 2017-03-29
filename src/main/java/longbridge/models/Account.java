package longbridge.models;

import javax.persistence.*;

/**
 * Created by Showboy on 27/03/2017.
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
