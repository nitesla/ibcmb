package longbridge.models;

import javax.persistence.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Entity
public class Account extends AbstractEntity{

<<<<<<< HEAD
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
=======
>>>>>>> 93ae8a1f5235023912f9e0c871393e5770fea1ae
    private String accountNumber;
    private String accountName;
    private String accountType;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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
