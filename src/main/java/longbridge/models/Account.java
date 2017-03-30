package longbridge.models;

import javax.persistence.*;
import java.util.Collection;

/**
 *
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Account extends AbstractEntity{


    private String accountNumber;
    private String accountName;
    private String accountType;

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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }


}
